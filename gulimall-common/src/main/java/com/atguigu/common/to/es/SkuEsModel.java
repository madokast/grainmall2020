package com.atguigu.common.to.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;


/**
 * Description
 * SkuEsModel
 * sku 在 es 里面保存的数据模型
 *
 * <p>
 * Data
 * 2020/6/13-23:52
 *
 * @author zrx
 * @version 1.0
 */

public class SkuEsModel {
    private final static Logger LOGGER = LoggerFactory.getLogger(SkuEsModel.class);

    /**
     * "skuId": {
     * "type": "long"
     * },
     */
    private Long skuId;

    /**
     * "spuId": { // 数据折叠功能？所以是 keyword
     * "type": "keyword"
     * },
     */
    private Long spuId;

    /**
     * "skuTitle": { // 只保存主标题，因为副标题在结果页不展示
     * "type": "text",
     * "analyzer": "ik_smart"
     * },
     */
    private String skuTitle;


    /**
     * "skuPrice": { // 为了防止精度
     * "type": "keyword"
     * },
     */
    private BigDecimal skuPrice;

    /**
     * "skuImg": { // 默认显示图片
     * "type": "keyword",
     * "index": false, // 不用于检索
     * "doc_values": false // 不用于聚合
     * },
     */
    private String skuImg;

    /**
     * "saleCount": { // 销量
     * "type": "long"
     * },
     */
    private Long saleCount;

    /**
     * "hasStock": { // 是否有库存。不存库存具体量
     * "type": "boolean"
     * },
     */
    private Boolean hasStock;

    /**
     * "hotScore": { // 热度评分，即商品的"综合"排序
     * "type": "long"
     * },
     */
    private Long hotScore;


    /**
     * "brandId": {
     * "type": "long"
     * },
     */
    private Long brandId;

    /**
     * "catalogId": {
     * "type": "long"
     * },
     */
    private Long catalogId;

    /**
     * "brandName": { //品牌名
     * "type": "keyword",
     * "index": false,
     * "doc_values": false
     * },
     */
    private String brandName;

    /**
     * "brandImg": { // 品牌图片
     * "type": "keyword",
     * "index": false,
     * "doc_values": false
     * },
     */
    private String brandImg;

    /**
     * "catalogName": { // 三级分类名
     * "type": "keyword",
     * "index": false,
     * "doc_values": false
     * },
     */
    private String catalogName;

    /**
     * "attrs": { // 属性集合
     * "type": "nested", // 嵌套，不要扁平化处理
     * "properties": {
     * "attrId": {
     * "type": "long"
     * },
     * "attrName": { // 属性名不用检索，如 CPU型号、内存
     * "type": "keyword",
     * "index": false,
     * "doc_values": false
     * },
     * "attrValue": { // 属性值用于 keyword 检索，如 8G 12G 16G
     * "type": "keyword"
     */
    private List<Attrs> attrs;

    public  static class Attrs {

        private Long attrId;

        private String attrName;

        private String attrValue;

        @Override
        public String toString() {
            return "Attrs{" +
                    "attrId=" + attrId +
                    ", attrName='" + attrName + '\'' +
                    ", attrValue='" + attrValue + '\'' +
                    '}';
        }

        public Long getAttrId() {
            return attrId;
        }

        public void setAttrId(Long attrId) {
            this.attrId = attrId;
        }

        public String getAttrName() {
            return attrName;
        }

        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }

        public String getAttrValue() {
            return attrValue;
        }

        public void setAttrValue(String attrValue) {
            this.attrValue = attrValue;
        }
    }

    @Override
    public String toString() {
        return "SkuEsModel{" +
                "skuId=" + skuId +
                ", spuId=" + spuId +
                ", skuTitle='" + skuTitle + '\'' +
                ", skuPrice=" + skuPrice +
                ", skuImg='" + skuImg + '\'' +
                ", saleCount=" + saleCount +
                ", hasStock=" + hasStock +
                ", hotScore=" + hotScore +
                ", brandId=" + brandId +
                ", catalogId=" + catalogId +
                ", brandName='" + brandName + '\'' +
                ", brandImg='" + brandImg + '\'' +
                ", catalogName='" + catalogName + '\'' +
                ", attrs=" + attrs +
                '}';
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public String getSkuTitle() {
        return skuTitle;
    }

    public void setSkuTitle(String skuTitle) {
        this.skuTitle = skuTitle;
    }

    public BigDecimal getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(BigDecimal skuPrice) {
        this.skuPrice = skuPrice;
    }

    public String getSkuImg() {
        return skuImg;
    }

    public void setSkuImg(String skuImg) {
        this.skuImg = skuImg;
    }

    public Long getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Long saleCount) {
        this.saleCount = saleCount;
    }

    public Boolean getHasStock() {
        return hasStock;
    }

    public void setHasStock(Boolean hasStock) {
        this.hasStock = hasStock;
    }

    public Long getHotScore() {
        return hotScore;
    }

    public void setHotScore(Long hotScore) {
        this.hotScore = hotScore;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandImg() {
        return brandImg;
    }

    public void setBrandImg(String brandImg) {
        this.brandImg = brandImg;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public List<Attrs> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Attrs> attrs) {
        this.attrs = attrs;
    }
}




