package com.atguigu.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;


@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
// exclude 数据源，否则因为找不到数据库配置而出错，也可以在pom中去除相关依赖
public class GulimallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallGatewayApplication.class, args);
    }

//
//    //@Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder routeBuilder){
//        return routeBuilder.routes()
//                .route("renren-fast", r -> { //  人人台管理
//                    return r.host("/api/**")
//                            .filters(f -> f.rewritePath("/api/(?<segment>.*)","/renren-fast/${segment}"))
//                            .uri("http://192.168.2.13:28080");
//                })
//                .build();
//
//        //#    gateway:
//        //#      routes:
//        //#        # renren-fast 后台管理路由
//        //#        - id: admin_route
//        //#          uri: lb://renren-fast
//        //#          predicates:
//        //#            - Path=/api/**
//        //#          filters:
//        //#            - RewritePath=/api/(?<segment>.*),/renren-fast/$\{segment}
//
//        // return builder.routes()
//        //        .route("circuitbreaker_route", r -> r.path("/consumingServiceEndpoint")
//        //            .filters(f -> f.circuitBreaker(c -> c.name("myCircuitBreaker").fallbackUri("forward:/inCaseOfFailureUseThis"))
//        //                .rewritePath("/consumingServiceEndpoint", "/backingServiceEndpoint")).uri("lb://backing-service:8088")
//        //        .build();
//    }
}
