package com.atguigu.gulimall.product;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Description
 * RedisTest
 * <p>
 * Data
 * 2020/6/15-17:47
 *
 * @author zrx
 * @version 1.0
 */

@SpringBootTest
public class RedisTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisTest.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void setTest(){
        //stringRedisTemplate.opsForValue().set("hello","world");
    }

    @Test
    public void getTest(){
//        String hello = stringRedisTemplate.opsForValue().get("hello");
//        LOGGER.info("hello = {}", hello);

//        stringRedisTemplate.delete()

//        stringRedisTemplate.opsForValue().setIfAbsent()
    }


}
