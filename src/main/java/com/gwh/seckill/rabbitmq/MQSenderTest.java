package com.gwh.seckill.rabbitmq;

import com.gwh.seckill.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSenderTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("queue", msg);
    }

    /**
     * 发送消息到Fanout交换机
     */
    public void sendFanout(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", msg);
    }

    /**
     * 发送消息到Direct交换机
     */
    public void sendDirect01(Object msg) {
        log.info("发送red消息：" + msg);
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTINGKEY_RED, msg);
    }

    public void sendDirect02(Object msg) {
        log.info("发送green消息：" + msg);
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTINGKEY_GREEN, msg);
    }

    /**
     * 发送消息到Topic交换机
     */
    public void sendTopic01(Object msg) {
        log.info("发送消息(queue_topic01接收)" + msg);
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, "queue.aaa.message", msg);
    }

    public void sendTopic02(Object msg) {
        log.info("发送消息(被两个queue接收)" + msg);
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, "message.queue.bbb.abc", msg);
    }

    /**
     * 发送消息到Headers交换机
     */
    public void sendHeaders01(String msg) {
        log.info("发送消息（被两个queue接收）：" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "fast");
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend(RabbitMQConfig.HEADERS_EXCHANGE, "", message);
    }

    public void sendHeaders02(String msg) {
        log.info("发送消息（被queue01接收）：" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "normal");
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend(RabbitMQConfig.HEADERS_EXCHANGE, "", message);
    }
}
