package com.gwh.seckill.controller;

import com.gwh.seckill.rabbitmq.MQSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 不是秒杀的类，用于测试RabbitMQ及其交换机模式
 */
@Controller
@RequestMapping("/rabbit")
public class RabbitMQController {

    @Autowired
    private MQSenderTest mqSenderTest;

    /**
     * RabbitMQ第一个demo
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSenderTest.send("Hello RabbitMQ!");
    }

    /**
     * fanout交换机
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mqFanout() {
        mqSenderTest.sendFanout("Hello Fanout!");
    }

    /**
     * direct交换机
     */
    @RequestMapping("/mq/direct/red")
    @ResponseBody
    public void mqDirectRed() {
        mqSenderTest.sendDirect01("Hello Direct red!");
    }

    @RequestMapping("/mq/direct/green")
    @ResponseBody
    public void mqDirectGreen() {
        mqSenderTest.sendDirect02("Hello Direct green!");
    }

    /**
     * topic交换机
     */
    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void mqTopic01(){
        mqSenderTest.sendTopic01("Hello Topic aaa");
    }

    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void mqTopic02(){
        mqSenderTest.sendTopic02("Hello Topic bbb");
    }

    /**
     * headers交换机
     */
    @RequestMapping("/mq/headers01")
    @ResponseBody
    public void mqHeaders01(){
        mqSenderTest.sendHeaders01("Hello Header aaa");
    }

    @RequestMapping("/mq/headers02")
    @ResponseBody
    public void mqHeaders02(){
        mqSenderTest.sendHeaders02("Hello Header bbb");
    }
}
