package com.atguigu.gulimall.search.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description
 * 封装检索条件
 * <p>
 * <pre>
 *     POST /product/_search
 * {
 *   "query": {
 *     "bool": {
 *       "must": [ // 模糊查询
 *         {
 *           "match": {
 *             "skuTitle": "华为"
 *           }
 *         }
 *       ],
 *       "filter": [
 *         {
 *           "term": { // 分类
 *             "catalogId": "225"
 *           }
 *         },
 *         {
 *           "terms": {
 *             "brandId": [ // 品牌
 *               "1",
 *               "2",
 *               "9"
 *             ]
 *           }
 *         },
 *         {
 *           "nested": { // 属性1
 *             "path": "attrs",
 *             "query": {
 *               "bool": {
 *                 "must": [
 *                   {
 *                     "term": {
 *                       "attrs.attrId": {
 *                         "value": "10"
 *                       }
 *                     }
 *                   },
 *                   {
 *                     "terms": {
 *                       "attrs.attrValue": [
 *                         "玻璃",
 *                         "金属"
 *                       ]
 *                     }
 *                   }
 *                 ]
 *               }
 *             }
 *           }
 *         },
 *         {
 *           "nested": { //属性 2
 *             "path": "attrs",
 *             "query": {
 *               "bool": {
 *                 "must": [
 *                   {
 *                     "term": {
 *                       "attrs.attrId": {
 *                         "value": "8"
 *                       }
 *                     }
 *                   },
 *                   {
 *                     "terms": {
 *                       "attrs.attrValue": [
 *                         "麒麟990",
 *                         "麒麟980"
 *                       ]
 *                     }
 *                   }
 *                 ]
 *               }
 *             }
 *           }
 *         },
 *         {
 *           "term": { // 是否有货
 *             "hasStock": "false"
 *           }
 *         },
 *         {
 *           "range": { // 价格区间
 *             "skuPrice": {
 *               "gte": 0,
 *               "lte": 6000
 *             }
 *           }
 *         }
 *       ]
 *     }
 *   },
 *   "sort": [
 *     {
 *       "skuPrice": { // 排序
 *         "order": "desc"
 *       }
 *     }
 *   ],
 *   "from": 0,
 *   "size": 5, // 分页
 *   "highlight": { // 高亮
 *     "fields": {
 *       "skuTitle": {
 *         "pre_tags": "<b style='color:red'>",
 *         "post_tags": "</b>"
 *       }
 *     }
 *   }
 * }
 * </pre>
 * Data
 * 2020/7/26-23:29
 *
 * @author zrx
 * @version 1.0
 */

public class SearchParam {
    private final static Logger LOGGER = LoggerFactory.getLogger(SearchParam.class);


    private String keyword; // 全文检索 skuTitle


    //  排序条件
    private String sort; // saleCount_asc销量   hotScore_desc热度 ...   skuPrice价格


    // 过滤条件
    private Integer hasStock; // 是否有货  0 / 1
    private String skuPrice; // 价格 范围  100_500   _500  100_
    private List<Long> brandId; //  品牌 id   ?brandId=1&brandId=2..
    private Long catalog3Id; // 三级分类 ?catalog3Id=1&catalog3Id=2
    private List<String> attrs; // 属性  ?attrs=1_其他:安卓&attrs=2_5寸  表示1号属性，值是安卓或其他

    //分页属性
    private Integer pageNum = 1;


    @Override
    public String toString() {
        return "SearchParam{" +
                "keyword='" + keyword + '\'' +
                ", sort='" + sort + '\'' +
                ", hasStock=" + hasStock +
                ", skuPrice='" + skuPrice + '\'' +
                ", brandId=" + brandId +
                ", catalog3Id=" + catalog3Id +
                ", attrs=" + attrs +
                ", pageNum=" + pageNum +
                '}';
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getHasStock() {
        return hasStock;
    }

    public void setHasStock(Integer hasStock) {
        this.hasStock = hasStock;
    }

    public String getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice) {
        this.skuPrice = skuPrice;
    }

    public List<Long> getBrandId() {
        return brandId;
    }

    public void setBrandId(List<Long> brandId) {
        this.brandId = brandId;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public List<String> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<String> attrs) {
        this.attrs = attrs;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
