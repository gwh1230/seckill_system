package com.gwh.seckill.rabbitmq;

import com.gwh.seckill.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiverTest {
    /**
     * first demo
     */
    @RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接受消息：" + msg);
    }

    /**
     * Fanout交换机
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE01_FANOUT)
    public void receiveFanout01(Object msg) {
        log.info("QUEUE01接收消息：" + msg);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE02_FANOUT)
    public void receiveFanout02(Object msg) {
        log.info("QUEUE02接收消息：" + msg);
    }

    /**
     * Direct交换机
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE01_DIRECT)
    public void receiveDirect01(Object msg) {
        log.info("Queue red接收消息：" + msg);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE02_DIRECT)
    public void receiveDirect02(Object msg) {
        log.info("Queue green接收消息：" + msg);

    }

    /**
     * Topic交换机
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_TOPIC01)
    public void receiveTopic01(Object msg) {
        log.info("QUEUE_TOPIC01接收消息：" + msg);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TOPIC02)
    public void receiveTopic02(Object msg) {
        log.info("QUEUE_TOPIC02接收消息：" + msg);
    }

    /**
     * Headers交换机
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_HEADERS01)
    public void receiveHeaders01(Message message) {
        log.info("QUEUE01接收Messgae对象：" + message);
        log.info("QUEUE01接收消息：" + new String(message.getBody()));
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_HEADERS02)
    public void receiveHeaders02(Message message) {
        log.info("QUEUE02接收Message对象：" + message);
        log.info("QUEUE02接收消息：" + new String(message.getBody()));
    }

}
