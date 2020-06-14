package com.atguigu.gulimall.ware.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 * 是否有库存
 * <p>
 * Data
 * 2020/6/14-10:25
 *
 * @author zrx
 * @version 1.0
 */

public class SkuHasStockVo {
    private final static Logger LOGGER = LoggerFactory.getLogger(SkuHasStockVo.class);

    private Long skuId;

    private Boolean hasStock;

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getHasStock() {
        return hasStock;
    }

    public void setHasStock(Boolean hasStock) {
        this.hasStock = hasStock;
    }

    @Override
    public String toString() {
        return "SkuHasStockVo{" +
                "skuId=" + skuId +
                ", hasStock=" + hasStock +
                '}';
    }
}
