package com.atguigu.gulimall.product.config;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyRedissonConfigTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(MyRedissonConfigTest.class);

    @Autowired
    RedissonClient client;

    @Test
    public void test() {
        LOGGER.info(client.toString());
    }


    int s = 0;

    @Test
    public void lockTest() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    RLock lock = client.getLock("测试锁01");
                    lock.lock();
                    try {
                        s++;
                        LOGGER.info(Thread.currentThread().getName() + "--" + s);
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        pool.shutdown();

        pool.awaitTermination(1, TimeUnit.MINUTES);
    }

}