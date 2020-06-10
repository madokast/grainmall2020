package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description
 * ProductFeignService
 * <p>
 * Data
 * 2020/6/6-23:48
 *
 * @author zrx
 * @version 1.0
 */


// 给网关发请求
//@FeignClient("gulimall-product")
@FeignClient("gulimall-gateway")
public interface ProductFeignService {
    Logger LOGGER = LoggerFactory.getLogger(ProductFeignService.class);

    //@RequestMapping("/product/skuinfo/info/{skuId}")
    @RequestMapping("/api/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId);
}
