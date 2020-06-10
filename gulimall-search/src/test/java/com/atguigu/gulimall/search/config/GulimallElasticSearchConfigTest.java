package com.atguigu.gulimall.search.config;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GulimallElasticSearchConfigTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(GulimallElasticSearchConfigTest.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Test
    public void injectTest() {
        LOGGER.info("restHighLevelClient = {}", restHighLevelClient);
    }


    @Test
    public void indexData() throws IOException {
        // 数据
        Map<String, String> data = new HashMap<>();
        data.put("userName", "madokast");
        data.put("age", "14");

        // 准备
        IndexRequest indexRequest = new IndexRequest("users"); // 指定 index 名
        indexRequest.id("1"); // 设置 id
        indexRequest.source(JSON.toJSONString(data), XContentType.JSON);

        // 保存
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        LOGGER.info("indexResponse = {}", indexResponse);

        //{
        //  "took" : 2,
        //  "timed_out" : false,
        //  "_shards" : {
        //    "total" : 1,
        //    "successful" : 1,
        //    "skipped" : 0,
        //    "failed" : 0
        //  },
        //  "hits" : {
        //    "total" : {
        //      "value" : 1,
        //      "relation" : "eq"
        //    },
        //    "max_score" : 1.0,
        //    "hits" : [
        //      {
        //        "_index" : "users",
        //        "_type" : "_doc",
        //        "_id" : "1",
        //        "_score" : 1.0,
        //        "_source" : {
        //          "userName" : "madokast",
        //          "age" : "14"
        //        }
        //      }
        //    ]
        //  }
        //}
    }


    @Test
    public void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank"); // 指定 index
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("address", "mill"));
        searchSourceBuilder.aggregation(AggregationBuilders.terms("ageTerms").field("age").size(5));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);

        LOGGER.info("searchSourceBuilder = " + searchSourceBuilder);


        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        LOGGER.info("searchResponse = {}", searchResponse);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            LOGGER.info("sourceAsString = {}", sourceAsString);
        }

        Terms ageTerms = searchResponse.getAggregations().get("ageTerms");
        List<? extends Terms.Bucket> buckets = ageTerms.getBuckets();
        buckets.forEach(bucket->{
            String keyAsString = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            LOGGER.info("keyAsString = {}", keyAsString);
            LOGGER.info("docCount = {}", docCount);

        });

    }

}