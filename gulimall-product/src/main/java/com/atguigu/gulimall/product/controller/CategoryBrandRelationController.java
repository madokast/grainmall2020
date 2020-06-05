package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.vo.BrandVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 品牌分类关联
 * 品牌对应的三级分类
 * 如 华为 - 手机
 *
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-04 23:20:01
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    private final static Logger LOGGER = LoggerFactory.getLogger(CategoryBrandRelationController.class);

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private BrandService brandService;

    /**
     * 2020年6月2日
     * 获取三级分类下所有品牌
     * 例如 手机分类下
     * 华为 小米 苹果
     * 1. controller 两个功能：1.校验 2.把 entity 转为 vo
     * 2. service 业务处理
     *
     * @param catId 三级分类id
     * @return 三级分类下所有品牌
     */
    @GetMapping("/brands/list")
    public R relationBrandList(@RequestParam(value = "catId", required = false) Long catId) {
        LOGGER.info("relationBrandList catId = {}", catId);


        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsByCatId(catId);
        if(catId!=null){
            categoryBrandRelationService.getBrandsByCatId(catId);
        }else {
            brandEntities = brandService.list();
        }



        List<BrandVo> brandVos = brandEntities.stream().map(brandEntity -> {
            BrandVo brandVo = new BrandVo();

            brandVo.setBrandId(brandEntity.getBrandId());

            brandVo.setBrandName(brandEntity.getName());

            return brandVo;
        }).collect(Collectors.toList());

        return R.ok().put("data", brandVos);
    }

    /**
     * 2020年5月31日
     * 查询品牌id下的管理到的三级分类
     *
     * @param brandId 品牌id
     */
    @GetMapping("/catelog/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam("brandId") Long brandId) {

        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId)
        );

        return R.ok().put("data", data);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        /**
         * 表有冗余
         * 这里只传了 品牌id 三级分类id
         * 但是还保存了 品牌名 分类名
         * 所以先查出来
         */

        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
