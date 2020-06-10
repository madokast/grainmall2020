package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseItemDonVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 未领取的采购单
     * <p>
     * CREATE TABLE `wms_purchase` (
     * `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '采购单id',
     * `assignee_id` bigint(20) DEFAULT NULL COMMENT '采购人id',
     * `assignee_name` varchar(255) DEFAULT NULL COMMENT '采购人名',
     * `phone` char(13) DEFAULT NULL COMMENT '联系方式',
     * `priority` int(4) DEFAULT NULL COMMENT '优先级',
     * `status` int(4) DEFAULT NULL COMMENT '状态',
     * `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
     * `amount` decimal(18,4) DEFAULT NULL COMMENT '总金额',
     * `create_time` datetime DEFAULT NULL COMMENT '创建日期',
     * `update_time` datetime DEFAULT NULL COMMENT '更新日期',
     * PRIMARY KEY (`id`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购信息'
     *
     * @param params 分页
     * @return 结果
     */
    @Override
    public PageUtils queryPageUnreceiveList(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.and(w -> {
            w.eq("status", 0).or().eq("status", 1);
        });


        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long perchanceId = mergeVo.getPerchanceId();
        List<Long> items = mergeVo.getItems();

        if (perchanceId == null) {
            // 新建采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.CREATED.getCode());
            this.save(purchaseEntity);
            perchanceId = purchaseEntity.getId();

        }

        // TODO 确认采购单状态是 0 or 1 才可以合并

        Long finalPerchanceId = perchanceId;
        List<PurchaseDetailEntity> purchaseDetailEntities = items.stream().map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPerchanceId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatus.ASSIGNED.getCode());

            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailEntities);

        // 更新 修改时间
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(perchanceId);
        purchaseEntity.setUpdateTime(new Date());

        this.updateById(purchaseEntity);

    }

    /**
     * 领取采购单
     * 主要是更新 采购单 和 采购单详情
     *
     * @param purchaseIds 采购单id
     */
    @Override
    public void receivePurchase(List<Long> purchaseIds) {
        List<PurchaseEntity> purchaseEntities = purchaseIds.stream()
                .map(this::getById)
                .filter(purchaseEntity -> purchaseEntity.getStatus() == WareConstant.PurchaseStatus.CREATED.getCode() ||
                        purchaseEntity.getStatus() == WareConstant.PurchaseStatus.ASSIGNED.getCode())
                .peek(purchaseEntity -> {
                    purchaseEntity.setStatus(WareConstant.PurchaseStatus.RECEIVED.getCode());
                    purchaseEntity.setUpdateTime(new Date());
                }).collect(Collectors.toList());

        this.updateBatchById(purchaseEntities);

        purchaseIds.forEach(purchaseId -> {
            List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService.listDetailByPurchaseId(purchaseId);

            if(!purchaseDetailEntityList.isEmpty()){
                purchaseDetailEntityList.forEach(purchaseDetailEntity -> {
                    purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatus.BUYING.getCode());
                });

                purchaseDetailService.updateBatchById(purchaseDetailEntityList);
            }


        });
    }

    @Transactional
    @Override
    public void donePurchase(PurchaseDoneVo purchaseDoneVo) {
        Long purchaseId = purchaseDoneVo.getId();
        List<PurchaseItemDonVo> purchaseItemDonVoList = purchaseDoneVo.getItems();

        LOGGER.info("purchaseDoneVo = {}", purchaseDoneVo);

        // 2. 改变采购项目状态
        boolean noErrorFlag = true;
        List<PurchaseDetailEntity> updates = new ArrayList<>(purchaseItemDonVoList.size());
        for (PurchaseItemDonVo purchaseItemDonVo : purchaseItemDonVoList) {
            Long itemId = purchaseItemDonVo.getItemId();
            Integer status = purchaseItemDonVo.getStatus();
            if(status==WareConstant.PurchaseDetailStatus.FAILED.getCode()){
                noErrorFlag = false;
            }

            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(itemId);
            purchaseDetailEntity.setStatus(status);

            updates.add(purchaseDetailEntity);


            // 3. 采购商品入库
            PurchaseDetailEntity purchaseDetailEntity1 = purchaseDetailService.getById(itemId);
            wareSkuService.addStock(purchaseDetailEntity1.getSkuId(),purchaseDetailEntity.getWareId(),purchaseDetailEntity.getSkuNum());
        }

        purchaseDetailService.updateBatchById(updates);

        // 1. 改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        if(noErrorFlag){
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.FINISHED.getCode());
        }else {
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.HAS_ERROR.getCode());
        }
        this.updateById(purchaseEntity);



    }
}