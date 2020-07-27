package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Description
 * SearchController
 * <p>
 * MVC 的 controller
 *
 * <p>
 * Data
 * 2020/7/26-22:59
 *
 * @author zrx
 * @version 1.0
 */


@Controller
public class SearchController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model) {

        SearchResult result = mallSearchService.search(searchParam);

        model.addAttribute("result", result);

        return "list";
    }
}
