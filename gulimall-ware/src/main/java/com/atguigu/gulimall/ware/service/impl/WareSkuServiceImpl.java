package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.vo.SkuHasStockVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

/**
 * CREATE TABLE `wms_ware_sku` (
 * `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
 * `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
 * `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
 * `stock` int(11) DEFAULT NULL COMMENT '库存数',
 * `sku_name` varchar(200) DEFAULT NULL COMMENT 'sku_name',
 * `stock_locked` int(11) DEFAULT NULL COMMENT '锁定库存',
 * PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='商品库存'
 */
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    private final static Logger LOGGER = LoggerFactory.getLogger(WareSkuServiceImpl.class);


    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 2020年6月6日
     * <p>
     * CREATE TABLE `wms_ware_sku` (
     * `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
     * `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
     * `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
     * `stock` int(11) DEFAULT NULL COMMENT '库存数',
     * `sku_name` varchar(200) DEFAULT NULL COMMENT 'sku_name',
     * `stock_locked` int(11) DEFAULT NULL COMMENT '锁定库存',
     * PRIMARY KEY (`id`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存'
     *
     * @param params 分页 skuId ware_id
     * @return 结果
     */
    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        WareSkuEntity one = this.getOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (one == null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(0);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);

            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == R.SUCCESS_CODE) {
                    Map<String, String> data = (Map<String, String>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) info.get("skuName"));
                }
            } catch (Exception e) {
                LOGGER.warn("wareSkuEntity.setSkuName 冗余行保存失败");
            }


            this.save(wareSkuEntity);
        } else {
            Integer oldStock = one.getStock();


            UpdateWrapper<WareSkuEntity> updateWrapper = new UpdateWrapper<>();

            updateWrapper.set("stock", oldStock + skuNum);
            updateWrapper.eq("sku_id", skuId);
            updateWrapper.eq("ware_id", wareId);

            this.update(updateWrapper);
        }
    }

    /**
     * 是否有库存
     * *    `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
     * *    `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
     * *    `stock` int(11) DEFAULT NULL COMMENT '库存数',
     *
     * @param skuIds sku ids
     * @return skuHasStockVos
     */
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {

        return skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();

            QueryWrapper<WareSkuEntity> skuHasStockVoQueryWrapper = new QueryWrapper<>();
            skuHasStockVoQueryWrapper.eq("sku_id", skuId);
            List<WareSkuEntity> skuEntityList = this.list(skuHasStockVoQueryWrapper);
            int stockSum = skuEntityList.stream().mapToInt(wareSkuEntity -> {
                Integer stock = wareSkuEntity.getStock();
                Integer stockLocked = wareSkuEntity.getStockLocked();

                if (stock == null) stock = 0;
                if (stockLocked == null) stockLocked = 0;

                int s = stock - stockLocked;
                return Math.max(s, 0);
            }).sum();


            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(stockSum > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());


    }
}