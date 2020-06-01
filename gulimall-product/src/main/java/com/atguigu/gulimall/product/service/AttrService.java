package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-04 22:45:30
 */
public interface AttrService extends IService<AttrEntity> {


    enum AttrType{
        /**
         * 属性类别，销售属性
         */
        ATTR_TYPE_SALE("sale",0)
        /**
         * 属性类别，基本属性
         */
        ,ATTR_TYPE_BASE("base",1);

        private String type;
        private Integer code;

        AttrType(String type, Integer code) {
            this.type = type;
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public Integer getCode() {
            return code;
        }
    }

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId);

    AttrRespVo getAttrRespVoById(Long attrId);

    void updateAttr(AttrVo attr);

    PageUtils querySaleAttr(Map<String, Object> params, Long catelogId);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId);
}

