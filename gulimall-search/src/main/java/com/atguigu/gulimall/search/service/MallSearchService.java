package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

import java.io.IOException;

/**
 * Description
 * MallSearchService
 * <p>
 * Data
 * 2020/7/26-23:32
 *
 * @author zrx
 * @version 1.0
 */

public interface MallSearchService {


    /**
     * 检索结果
     * @param searchParam 检索参数
     * @return SearchResult 页面需要的信息
     */
    SearchResult search(SearchParam searchParam);
}
