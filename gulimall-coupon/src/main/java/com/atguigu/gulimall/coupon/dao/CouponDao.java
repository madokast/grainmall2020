package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-05 13:16:55
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
