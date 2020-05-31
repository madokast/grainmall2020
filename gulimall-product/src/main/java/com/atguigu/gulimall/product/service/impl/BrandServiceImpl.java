package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    private final static Logger LOGGER = LoggerFactory.getLogger(BrandServiceImpl.class);

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LOGGER.info("params = {}", params);
        // 以 key 的模糊查询
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> brandEntityQueryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            LOGGER.info("key = {}", key);

            brandEntityQueryWrapper.eq("brand_id",key)
                    .or().like("name",key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                brandEntityQueryWrapper
        );
        return new PageUtils(page);
    }

    // queryPage 原方法。新增模糊查询方法
    //    @Override
    //    public PageUtils queryPage(Map<String, Object> params) {
    //        IPage<BrandEntity> page = this.page(
    //                new Query<BrandEntity>().getPage(params),
    //                new QueryWrapper<BrandEntity>()
    //        );
    //
    //        return new PageUtils(page);
    //    }


    /**
     * 包装冗余字段的一致性
     * 如 品牌-三级分类 表
     * @param brand 品牌
     */
    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        String name = brand.getName();
        if(!StringUtils.isEmpty(name)){
            Long brandId = brand.getBrandId();
            categoryBrandRelationService.updateBrand(brandId,name);

            // TODO 跟新其他冗余信息
        }

        // 更新自己
        updateById(brand);
    }
}