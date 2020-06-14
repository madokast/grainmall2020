package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SpuInfoServiceImpl.class);

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 2020年6月5日
     * 条件检索
     *
     * @param params 分页+条件
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.and(w -> {
                w.eq("publish_status", status);
            });
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String catalogId = (String) params.get("catalogId");
        if (!StringUtils.isEmpty(catalogId) && !"0".equalsIgnoreCase(catalogId)) {
            queryWrapper.eq("catalog_id", catalogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 大保存
     * TODO 更多的知识在高级部分
     *
     * @param spuSaveVo spu商品信息
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {

        // 1. 保存基本信息
        //CREATE TABLE `pms_spu_info` (
        //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
        //   `spu_name` varchar(200) DEFAULT NULL COMMENT '商品名称',
        //   `spu_description` varchar(1000) DEFAULT NULL COMMENT '商品描述',
        //   `catalog_id` bigint(20) DEFAULT NULL COMMENT '所属分类id',
        //   `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌id',
        //   `weight` decimal(18,4) DEFAULT NULL,
        //   `publish_status` tinyint(4) DEFAULT NULL COMMENT '上架状态[0 - 下架，1 - 上架]',
        //   `create_time` datetime DEFAULT NULL,
        //   `update_time` datetime DEFAULT NULL,
        //   PRIMARY KEY (`id`)
        // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='spu信息'
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setUpdateTime(new Date());
        spuInfoEntity.setCreateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
        Long spuId = spuInfoEntity.getId(); // 拿到 spu id


        // 2. spu 描述图片 pms_spu_info_desc
        //CREATE TABLE `pms_spu_info_desc` (
        //   `spu_id` bigint(20) NOT NULL COMMENT '商品id',
        //   `decript` longtext COMMENT '商品介绍',
        //   PRIMARY KEY (`spu_id`)
        // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='spu信息介绍'
        List<String> decriptList = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(String.join(",", decriptList));
        spuInfoDescService.getBaseMapper().insert(spuInfoDescEntity);

        // 3. spu 图片集 pms_spu_images
        //CREATE TABLE `pms_spu_images` (
        //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
        //   `spu_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
        //   `img_name` varchar(200) DEFAULT NULL COMMENT '图片名',
        //   `img_url` varchar(255) DEFAULT NULL COMMENT '图片地址',
        //   `img_sort` int(11) DEFAULT NULL COMMENT '顺序',
        //   `default_img` tinyint(4) DEFAULT NULL COMMENT '是否默认图',
        //   PRIMARY KEY (`id`)
        // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='spu图片'
        List<String> spuSaveVoImages = spuSaveVo.getImages();
        spuImagesService.saveImagesForSpuId(spuId, spuSaveVoImages);


        // 4. spu 基本属性 （规格属性） pms_product_attr_value
        //CREATE TABLE `pms_product_attr_value` (
        //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
        //   `spu_id` bigint(20) DEFAULT NULL COMMENT '商品id',
        //   `attr_id` bigint(20) DEFAULT NULL COMMENT '属性id',
        //   `attr_name` varchar(200) DEFAULT NULL COMMENT '属性名',
        //   `attr_value` varchar(200) DEFAULT NULL COMMENT '属性值',
        //   `attr_sort` int(11) DEFAULT NULL COMMENT '顺序',
        //   `quick_show` tinyint(4) DEFAULT NULL COMMENT '快速展示【是否展示在介绍上；0-否 1-是】',
        //   PRIMARY KEY (`id`)
        // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='spu属性值'
        List<BaseAttrs> baseAttrsList = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntityList = baseAttrsList.stream().map(baseAttrs -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuId);
            productAttrValueEntity.setAttrId(baseAttrs.getAttrId());

            AttrEntity attrEntity = attrService.getById(baseAttrs.getAttrId());

            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(baseAttrs.getAttrValues());
            productAttrValueEntity.setQuickShow(baseAttrs.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntityList);

        // 4.1 spu 积分信息 跨库 sms_spu_bounds
        //CREATE TABLE `sms_spu_bounds` (
        //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
        //   `spu_id` bigint(20) DEFAULT NULL,
        //   `grow_bounds` decimal(18,4) DEFAULT NULL COMMENT '成长积分',
        //   `buy_bounds` decimal(18,4) DEFAULT NULL COMMENT '购物积分',
        //   `work` tinyint(1) DEFAULT NULL COMMENT '优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]',
        //   PRIMARY KEY (`id`)
        // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品spu积分设置'
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuId);
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            LOGGER.error("远程保存spu积分信息失败");
        }


        // 5. 保存 sku
        List<Skus> skus = spuSaveVo.getSkus(); // sku 全部信息
        if (skus != null && !skus.isEmpty()) {
            skus.forEach(item -> {

                // 5.1 sku 基本信息信息 pms_sku_info
                //CREATE TABLE `pms_sku_info` (
                //   `sku_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'skuId',
                //   `spu_id` bigint(20) DEFAULT NULL COMMENT 'spuId',
                //   `sku_name` varchar(255) DEFAULT NULL COMMENT 'sku名称',
                //   `sku_desc` varchar(2000) DEFAULT NULL COMMENT 'sku介绍描述',
                //   `catalog_id` bigint(20) DEFAULT NULL COMMENT '所属分类id',
                //   `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌id',
                //   `sku_default_img` varchar(255) DEFAULT NULL COMMENT '默认图片',
                //   `sku_title` varchar(255) DEFAULT NULL COMMENT '标题',
                //   `sku_subtitle` varchar(2000) DEFAULT NULL COMMENT '副标题',
                //   `price` decimal(18,4) DEFAULT NULL COMMENT '价格',
                //   `sale_count` bigint(20) DEFAULT NULL COMMENT '销量',
                //   PRIMARY KEY (`sku_id`)
                // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sku信息'
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(
                        item.getImages().stream().filter(img -> img.getDefaultImg() == 1).map(Images::getImgUrl).findFirst().orElse(null)
                );

                skuInfoService.getBaseMapper().insert(skuInfoEntity);
                // 保存 pms_sku_info 后，可以拿到主键 sku-id
                final Long skuId = skuInfoEntity.getSkuId();


                // 5.2 sku 图片信息 pms_sku_images
                //CREATE TABLE `pms_sku_images` (
                //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                //   `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
                //   `img_url` varchar(255) DEFAULT NULL COMMENT '图片地址',
                //   `img_sort` int(11) DEFAULT NULL COMMENT '排序',
                //   `default_img` int(11) DEFAULT NULL COMMENT '默认图[0 - 不是默认图，1 - 是默认图]',
                //   PRIMARY KEY (`id`)
                // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sku图片'
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(skuImagesEntity -> !StringUtils.isEmpty(skuImagesEntity.getImgUrl()))
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);

                // 5.3 sku 销售属性信息 pms_sku_sale_attr_value
                //CREATE TABLE `pms_sku_sale_attr_value` (
                //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                //   `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
                //   `attr_id` bigint(20) DEFAULT NULL COMMENT 'attr_id',
                //   `attr_name` varchar(200) DEFAULT NULL COMMENT '销售属性名',
                //   `attr_value` varchar(200) DEFAULT NULL COMMENT '销售属性值',
                //   `attr_sort` int(11) DEFAULT NULL COMMENT '顺序',
                //   PRIMARY KEY (`id`)
                // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sku销售属性&值'
                List<Attr> attrs = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);


                // 5.4 sku 优惠 满减信息（跨库
                // 5.4.1 sku 打折信息 sms_sku_ladder
                //CREATE TABLE `sms_sku_ladder` (
                //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                //   `sku_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
                //   `full_count` int(11) DEFAULT NULL COMMENT '满几件',
                //   `discount` decimal(4,2) DEFAULT NULL COMMENT '打几折',
                //   `price` decimal(18,4) DEFAULT NULL COMMENT '折后价',
                //   `add_other` tinyint(1) DEFAULT NULL COMMENT '是否叠加其他优惠[0-不可叠加，1-可叠加]',
                //   PRIMARY KEY (`id`)
                // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品阶梯价格'
                //---------- 全部封装 --------------------//


                // 5.4.2 sku 满减信息  sms_sku_full_reduction
                //CREATE TABLE `sms_sku_full_reduction` (
                //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                //   `sku_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
                //   `full_price` decimal(18,4) DEFAULT NULL COMMENT '满多少',
                //   `reduce_price` decimal(18,4) DEFAULT NULL COMMENT '减多少',
                //   `add_other` tinyint(1) DEFAULT NULL COMMENT '是否参与其他优惠',
                //   PRIMARY KEY (`id`)
                // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品满减信息'
                //---------- 全部封装 --------------------//

                // 5.4.3 sku 会员价格表 sms_member_price
                //CREATE TABLE `sms_member_price` (
                //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                //   `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
                //   `member_level_id` bigint(20) DEFAULT NULL COMMENT '会员等级id',
                //   `member_level_name` varchar(100) DEFAULT NULL COMMENT '会员等级名',
                //   `member_price` decimal(18,4) DEFAULT NULL COMMENT '会员对应价格',
                //   `add_other` tinyint(1) DEFAULT NULL COMMENT '可否叠加其他优惠[0-不可叠加优惠，1-可叠加]',
                //   PRIMARY KEY (`id`)
                // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品会员价格'
                //---------- 全部封装 --------------------//
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
                    R r1 = couponFeignService.saveSkuRedution(skuReductionTo);
                    if (r1.getCode() != 0) {
                        LOGGER.error("远程保存sku优惠信息失败");
                    }
                }

            });

        }


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }


    /**
     * 商品上级
     *
     * @param spuId 商品
     */
    @Transactional
    @Override
    public void up(Long spuId) {
        // 真正需要上架的是 sku
        List<SkuEsModel> skuEsModels;

        // 查出 spu-id 对应的所有 sku 信息
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // 属性 attrs 可被检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntitiesFotSpu = productAttrValueService.baseListForSpu(spuId);
        // 挑出检索属性
        List<Long> attrIds = productAttrValueEntitiesFotSpu.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);
        Set<Long> searchAttrIdSet = new HashSet<>(searchAttrIds);
        // 返回类型
        List<SkuEsModel.Attrs> attrs = productAttrValueEntitiesFotSpu.stream().filter(productAttrValueEntity -> {
            Long attrId = productAttrValueEntity.getAttrId();
            return searchAttrIdSet.contains(attrId);
        }).map(productAttrValueEntity -> {
            SkuEsModel.Attrs attrsItem = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(productAttrValueEntity, attrsItem);
            return attrsItem;
        }).collect(Collectors.toList());

        // 库存查询
        Map<Long, Boolean> skuHasStockMap = null;
        try {
            List<SkuHasStockTo> skuHasStockList = wareFeignService.getSkuHasStockList(skuIds);
            skuHasStockMap = skuHasStockList.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        } catch (Exception e) {
            LOGGER.error("商品上架 远程库存查询异常，原因{}", e.toString());
        }
        Map<Long, Boolean> finalSkuHasStockMap = skuHasStockMap;

        // 封装信息
        skuEsModels = skuInfoEntities.stream().map(skuInfoEntity -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            // 属性对拷
            BeanUtils.copyProperties(skuInfoEntity, skuEsModel);
            // 特殊的
            skuEsModel.setSkuPrice(skuInfoEntity.getPrice());
            skuEsModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());


            // 库存
            Long skuId = skuEsModel.getSkuId();
            if (finalSkuHasStockMap == null || finalSkuHasStockMap.isEmpty()) {
                // 默认有数据
                skuEsModel.setHasStock(true);
            } else {
                skuEsModel.setHasStock(finalSkuHasStockMap.getOrDefault(skuId, false));
            }

            // 热度评分 默认 0
            skuEsModel.setHotScore(0L);

            // 品牌名 图标
            Long brandId = skuEsModel.getBrandId();
            BrandEntity brandEntity = brandService.getById(brandId);
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());

            // 分类名字
            Long catalogId = skuEsModel.getCatalogId();
            CategoryEntity categoryEntity = categoryService.getById(catalogId);
            skuEsModel.setCatalogName(categoryEntity.getName());


            // attrs
            skuEsModel.setAttrs(attrs);

            return skuEsModel;
        }).collect(Collectors.toList());

        LOGGER.info("商品上架");
        skuEsModels.forEach(skuEsModel -> LOGGER.info(skuEsModel.toString()));


        // 数据发送给 es
        R r = searchFeignService.productStatusUp(skuEsModels);
        if (r.getCode() == 0) {
            // success
            LOGGER.info("es 上架成功");

            // 修改当前商品状态
            SpuInfoEntity spuInfoEntity = getById(spuId);
            spuInfoEntity.setPublishStatus(ProductConstant.ProductStatus.SPU_DOWN.getCode());
            updateById(spuInfoEntity);

        } else {
            // failure
            LOGGER.error("es 上架失败");

            // 接口幂等性
        }


    }
}