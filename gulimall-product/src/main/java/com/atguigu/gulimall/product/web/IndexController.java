package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Description
 * 访问 http://192.168.2.13:35200/index.html 跳转到 index.html
 * <p>
 * Data
 * 2020/6/14-22:39
 *
 * @author zrx
 * @version 1.0
 */

@Controller
public class IndexController {
    private final static Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        // 查出所有 1级商品分类
        List<CategoryEntity> categoryEntities = categoryService.getLeaveOneCategoris();

        // 交给页面 model
        model.addAttribute("categorys", categoryEntities);

        // 前缀默认是 classPath:/templates/
        // 后缀默认是 .html
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        return categoryService.getCatelogJson();
    }
}
