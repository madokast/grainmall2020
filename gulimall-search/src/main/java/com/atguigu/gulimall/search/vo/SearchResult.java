package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description
 * SearchResponse
 * 封装查询结果
 * <p>
 * Data
 * 2020/7/26-23:57
 *
 * @author zrx
 * @version 1.0
 */

public class SearchResult {
    private final static Logger LOGGER = LoggerFactory.getLogger(SearchResult.class);

    // 商品信息
    private List<SkuEsModel> products;

    // 页码
    private Integer pageNum;

    // 总记录数
    private Long total;

    // 总页码
    private Integer totalPages;

    // 展示的筛选信息
    // 当前查询到的结果，涉及到的所有品牌
    private List<BrandVo> brands;

    // 涉及到的属性信息
    private List<AttrVo> attrs;

    // 涉及到的三级分类
    private List<CatalogVo> catalogs;

    //========================== get set


    @Override
    public String toString() {
        return "SearchResult{" +
                "products=" + products +
                ", pageNum=" + pageNum +
                ", total=" + total +
                ", totalPages=" + totalPages +
                ", brands=" + brands +
                ", attrs=" + attrs +
                ", catalogs=" + catalogs +
                '}';
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public List<SkuEsModel> getProducts() {
        return products;
    }

    public void setProducts(List<SkuEsModel> products) {
        this.products = products;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<BrandVo> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandVo> brands) {
        this.brands = brands;
    }

    public List<AttrVo> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<AttrVo> attrs) {
        this.attrs = attrs;
    }

    public List<CatalogVo> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<CatalogVo> catalogs) {
        this.catalogs = catalogs;
    }

    //========================== inner class
    public static class CatalogVo{
        /**
         * "catalogId": {
         * "type": "long"
         * },
         */
        private Long catalogId;

        /**
         * "catalogName": { // 三级分类名
         * "type": "keyword",
         * "index": false,
         * "doc_values": false
         * },
         */
        private String catalogName;

        public Long getCatalogId() {
            return catalogId;
        }

        public void setCatalogId(Long catalogId) {
            this.catalogId = catalogId;
        }

        public String getCatalogName() {
            return catalogName;
        }

        public void setCatalogName(String catalogName) {
            this.catalogName = catalogName;
        }

        @Override
        public String toString() {
            return "CatalogVo{" +
                    "catalogId=" + catalogId +
                    ", catalogName='" + catalogName + '\'' +
                    '}';
        }
    }

    public static class AttrVo{
        // id name values

        private Long attrId;

        private String attrName;

        private List<String> attrValue;

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

        public List<String> getAttrValue() {
            return attrValue;
        }

        public void setAttrValue(List<String> attrValue) {
            this.attrValue = attrValue;
        }

        @Override
        public String toString() {
            return "AttrVo{" +
                    "attrId=" + attrId +
                    ", attrName='" + attrName + '\'' +
                    ", attrValue=" + attrValue +
                    '}';
        }
    }

    public static class BrandVo{
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
         * "brandId": {
         * "type": "long"
         * },
         */
        private Long brandId;

        @Override
        public String toString() {
            return "BrandVo{" +
                    "brandName='" + brandName + '\'' +
                    ", brandImg='" + brandImg + '\'' +
                    ", brandId=" + brandId +
                    '}';
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

        public Long getBrandId() {
            return brandId;
        }

        public void setBrandId(Long brandId) {
            this.brandId = brandId;
        }
    }
}
