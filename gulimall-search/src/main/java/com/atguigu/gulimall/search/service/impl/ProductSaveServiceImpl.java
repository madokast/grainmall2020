package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.ProductSaveService;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description
 * TODO
 * <p>
 * Data
 * 2020/6/14-11:08
 *
 * @author zrx
 * @version 1.0
 */

@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductSaveServiceImpl.class);


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 上架商品
     * 保存商品数据
     *
     * @param skuEsModels 商品数据
     * @return 是否出错
     */
    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        // 给 es 建立索引。建立好映射关系 -- 已完成

        // 保存数据 批量操作
        // BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();

        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(skuEsModel), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        boolean hasFailures = bulkResponse.hasFailures();

        if (hasFailures) {
            List<String> collect = Arrays.stream(bulkResponse.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
            LOGGER.error("es 商品上架出现异常 {}", collect);
        }

        return hasFailures;

    }
}
