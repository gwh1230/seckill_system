package com.gwh.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gwh.seckill.pojo.Order;
import com.gwh.seckill.pojo.User;
import com.gwh.seckill.vo.GoodsVo;
import com.gwh.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2021-12-11
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     */
    Order secKill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    /**
     * 获取秒杀地址
     */
    String createPath(User user, Long goodsId);

    Boolean checkPath(User user, Long goodsId, String path);
}
