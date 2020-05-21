package com.atguigu.gulimall.thirdparty;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

/**
 * Description
 * RemoteConfigTest
 * <p>
 * Data
 * 2020/5/17-16:13
 *
 * @author zrx
 * @version 1.0
 */

@SpringBootTest
public class RemoteConfigTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteConfigTest.class);

    //#spring:
    //#  cloud:
    //#   alicloud:
    //#     access-key: XXX
    //#     secret-key: XXX
    //#     oss:
    //#       endpoint: oss-cn-shenzhen.aliyuncs.com
    //#       bucket-name: gulimall-madokast
    @Value("${spring.cloud.alicloud.access-key}")
    private String key;

    @Test
    public void TestRemoteConfig(){
        LOGGER.info("key = {}", key);
        Assert.notNull(key,"远程配置读取失败");
    }
}
