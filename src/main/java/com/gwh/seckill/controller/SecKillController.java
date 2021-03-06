package com.gwh.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gwh.seckill.anno.AccessLimit;
import com.gwh.seckill.exception.GlobalException;
import com.gwh.seckill.pojo.Order;
import com.gwh.seckill.pojo.SeckillMessage;
import com.gwh.seckill.pojo.SeckillOrder;
import com.gwh.seckill.pojo.User;
import com.gwh.seckill.rabbitmq.MQSender;
import com.gwh.seckill.service.IGoodsService;
import com.gwh.seckill.service.IOrderService;
import com.gwh.seckill.service.ISeckillOrderService;
import com.gwh.seckill.vo.GoodsVo;
import com.gwh.seckill.vo.RespBean;
import com.gwh.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private RedisScript<Long> script;

    /**
     * key???goodsId,value?????????????????????
     */
    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    /**
     * ??????????????????????????????????????????
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        System.out.println("seckill2-" + goodsId + "-" + user.toString());
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        // ????????????
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckill_fail";
        }
        // ????????????????????????
        SeckillOrder seckillOrder = seckillOrderService.getOne(
                new QueryWrapper<SeckillOrder>()
                        .eq("user_id", user.getId())
                        .eq("goods_id", goodsId));
        if (null != seckillOrder) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckill_fail";
        }
        Order order = orderService.secKill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "order_detail";

    }


    /**
     * ??????
     * windows?????????QPS: 785
     * ?????????????????????QPS: 1356
     * ??????Redis???????????????RabbitMQ???QPS: 2454
     * ?????????????????????
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill2", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill2(@PathVariable String path, User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // ????????????
        Boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // ??????????????????????????????????????????Redis?????????
        if (emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // ??????????????????
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (null != seckillOrder) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        // ???????????????decrement()???????????????
//        Long stock = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        // ??????Redis lua??????????????????
        Long stock = redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId),
                Collections.EMPTY_LIST);
        if (stock < 0) {
            emptyStockMap.put(goodsId, true);
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));
        return RespBean.success(0);

        /*
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        // ????????????
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // ????????????????????????
//        SeckillOrder seckillOrder = seckillOrderService.getOne(
//                new QueryWrapper<SeckillOrder>()
//                        .eq("user_id", user.getId())
//                        .eq("goods_id", goodsId));
        // ??????redis?????????
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
        if (null != seckillOrder) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = orderService.secKill(user, goods);
        if (null == order) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        return RespBean.success(order);
         */
    }

    /**
     * ??????????????????
     *
     * @param user
     * @param goodsId
     * @return orderId:?????????-1???????????????, 0:?????????
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * ??????????????????
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
//        if (null == user) {
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
        // ???????????????????????????????????????????????????????????????5????????????5???
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        String uri = request.getRequestURI();
//        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
//        if (null == count) {
//            valueOperations.set(uri + ":" + user.getId(), 1, 5, TimeUnit.SECONDS);
//        } else if (count < 5) {
//            valueOperations.increment(uri + ":" + user.getId());
//        } else {
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REAHCED);
//        }
        // ?????????????????????????????????????????????

        Boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (null == user || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // ???????????????????????????????????????
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // ?????????????????????????????????Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(),
                300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("?????????????????????, {}", e.getMessage());
        }
    }

    /**
     * ??????????????????????????????????????????Redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(goodsVos)) {
            return;
        }
        goodsVos.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
