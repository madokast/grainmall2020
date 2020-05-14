package com.atguigu.gulimall.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Description
 * 允许跨域，添加相关请求头
 * <p>
 * Data
 * 2020/5/7-23:54
 *
 * @author zrx
 * @version 1.0
 */

@Configuration
public class GulimallCorsConfiguration {
    private final static Logger LOGGER = LoggerFactory.getLogger(GulimallCorsConfiguration.class);

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
                new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration =
                new org.springframework.web.cors.CorsConfiguration();

        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);

        urlBasedCorsConfigurationSource.registerCorsConfiguration(
                "/**", //任意路径都跨域
                corsConfiguration
        );

        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
