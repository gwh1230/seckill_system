package com.gwh.seckill;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeckillDemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * redis的lua脚本
     */
    @Autowired
    private RedisScript<Boolean> testScript;

    @Test
    public void testLock01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 占位，如果key不存在才可以设置成功
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");
        // 如果占位成功，进行正常操作
        if (isLock) {
            valueOperations.set("name", "gwh");
            String name = (String) valueOperations.get("name");
            System.out.println("name=" + name);
            // 操作结束，删除锁
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程在使用，请稍后");
        }
    }

    @Test
    public void testLock02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 占位，如果key不存在才可以设置成功
        // 设置锁的超时时间，到期自动删除，防止出现异常，锁无法正常释放
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        // 如果占位成功，进行正常操作
        if (isLock) {
            valueOperations.set("name", "gwh");
            String name = (String) valueOperations.get("name");
            System.out.println("name=" + name);
            // 此处出现异常，不会删除锁
            Integer.parseInt("aaaa");
            // 操作结束，删除锁
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程在使用，请稍后");
        }
    }

    /**
     * lua脚本实现锁
     */
    @Test
    public void testLock03() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1", value, 5, TimeUnit.SECONDS);
        if (isLock) {
            valueOperations.set("name", "gwh");
            String name = (String) valueOperations.get("name");
            System.out.println("name=" + name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(testScript, Collections.singletonList("k1"), value);
            System.out.println(result);
        }else {
            System.out.println("有线程在使用，请稍后");
        }
    }

}
