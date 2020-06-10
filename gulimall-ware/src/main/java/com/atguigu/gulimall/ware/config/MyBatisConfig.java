package com.atguigu.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Description
 * MyBatisConfig
 * <p>
 * Data
 * 2020/5/31-19:18
 *
 * @author zrx
 * @version 1.0
 */

@Configuration
@EnableTransactionManagement // 开启事务
@MapperScan("com.atguigu.gulimall.ware.dao")
public class MyBatisConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(MyBatisConfig.class);


    // 引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptorn() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        // 设置请求的页 大于最大页的操作，true返回首页，false 继续操作
        paginationInterceptor.setOverflow(true);

        // 设置页内最大项数
        paginationInterceptor.setLimit(100);

        return paginationInterceptor;
    }
}
