package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class GulimallProductApplicationTests {
    private final static Logger LOGGER = LoggerFactory.getLogger(GulimallProductApplicationTests.class);

    @Autowired
    private BrandService brandService;


    /**
     * 测试主键自增
     */
    @Test
    public void testBrandService(){

//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//
//        brandService.save(brandEntity);
//
//        LOGGER.info("保存成功");
    }

    /**
     * 查询测试
     */
    @Test
    public void testQuery(){
        List<BrandEntity> brand_id = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        LOGGER.info("brand_id = {}", brand_id);
        // brand_id = [BrandEntity(brandId=1, name=华为, logo=null, descript=null, showStatus=null, firstLetter=null, sort=null)]
    }

    @Test
    void contextLoads() {
    }

}
