package com.gwh.seckill.controller;


import com.gwh.seckill.pojo.User;
import com.gwh.seckill.rabbitmq.MQSenderTest;
import com.gwh.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2021-12-08
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSenderTest mqSenderTest;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

}
