package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.to.MemberPriceTo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        Long skuId = skuReductionTo.getSkuId();
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
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuId);
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }


        // 5.4.2 sku 满减信息  sms_sku_full_reduction
        //CREATE TABLE `sms_sku_full_reduction` (
        //   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
        //   `sku_id` bigint(20) DEFAULT NULL COMMENT 'spu_id',
        //   `full_price` decimal(18,4) DEFAULT NULL COMMENT '满多少',
        //   `reduce_price` decimal(18,4) DEFAULT NULL COMMENT '减多少',
        //   `add_other` tinyint(1) DEFAULT NULL COMMENT '是否参与其他优惠',
        //   PRIMARY KEY (`id`)
        // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品满减信息'
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if (skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            this.save(skuFullReductionEntity);
        }


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

        List<MemberPriceTo> memberPriceTos = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> memberPriceEntities = memberPriceTos.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuId);
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        })
                .filter(memberPriceEntity -> memberPriceEntity.getMemberPrice().compareTo(new BigDecimal("0")) > 0)
                .collect(Collectors.toList());


        memberPriceService.saveBatch(memberPriceEntities);
    }
}