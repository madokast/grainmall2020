package com.atguigu.common.to;

import com.baomidou.mybatisplus.annotation.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description
 * TO -> 微服务间传输数据
 * <p>
 * Data
 * 2020/6/5-21:09
 *
 * @author zrx
 * @version 1.0
 */

public class SpuBoundsTo implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(SpuBoundsTo.class);

    /**
     * id
     */
    private Long spuId;


    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;

    @Override
    public String toString() {
        return "SpuBoundsTo{" +
                "spuId=" + spuId +
                ", growBounds=" + growBounds +
                ", buyBounds=" + buyBounds +
                '}';
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public BigDecimal getGrowBounds() {
        return growBounds;
    }

    public void setGrowBounds(BigDecimal growBounds) {
        this.growBounds = growBounds;
    }

    public BigDecimal getBuyBounds() {
        return buyBounds;
    }

    public void setBuyBounds(BigDecimal buyBounds) {
        this.buyBounds = buyBounds;
    }
}
