package com.gwh.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试类，不属于秒杀系统
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    /**
     * first demo
     */
    @Bean
    public Queue queue() {
        return new Queue("queue", true);
    }

    /**
     * 测试fanout交换机
     */
    public static final String QUEUE01_FANOUT = "queue_fanout01";
    public static final String QUEUE02_FANOUT = "queue_fanout02";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";

    @Bean
    public Queue fanoutQueue01() {
        return new Queue(QUEUE01_FANOUT);
    }

    @Bean
    public Queue fanoutQueue02() {
        return new Queue(QUEUE02_FANOUT);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutbBinding01() {
        return BindingBuilder.bind(fanoutQueue01()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding02() {
        return BindingBuilder.bind(fanoutQueue02()).to(fanoutExchange());
    }


    /**
     * 测试direct交换机
     */
    public static final String QUEUE01_DIRECT = "queue_direct01";
    public static final String QUEUE02_DIRECT = "queue_direct02";
    public static final String DIRECT_EXCHANGE = "direct_exchange";
    // 路由键
    public static final String ROUTINGKEY_RED = "queue.red";
    public static final String ROUTINGKEY_GREEN = "queue.green";

    @Bean
    public Queue queueDirect01() {
        return new Queue(QUEUE01_DIRECT);
    }

    @Bean
    public Queue queueDirect02() {
        return new Queue(QUEUE02_DIRECT);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public Binding directBinding01() {
        return BindingBuilder.bind(queueDirect01()).to(directExchange()).with(ROUTINGKEY_RED);
    }

    @Bean
    public Binding directBinding02() {
        return BindingBuilder.bind(queueDirect02()).to(directExchange()).with(ROUTINGKEY_GREEN);
    }

    /**
     * 测试topic交换机
     */
    public static final String QUEUE_TOPIC01 = "queue_topic01";
    public static final String QUEUE_TOPIC02 = "queue_topic02";
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    // #匹配一个单词或多个，*匹配1个
    public static final String TOPIC_ROUTINGKEY01 = "#.queue.#";
    public static final String TOPIC_ROUTINGKEY02 = "*.queue.#";

    @Bean
    public Queue queueTopic01() {
        return new Queue(QUEUE_TOPIC01);
    }

    @Bean
    public Queue queueTopic02() {
        return new Queue(QUEUE_TOPIC02);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding topicBinding01() {
        return BindingBuilder.bind(queueTopic01()).to(topicExchange()).with(TOPIC_ROUTINGKEY01);
    }

    @Bean
    public Binding topicBinding02() {
        return BindingBuilder.bind(queueTopic02()).to(topicExchange()).with(TOPIC_ROUTINGKEY02);
    }

    /**
     * headers模式
     */
    public static final String QUEUE_HEADERS01 = "queue_headers01";
    public static final String QUEUE_HEADERS02 = "queue_headers02";
    public static final String HEADERS_EXCHANGE = "headers_exchange";

    @Bean
    public Queue queueHeaders01() {
        return new Queue(QUEUE_HEADERS01);
    }

    @Bean
    public Queue queueHeaders02() {
        return new Queue(QUEUE_HEADERS02);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Binding headersBinding01() {
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "low");
        return BindingBuilder.bind(queueHeaders01()).to(headersExchange()).whereAny(map).match();
    }

    @Bean
    public Binding headersBinding02() {
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "fast");
        return BindingBuilder.bind(queueHeaders02()).to(headersExchange()).whereAll(map).match();
    }

}
