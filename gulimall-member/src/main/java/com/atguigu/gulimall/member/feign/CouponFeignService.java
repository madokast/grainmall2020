package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * member服务 调用 coupon服务的远程接口
 * 2020年5月5日 基于测试而创建
 */

@FeignClient("gulimall-coupon")//要调用的服务名
@Component
public interface CouponFeignService {

    /**
     * 直接去 coupon controller 里面把选哟调用的方法的签名拿过来
     * @return 某个会员的所有优惠券信息
     */
    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();

}
