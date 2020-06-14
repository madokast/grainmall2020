package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * Description
 * ProductSaveService
 * <p>
 * Data
 * 2020/6/14-11:07
 *
 * @author zrx
 * @version 1.0
 */

public interface ProductSaveService {

    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
