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


    /**
     * index 结构：
     * <pre>
     *     PUT product
     * {
     *   "mappings": {
     *     "properties": {
     *       "skuId": {
     *         "type": "long"
     *       },
     *       "spuId": {
     *         "type": "keyword"
     *       },
     *       "skuTitle": {
     *         "type": "text",
     *         "analyzer": "ik_smart"
     *       },
     *       "skuPrice": {
     *         "type": "keyword"
     *       },
     *       "skuImg": {
     *         "type": "keyword",
     *         "index": false,
     *         "doc_values": false
     *       },
     *       "saleCount": {
     *         "type": "long"
     *       },
     *       "hasStock": {
     *         "type": "boolean"
     *       },
     *       "hotScore": {
     *         "type": "long"
     *       },
     *       "brandId": {
     *         "type": "long"
     *       },
     *       "catalogId": {
     *         "type": "long"
     *       },
     *       "brandName": {
     *         "type": "keyword",
     *         "index": false,
     *         "doc_values": false
     *       },
     *       "brandImg": {
     *         "type": "keyword",
     *         "index": false,
     *         "doc_values": false
     *       },
     *       "catalogName": {
     *         "type": "keyword",
     *         "index": false,
     *         "doc_values": false
     *       },
     *       "attrs": {
     *         "type": "nested",
     *         "properties": {
     *           "attrId": {
     *             "type": "long"
     *           },
     *           "attrName": {
     *             "type": "keyword",
     *             "index": false,
     *             "doc_values": false
     *           },
     *           "attrValue": {
     *             "type": "keyword"
     *           }
     *         }
     *       }
     *     }
     *   }
     * }
     * </pre>
     */
    public static final String PRODUCT_INDEX = "product"; // sku 数据在 es 中的 index
}
