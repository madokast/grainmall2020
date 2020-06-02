package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuImagesDao;
import com.atguigu.gulimall.product.entity.SpuImagesEntity;
import com.atguigu.gulimall.product.service.SpuImagesService;

import javax.imageio.ImageIO;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public void saveImagesForSpuId(Long spuId, List<String> spuSaveVoImages) {
        if (spuSaveVoImages == null || spuSaveVoImages.isEmpty()) {
            return;
        } else {
            List<SpuImagesEntity> collect = spuSaveVoImages.stream().map(url -> {
                SpuImagesEntity spuImagesEntity = new SpuImagesEntity();

                spuImagesEntity.setSpuId(spuId);

                spuImagesEntity.setImgUrl(url);

                return spuImagesEntity;
            }).collect(Collectors.toList());

            this.saveBatch(collect);
        }
    }
}