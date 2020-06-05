package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description
 * CouponFeignService
 * 远程调用 coupon 非服务
 * <p>
 * Data
 * 2020/6/5-21:04
 *
 * @author zrx
 * @version 1.0
 */

@FeignClient("gulimall-coupon")
@Component
public interface CouponFeignService {

    /**
     * RequestBody 注解
     * 传输的对象转为 json 在请求体中
     *
     * @param spuBoundsTo 对象
     * @return 结果
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    /**
     * 商品服务调用
     * 保存 sku 所有优惠信息
     * @param skuReductionTo to对象
     * @return ok
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuRedution(@RequestBody SkuReductionTo skuReductionTo);
}
