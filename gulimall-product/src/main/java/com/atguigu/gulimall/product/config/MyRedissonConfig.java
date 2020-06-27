package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Description
 * redisson
 * <p>
 * Data
 * 2020/6/23-13:28
 *
 * @author zrx
 * @version 1.0
 */

@Configuration
public class MyRedissonConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(MyRedissonConfig.class);

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.2.3:26379");
        return Redisson.create(config);
    }
}
