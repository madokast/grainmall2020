package com.atguigu.gulimall.search.controller;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ProductSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Description
 * 保存数据
 * <p>
 * Data
 * 2020/6/14-11:04
 *
 * @author zrx
 * @version 1.0
 */

@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticSaveController.class);

    @Autowired
    private ProductSaveService productSaveService;

    /**
     * 2020年6月14日
     * 上架商品
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {

        try {
            boolean failure = productSaveService.productStatusUp(skuEsModels);
            if (failure) throw new RuntimeException("商品上架 ES 批处理中有异常");

            return R.ok();
        } catch (Exception e) {
            LOGGER.error("商品上架ES异常 {}", e.toString());
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }
}
