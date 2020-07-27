package com.atguigu.gulimall.search.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 * 常量
 * <p>
 * Data
 * 2020/6/14-11:10
 *
 * @author zrx
 * @version 1.0
 */

public class EsConstant {
    private final static Logger LOGGER = LoggerFactory.getLogger(EsConstant.class);


    // 2020年7月27日  修改
    public static final String PRODUCT_INDEX = "gulimall_product"; // sku 数据在 es 中的 index

    // 每页显示数
    public static final Integer PAGE_SIZE = 4;
}
