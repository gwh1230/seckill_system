package com.gwh.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀系统RabbitMQ配置类
 */
@Configuration
public class RabbitMQTopicConfig {

    private static final String SECKILL_QUEUE = "seckillQueue";
    private static final String SECKILL_EXCHANGE = "seckillExchange";

    @Bean
    public Queue seckillQueue() {
        return new Queue(SECKILL_QUEUE);
    }

    @Bean
    public TopicExchange seckillTopicExchange() {
        return new TopicExchange(SECKILL_EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(seckillQueue()).to(seckillTopicExchange()).with("seckill.#");
    }
}
