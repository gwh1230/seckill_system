package com.gwh.seckill.controller;


import com.gwh.seckill.pojo.Order;
import com.gwh.seckill.pojo.User;
import com.gwh.seckill.service.IOrderService;
import com.gwh.seckill.vo.OrderDetailVo;
import com.gwh.seckill.vo.RespBean;
import com.gwh.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jobob
 * @since 2021-12-11
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 订单详情
     *
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detailVo = orderService.detail(orderId);
        return RespBean.success(detailVo);
    }
}
