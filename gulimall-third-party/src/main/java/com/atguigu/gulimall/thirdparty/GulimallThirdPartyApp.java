package com.atguigu.gulimall.thirdparty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.sql.DataSource;

/**
 * Description
 * GulimallThirdPartyApp
 * <p>
 * Data
 * 2020/5/17-10:23
 *
 * @author zrx
 * @version 1.0
 */

@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallThirdPartyApp {
    private final static Logger LOGGER = LoggerFactory.getLogger(GulimallThirdPartyApp.class);

    public static void main(String[] args) {
        SpringApplication.run(GulimallThirdPartyApp.class, args);
    }
}
