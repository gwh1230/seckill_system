package com.gwh.seckill.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwh.seckill.pojo.SeckillMessage;
import com.gwh.seckill.pojo.SeckillOrder;
import com.gwh.seckill.pojo.User;
import com.gwh.seckill.service.IGoodsService;
import com.gwh.seckill.service.IOrderService;
import com.gwh.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IOrderService orderService;

    /**
     * 下单操作
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("接收消息：" + message);
        SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);
        Long goodId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodId);
        // 库存不足直接return
        if (goodsVo.getStockCount() < 1) {
            return;
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder
                = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodId);
        if (null != seckillOrder) {
            return;
        }
        // 下单
        orderService.secKill(user, goodsVo);
    }
}
