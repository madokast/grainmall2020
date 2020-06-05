package com.atguigu.common.to;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Description
 * SkuReductionTo
 * sku 优惠信息
 * <p>
 * Data
 * 2020/6/5-21:22
 *
 * @author zrx
 * @version 1.0
 */

public class SkuReductionTo implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(SkuReductionTo.class);

    private Long skuId;

    private int fullCount;

    private BigDecimal discount;

    private int countStatus;

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private int priceStatus;

    private List<MemberPriceTo> memberPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    @Override
    public String toString() {
        return "SkuReductionTo{" +
                "skuId=" + skuId +
                ", fullCount=" + fullCount +
                ", discount=" + discount +
                ", countStatus=" + countStatus +
                ", fullPrice=" + fullPrice +
                ", reducePrice=" + reducePrice +
                ", priceStatus=" + priceStatus +
                ", memberPrice=" + memberPrice +
                '}';
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public int getFullCount() {
        return fullCount;
    }

    public void setFullCount(int fullCount) {
        this.fullCount = fullCount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getCountStatus() {
        return countStatus;
    }

    public void setCountStatus(int countStatus) {
        this.countStatus = countStatus;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public BigDecimal getReducePrice() {
        return reducePrice;
    }

    public void setReducePrice(BigDecimal reducePrice) {
        this.reducePrice = reducePrice;
    }

    public int getPriceStatus() {
        return priceStatus;
    }

    public void setPriceStatus(int priceStatus) {
        this.priceStatus = priceStatus;
    }

    public List<MemberPriceTo> getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(List<MemberPriceTo> memberPrice) {
        this.memberPrice = memberPrice;
    }
}
