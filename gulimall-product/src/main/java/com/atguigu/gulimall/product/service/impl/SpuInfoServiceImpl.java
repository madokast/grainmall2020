package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.BaseAttrs;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 大保存
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
        // TODO 远程保存

        // 5. 保存 sku
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
        // TODO 2020年6月2日 昨天学到这里


        // 5.2 sku 图片信息 pms_sku_images
        //CREATE TABLE `pms_sku_images` (
        //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
        //   `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
        //   `img_url` varchar(255) DEFAULT NULL COMMENT '图片地址',
        //   `img_sort` int(11) DEFAULT NULL COMMENT '排序',
        //   `default_img` int(11) DEFAULT NULL COMMENT '默认图[0 - 不是默认图，1 - 是默认图]',
        //   PRIMARY KEY (`id`)
        // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sku图片'


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


        // 5.4.2 sku 满减信息  sms_sku_full_reduction
        //CREATE TABLE `sms_sku_full_reduction` (
        //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
        //   `sku_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
        //   `full_price` decimal(18,4) DEFAULT NULL COMMENT '满多少',
        //   `reduce_price` decimal(18,4) DEFAULT NULL COMMENT '减多少',
        //   `add_other` tinyint(1) DEFAULT NULL COMMENT '是否参与其他优惠',
        //   PRIMARY KEY (`id`)
        // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品满减信息'


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

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }
}