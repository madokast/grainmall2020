package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 商品三级分类
 *
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-04 23:20:01
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 2020年5月7日
     *
     * @return 查出所有分类，以树形结构组装
     * @see CategoryService#listWithTree()
     */
    @RequestMapping("/list/tree")
    public R listTree() {
        // all
        List<CategoryEntity> listWithTree = categoryService.listWithTree();

        return R.ok().put("data", listWithTree);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category) {
        if (categoryService.save(category)) {
            return R.ok();
        }else {
            return R.error(HttpStatus.SC_BAD_REQUEST,"新增失败");
        }

    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category) {
        if (categoryService.updateById(category)) {
            return R.ok();
        }else {
            return R.error(HttpStatus.SC_BAD_REQUEST,"修改失败");
        }
    }

    /**
     * 删除 CategoryEntity 以 catId
     * 只有当所有的 catIds 全部都满足删除条件————即没有子分类时，才会删除
     * 只要有一个不满足，就不会执行
     *
     * @RequestBody 来自请求体，以此方法只接受 post 请求
     * 可以是 json 格式
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds) {

        // 此方法调用时不加检查，不能用
        // categoryService.removeByIds(Arrays.asList(catIds));

        if (categoryService.removeMenuByIds(Arrays.asList(catIds))) {
            return R.ok();
        } else {
            return R.error(HttpStatus.SC_BAD_REQUEST,"删除失败");
        }

    }

}
