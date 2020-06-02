package com.atguigu.gulimall.product.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 * BrandVo
 * 专用于下面方法的返回
 * @see  com.atguigu.gulimall.product.controller.CategoryBrandRelationController#relationBrandList
 * <p>
 * Data
 * 2020/6/2-20:38
 *
 * @author zrx
 * @version 1.0
 */

public class BrandVo {
    private final static Logger LOGGER = LoggerFactory.getLogger(BrandVo.class);

    private Long brandId;

    private String brandName;

    @Override
    public String toString() {
        return "BrandVo{" +
                "brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                '}';
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
