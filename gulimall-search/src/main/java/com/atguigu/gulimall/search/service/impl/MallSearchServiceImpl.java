package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.tomcat.util.security.Escape;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description
 * MallSearchServiceImpl
 * <p>
 * Data
 * 2020/7/26-23:32
 *
 * @author zrx
 * @version 1.0
 */

@Service
public class MallSearchServiceImpl implements MallSearchService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MallSearchServiceImpl.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 涉及的 sql 语句
     * <pre>
     *     POST /gulimall_product/_search
     * {
     *   "query": {
     *     "bool": {
     *       "must": [
     *         {
     *           "match": {
     *             "skuTitle": "华为"
     *           }
     *         }
     *       ],
     *       "filter": [
     *         {
     *           "term": {
     *             "catalogId": "225"
     *           }
     *         },
     *         {
     *           "terms": {
     *             "brandId": [
     *               "1",
     *               "2",
     *               "9"
     *             ]
     *           }
     *         },
     *         {
     *           "nested": {
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
     *           "nested": {
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
     *           "term": {
     *             "hasStock": "false"
     *           }
     *         },
     *         {
     *           "range": {
     *             "skuPrice": {
     *               "gte": 0,
     *               "lte": 6000
     *             }
     *           }
     *         }
     *       ]
     *     }
     *   },
     *   "aggs": {
     *     "brandAgg": {
     *       "terms": {
     *         "field": "brandId",
     *         "size": 100
     *       },
     *       "aggs": {
     *         "brand_name_add": {
     *           "terms": {
     *             "field": "brandName",
     *             "size": 100
     *           }
     *         },
     *         "brand_img_add": {
     *           "terms": {
     *             "field": "brandImg",
     *             "size": 100
     *           }
     *         }
     *       }
     *     },
     *     "catalogAgg": {
     *       "terms": {
     *         "field": "catalogId",
     *         "size": 100
     *       },
     *       "aggs": {
     *         "catalog_name_agg": {
     *           "nested": {
     *             "path": "attrs"
     *           },
     *           "aggs": {
     *             "attr_id_agg": {
     *               "terms": {
     *                 "field": "attrs.attrId",
     *                 "size": 100
     *               },
     *               "aggs": {
     *                 "attr_name_agg": {
     *                   "terms": {
     *                     "field": "attrs.attrName",
     *                     "size": 100
     *                   }
     *                 },
     *                 "attr_value_agg": {
     *                   "terms": {
     *                     "field": "attrs.attrValue",
     *                     "size": 100
     *                   }
     *                 }
     *               }
     *             }
     *           }
     *         }
     *       }
     *     }
     *   },
     *   "sort": [
     *     {
     *       "skuPrice": {
     *         "order": "desc"
     *       }
     *     }
     *   ],
     *   "from": 0,
     *   "size": 5,
     *   "highlight": {
     *     "fields": {
     *       "skuTitle": {
     *         "pre_tags": "<b style='color:red'>",
     *         "post_tags": "</b>"
     *       }
     *     }
     *   }
     * }
     * </pre>
     *
     * @param searchParam 检索参数
     * @return 查询结果
     */
    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchResult result = null;
        // 准备检索请求
        SearchRequest searchRequest = buildSearchRequest(searchParam);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest,
                    GulimallElasticSearchConfig.COMMON_OPTIONS);
            LOGGER.info("searchResponse = \n{}", searchResponse);

            // 封装响应数据
            result = buildSearchResult(searchResponse, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 构建请求参数
     *
     * @param searchParam 前端传来的 请求参数
     * @return es 请求参数
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 1 查找
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 1.1 模糊查询
        String keyword = searchParam.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            //      "must": [
            //        {
            //          "match": {
            //            "skuTitle": "华为"
            //          }
            //        }
            //      ],

            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", keyword));
        }

        // 1.2.1 三级分类id
        Long catalog3Id = searchParam.getCatalog3Id();
        if (catalog3Id != null) {
            //        {
            //          "term": {
            //            "catalogId": "225"
            //          }
            //        },

            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", catalog3Id));
        }

        // 1.2.2 品牌 id
        List<Long> brandId = searchParam.getBrandId();
        if (brandId != null && (!brandId.isEmpty())) {
            //        {
            //          "terms": {
            //            "brandId": [
            //              "1",
            //              "2",
            //              "9"
            //            ]
            //          }
            //        },

            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }

        // 1.2.3 属性查询
        List<String> attrs = searchParam.getAttrs();
        if (attrs != null && (!attrs.isEmpty())) {

            //          "nested": {
            //            "path": "attrs",
            //            "query": {
            //              "bool": {
            //                "must": [
            //                  {
            //                    "term": {
            //                      "attrs.attrId": {
            //                        "value": "10"
            //                      }
            //                    }
            //                  },
            //                  {
            //                    "terms": {
            //                      "attrs.attrValue": [
            //                        "玻璃",
            //                        "金属"
            //                      ]
            //                    }
            //                  }
            //                ]
            //              }
            //            }
            //          }


            attrs.forEach(attr -> {
                // attrs=1_其他:安卓&attrs=2_5寸  表示1号属性，值是安卓或其他
                String[] split = attr.split("_");
                if (split.length == 2) {
                    BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();

                    String attrId = split[0];
                    String values = split[1];
                    String[] valuesSplit = values.split(":");
                    nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                    nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", valuesSplit));

                    NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                    boolQueryBuilder.filter(nestedQuery);
                }
            });
        }

        // 1.2.4 库存
        Integer hasStock = searchParam.getHasStock();
        if (hasStock != null) {
            //        {
            //          "term": {
            //            "hasStock": "false"
            //          }
            //        },

            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", hasStock == 1));
        }

        // 1.2.5 价格区间
        String skuPrice = searchParam.getSkuPrice(); //100_500   _500  100_
        if (!StringUtils.isEmpty(skuPrice)) {

            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");


            //        {
            //          "range": {
            //            "skuPrice": {
            //              "gte": 0,
            //              "lte": 6000
            //            }
            //          }
            String[] split = skuPrice.split("_");
            int length = split.length;
            if (length == 2) {
                rangeQuery.from(split[0]).to(split[1]);
            } else if (length == 1) {
                if (skuPrice.startsWith("_")) {
                    rangeQuery.from(0).to(skuPrice);
                }

                if (skuPrice.endsWith("_")) {
                    rangeQuery.from(skuPrice);
                }
            }


            boolQueryBuilder.filter(rangeQuery);
        }

        // 1 总构建
        searchSourceBuilder.query(boolQueryBuilder);


        // 2 排序分类高亮
        // 2.1 sort
        String sort = searchParam.getSort();
        if (!StringUtils.isEmpty(sort)) {
            //saleCount_asc销量

            //  "sort": [
            //    {
            //      "skuPrice": {
            //        "order": "desc"
            //      }
            //    }
            //  ],
            String[] split = sort.split("_");
            if (split.length == 2) {
                if (split[1] != null && split[1].equalsIgnoreCase("asc")) {
                    searchSourceBuilder.sort(split[0], SortOrder.ASC);
                } else if (split[1] != null && split[1].equalsIgnoreCase("desc")) {
                    searchSourceBuilder.sort(split[0], SortOrder.DESC);
                }
            }
        }

        // 2.2 分页
        Integer pageNum = searchParam.getPageNum();
        if (pageNum != null) {
            //  "from": 0,
            //  "size": 5,

            int from = ((EsConstant.PAGE_SIZE - 1) * pageNum);

            searchSourceBuilder.from(from);
            searchSourceBuilder.size(EsConstant.PAGE_SIZE);
        }

        // 2.3 高亮
        if (!StringUtils.isEmpty(keyword)) {
            //  "highlight": {
            //    "fields": {
            //      "skuTitle": {
            //        "pre_tags": "<b style='color:red'>",
            //        "post_tags": "</b>"
            //      }
            //    }
            //  }

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }


        // 3 聚合分析
        // 3.1 brandAgg
        /*
         *         //    "brandAgg": {
         *         //      "terms": {
         *         //        "field": "brandId",
         *         //        "size": 100
         *         //      },
         *         //      "aggs": {
         *         //        "brand_name_add": {
         *         //          "terms": {
         *         //            "field": "brandName",
         *         //            "size": 100
         *         //          }
         *         //        },
         *         //        "brand_img_add": {
         *         //          "terms": {
         *         //            "field": "brandImg",
         *         //            "size": 100
         *         //          }
         *         //        }
         *         //      }
         *         //    },
         */
        // "field": "brandId", brand_agg
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);

        // "brand_name_agg":
        TermsAggregationBuilder brand_name_add = AggregationBuilders.terms("brand_name_agg").field("brandName").size(1);
        brandAgg.subAggregation(brand_name_add);

        // "brand_img_agg": {
        TermsAggregationBuilder brand_img_add = AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1);
        brandAgg.subAggregation(brand_img_add);

        searchSourceBuilder.aggregation(brandAgg);

        // 3.2 catalogAgg
        /*
                //    "catalogAgg": {
        //      "terms": {
        //        "field": "catalogId",
        //        "size": 100
        //      },
        //      "aggs": {
        //        "catalog_name_agg": {
        //          "nested": {
        //            "path": "attrs"
        */
        // catalog_name_agg
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(50);

        TermsAggregationBuilder catalog_name_agg = AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1);
        catalogAgg.subAggregation(catalog_name_agg);
        searchSourceBuilder.aggregation(catalogAgg);

        // 3.2属性聚合 注意是  catalogAgg 的子聚合
        /*
        "catalog_name_agg": {
          "nested": {
            "path": "attrs"
          },
          "aggs": {
            "attr_id_agg": {
              "terms": {
                "field": "attrs.attrId",
                "size": 100
              },
              "aggs": {
                "attr_name_agg": {
                  "terms": {
                    "field": "attrs.attrName",
                    "size": 100
                  }
                },
                "attr_value_agg": {
                  "terms": {
                    "field": "attrs.attrValue",
                    "size": 100
         */
        NestedAggregationBuilder attr_agg_nested = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(100);
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(100));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(100));

        attr_agg_nested.subAggregation(attr_id_agg);
        searchSourceBuilder.aggregation(attr_agg_nested);


        // ----------------

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);

        LOGGER.info("searchRequest = \n{}", searchRequest);

        return searchRequest;

    }

    /**
     * 构建请求相应结果
     * <pre>
     *     {
     *   "took" : 6,
     *   "timed_out" : false,
     *   "_shards" : {
     *     "total" : 1,
     *     "successful" : 1,
     *     "skipped" : 0,
     *     "failed" : 0
     *   },
     *   "hits" : {
     *     "total" : {
     *       "value" : 4,
     *       "relation" : "eq"
     *     },
     *     "max_score" : 0.6107198,
     *     "hits" : [
     *       {
     *         "_index" : "gulimall_product",
     *         "_type" : "_doc",
     *         "_id" : "7",
     *         "_score" : 0.6107198,
     *         "_source" : {
     *           "attrs" : [
     *             {
     *               "attrId" : 7,
     *               "attrName" : "品牌",
     *               "attrValue" : "华为"
     *             },
     *             {
     *               "attrId" : 10,
     *               "attrName" : "机身材质",
     *               "attrValue" : "玻璃"
     *             },
     *             {
     *               "attrId" : 9,
     *               "attrName" : "屏幕尺寸",
     *               "attrValue" : "4"
     *             },
     *             {
     *               "attrId" : 8,
     *               "attrName" : "CPU品牌",
     *               "attrValue" : "麒麟990"
     *             }
     *           ],
     *           "brandId" : 1,
     *           "brandImg" : "https://gulimall-madokast.oss-cn-shenzhen.aliyuncs.com/2020-05-25/b407e2e8-cbea-427d-8d78-a5ada5a007fd_huawei.jpg",
     *           "brandName" : "华为",
     *           "catalogId" : 225,
     *           "catalogName" : "手机",
     *           "hasStock" : false,
     *           "hotScore" : 0,
     *           "saleCount" : 0,
     *           "skuId" : 7,
     *           "skuImg" : "https://gulimall-madokast.oss-cn-shenzhen.aliyuncs.com/2020-06-02/5ab893c4-e4ac-4411-814e-37a7b26600fa_73ab4d2e818d2211.jpg",
     *           "skuPrice" : 5799.0,
     *           "skuTitle" : "华为 HUAWEI Mate 30 Pro 5G 罗兰紫 8G",
     *           "spuId" : 3
     *         },
     *         "highlight" : {
     *           "skuTitle" : [
     *             "<b style='color:red'>华为</b> HUAWEI Mate 30 Pro 5G 罗兰紫 8G"
     *           ]
     *         }
     *       },
     *       {
     *         "_index" : "gulimall_product",
     *         "_type" : "_doc",
     *         "_id" : "5",
     *         "_score" : 0.5869402,
     *         "_source" : {
     *           "attrs" : [
     *             {
     *               "attrId" : 7,
     *               "attrName" : "品牌",
     *               "attrValue" : "华为"
     *             },
     *             {
     *               "attrId" : 10,
     *               "attrName" : "机身材质",
     *               "attrValue" : "玻璃"
     *             },
     *             {
     *               "attrId" : 9,
     *               "attrName" : "屏幕尺寸",
     *               "attrValue" : "4"
     *             },
     *             {
     *               "attrId" : 8,
     *               "attrName" : "CPU品牌",
     *               "attrValue" : "麒麟990"
     *             }
     *           ],
     *           "brandId" : 1,
     *           "brandImg" : "https://gulimall-madokast.oss-cn-shenzhen.aliyuncs.com/2020-05-25/b407e2e8-cbea-427d-8d78-a5ada5a007fd_huawei.jpg",
     *           "brandName" : "华为",
     *           "catalogId" : 225,
     *           "catalogName" : "手机",
     *           "hasStock" : false,
     *           "hotScore" : 0,
     *           "saleCount" : 0,
     *           "skuId" : 5,
     *           "skuImg" : "https://gulimall-madokast.oss-cn-shenzhen.aliyuncs.com/2020-06-02/19e40288-bbe6-4323-819c-d7fe4f3cd66a_23d9fbb256ea5d4a.jpg",
     *           "skuPrice" : 5799.0,
     *           "skuTitle" : "华为 HUAWEI Mate 30 Pro 5G 翡冷翠 8G",
     *           "spuId" : 3
     *         },
     *         "highlight" : {
     *           "skuTitle" : [
     *             "<b style='color:red'>华为</b> HUAWEI Mate 30 Pro 5G 翡冷翠 8G"
     *           ]
     *         }
     *       }
     *     ]
     *   },
     *   "aggregations" : {
     *     "catalog_agg" : {
     *       "doc_count_error_upper_bound" : 0,
     *       "sum_other_doc_count" : 0,
     *       "buckets" : [
     *         {
     *           "key" : 225,
     *           "doc_count" : 4,
     *           "catalog_name_agg" : {
     *             "doc_count_error_upper_bound" : 0,
     *             "sum_other_doc_count" : 0,
     *             "buckets" : [
     *               {
     *                 "key" : "手机",
     *                 "doc_count" : 4
     *               }
     *             ]
     *           }
     *         }
     *       ]
     *     },
     *     "attr_agg" : {
     *       "doc_count" : 16,
     *       "attr_id_agg" : {
     *         "doc_count_error_upper_bound" : 0,
     *         "sum_other_doc_count" : 0,
     *         "buckets" : [
     *           {
     *             "key" : 7,
     *             "doc_count" : 4,
     *             "attr_name_agg" : {
     *               "doc_count_error_upper_bound" : 0,
     *               "sum_other_doc_count" : 0,
     *               "buckets" : [
     *                 {
     *                   "key" : "品牌",
     *                   "doc_count" : 4
     *                 }
     *               ]
     *             },
     *             "attr_value_agg" : {
     *               "doc_count_error_upper_bound" : 0,
     *               "sum_other_doc_count" : 0,
     *               "buckets" : [
     *                 {
     *                   "key" : "华为",
     *                   "doc_count" : 4
     *                 }
     *               ]
     *             }
     *           },
     *           {
     *             "key" : 8,
     *             "doc_count" : 4,
     *             "attr_name_agg" : {
     *               "doc_count_error_upper_bound" : 0,
     *               "sum_other_doc_count" : 0,
     *               "buckets" : [
     *                 {
     *                   "key" : "CPU品牌",
     *                   "doc_count" : 4
     *                 }
     *               ]
     *             },
     *             "attr_value_agg" : {
     *               "doc_count_error_upper_bound" : 0,
     *               "sum_other_doc_count" : 0,
     *               "buckets" : [
     *                 {
     *                   "key" : "麒麟990",
     *                   "doc_count" : 4
     *                 }
     *               ]
     *             }
     *           },
     *           {
     *             "key" : 9,
     *             "doc_count" : 4,
     *             "attr_name_agg" : {
     *               "doc_count_error_upper_bound" : 0,
     *               "sum_other_doc_count" : 0,
     *               "buckets" : [
     *                 {
     *                   "key" : "屏幕尺寸",
     *                   "doc_count" : 4
     *                 }
     *               ]
     *             },
     *             "attr_value_agg" : {
     *               "doc_count_error_upper_bound" : 0,
     *               "sum_other_doc_count" : 0,
     *               "buckets" : [
     *                 {
     *                   "key" : "4",
     *                   "doc_count" : 4
     *                 }
     *               ]
     *             }
     *           },
     *           {
     *             "key" : 10,
     *             "doc_count" : 4,
     *             "attr_name_agg" : {
     *               "doc_count_error_upper_bound" : 0,
     *               "sum_other_doc_count" : 0,
     *               "buckets" : [
     *                 {
     *                   "key" : "机身材质",
     *                   "doc_count" : 4
     *                 }
     *               ]
     *             },
     *             "attr_value_agg" : {
     *               "doc_count_error_upper_bound" : 0,
     *               "sum_other_doc_count" : 0,
     *               "buckets" : [
     *                 {
     *                   "key" : "玻璃",
     *                   "doc_count" : 4
     *                 }
     *               ]
     *             }
     *           }
     *         ]
     *       }
     *     },
     *     "brand_agg" : {
     *       "doc_count_error_upper_bound" : 0,
     *       "sum_other_doc_count" : 0,
     *       "buckets" : [
     *         {
     *           "key" : 1,
     *           "doc_count" : 4,
     *           "brand_img_agg" : {
     *             "doc_count_error_upper_bound" : 0,
     *             "sum_other_doc_count" : 0,
     *             "buckets" : [
     *               {
     *                 "key" : "https://gulimall-madokast.oss-cn-shenzhen.aliyuncs.com/2020-05-25/b407e2e8-cbea-427d-8d78-a5ada5a007fd_huawei.jpg",
     *                 "doc_count" : 4
     *               }
     *             ]
     *           },
     *           "brand_name_agg" : {
     *             "doc_count_error_upper_bound" : 0,
     *             "sum_other_doc_count" : 0,
     *             "buckets" : [
     *               {
     *                 "key" : "华为",
     *                 "doc_count" : 4
     *               }
     *             ]
     *           }
     *         }
     *       ]
     *     }
     *   }
     * }
     * </pre>
     *
     * @param searchResponse es 的请求相应结果
     * @return 传给前端的请求相应结果
     */
    private SearchResult buildSearchResult(SearchResponse searchResponse, SearchParam searchParam) {
        SearchResult result = new SearchResult();

        SearchHits hits = searchResponse.getHits(); // 大 hits
        SearchHit[] searchHits = hits.getHits(); // 内 hits


        List<SkuEsModel> products = new ArrayList<>();
        if (searchHits != null && searchHits.length > 0) {
            for (SearchHit hit : searchHits) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel sku = JSON.parseObject(sourceAsString, SkuEsModel.class);

                // 高亮还没有封装进去
                if (!StringUtils.isEmpty(searchParam.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String high = skuTitle.getFragments()[0].string();
                    sku.setSkuTitle(high);
                }

                products.add(sku);
            }
        }


        // 所有查询到的商品
        result.setProducts(products);

        // -------------- agg
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        ParsedNested attr_agg = aggregations.get("attr_agg");
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");


        // 所有分类信息
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        if (buckets != null && buckets.size() > 0) {
            for (Terms.Bucket bucket : buckets) {
                SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();

                String key = bucket.getKeyAsString();
                String catalog_name = ((ParsedStringTerms) bucket.getAggregations().get("catalog_name_agg"))
                        .getBuckets().get(0).getKeyAsString();

                catalogVo.setCatalogId(Long.parseLong(key));
                catalogVo.setCatalogName(catalog_name);

                catalogVos.add(catalogVo);
            }
        }
        result.setCatalogs(catalogVos);


        // 所有商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        buckets = attr_id_agg.getBuckets();
        if (buckets != null && buckets.size() > 0) {
            for (Terms.Bucket bucket : buckets) {
                SearchResult.AttrVo attrVo = new SearchResult.AttrVo();

                String key = bucket.getKeyAsString();

                String attr_name_agg = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg"))
                        .getBuckets().get(0).getKeyAsString();


                List<? extends Terms.Bucket> attr_value_agg = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets();
                List<String> attrValue = new ArrayList<>();

                for (Terms.Bucket item : attr_value_agg) {
                    String k = item.getKeyAsString();
                    attrValue.add(k);
                }

                attrVo.setAttrId(Long.parseLong(key));
                attrVo.setAttrName(attr_name_agg);
                attrVo.setAttrValue(attrValue);

                attrVos.add(attrVo);
            }
        }
        result.setAttrs(attrVos);


        // 当前商品涉及的品牌信息
        buckets = brand_agg.getBuckets();
        List<SearchResult.BrandVo> brands = new ArrayList<>();
        if (buckets != null && buckets.size() > 0) {
            for (Terms.Bucket bucket : buckets) {
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo();

                String key = bucket.getKeyAsString();

                String brand_img_agg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg"))
                        .getBuckets().get(0).getKeyAsString();

                String brand_name_agg = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg"))
                        .getBuckets().get(0).getKeyAsString();

                brandVo.setBrandId(Long.parseLong(key));
                brandVo.setBrandName(brand_name_agg);
                brandVo.setBrandImg(brand_img_agg);

                brands.add(brandVo);
            }
        }
        result.setBrands(brands);


        // 分页信息
        long total = hits.getTotalHits().value; //总记录数
        int totalPages = (int) (
                total % EsConstant.PAGE_SIZE == 0 ? total / EsConstant.PAGE_SIZE : total / EsConstant.PAGE_SIZE + 1);
        result.setPageNum(searchParam.getPageNum()); //当前页数
        result.setTotalPages(totalPages); // 总页数
        result.setTotal(total); // 总记录数


        LOGGER.info("totalPages = \n{}", totalPages);

        return result;
    }
}
