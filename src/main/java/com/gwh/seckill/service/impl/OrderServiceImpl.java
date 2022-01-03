package com.gwh.seckill.service.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gwh.seckill.exception.GlobalException;
import com.gwh.seckill.mapper.OrderMapper;
import com.gwh.seckill.pojo.Order;
import com.gwh.seckill.pojo.SeckillGoods;
import com.gwh.seckill.pojo.SeckillOrder;
import com.gwh.seckill.pojo.User;
import com.gwh.seckill.service.IGoodsService;
import com.gwh.seckill.service.IOrderService;
import com.gwh.seckill.service.ISeckillGoodsService;
import com.gwh.seckill.service.ISeckillOrderService;
import com.gwh.seckill.utils.MD5Util;
import com.gwh.seckill.utils.UUIDUtil;
import com.gwh.seckill.vo.GoodsVo;
import com.gwh.seckill.vo.OrderDetailVo;
import com.gwh.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.security.provider.MD5;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2021-12-11
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Resource
    private OrderMapper orderMapper;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    public Order secKill(User user, GoodsVo goods) {
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(
                new QueryWrapper<SeckillGoods>()
                        .eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        // 库存大于0才可以秒杀
        boolean seckillGoodsResutl = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count-1")
                .eq("goods_id", goods.getId())
                .gt("stock_count", 0));
        if (seckillGoods.getStockCount() < 1) {
            // 判断是否还有库存
            redisTemplate.opsForValue().set("isStockEmpty:" + goods.getId(), true);
            return null;
        }
        if (!seckillGoodsResutl) {
            return null;
        }

        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());

        // 防止并发情况下，同一个用户触发多次秒杀，出现主键冲突异常
        try {
            seckillOrderService.save(seckillOrder);
        } catch (DuplicateKeyException exception) {
            throw new GlobalException(RespBeanEnum.REPEATE_ERROR);
        }
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);
        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (null == orderId) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        return new OrderDetailVo(order, goodsVo);
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public Boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);

    }

    @Override
    public Boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (StringUtils.isEmpty(captcha) || null == user || goodsId < 0) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
