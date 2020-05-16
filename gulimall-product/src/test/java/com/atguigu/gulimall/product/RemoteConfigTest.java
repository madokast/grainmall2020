package com.atguigu.gulimall.product;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Description
 * RemoteConfigTest
 * <p>
 * Data
 * 2020/5/16-23:44
 *
 * @author zrx
 * @version 1.0
 */

@SpringBootTest
public class RemoteConfigTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteConfigTest.class);

    @Value("${server.port}")
    private String port;

    @Test
    public void injectTest(){
        LOGGER.info("port = {}", port);
    }
}
