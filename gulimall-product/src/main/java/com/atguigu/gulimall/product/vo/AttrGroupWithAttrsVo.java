package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description
 * 属性分组 及其下的所有 属性
 * @see com.atguigu.gulimall.product.controller.AttrGroupController#getAttrGroupWithAttrs(Long)
 * <p>
 * Data
 * 2020/6/2-21:38
 *
 * @author zrx
 * @version 1.0
 */

public class AttrGroupWithAttrsVo {
    private final static Logger LOGGER = LoggerFactory.getLogger(AttrGroupWithAttrsVo.class);

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属三级分类id
     */
    private Long catelogId;

    /**
     * 这个属性分组下的所有属性
     */
    private List<AttrEntity> attrEntityList;

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public Long getAttrGroupId() {
        return attrGroupId;
    }

    public void setAttrGroupId(Long attrGroupId) {
        this.attrGroupId = attrGroupId;
    }

    public String getAttrGroupName() {
        return attrGroupName;
    }

    public void setAttrGroupName(String attrGroupName) {
        this.attrGroupName = attrGroupName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getCatelogId() {
        return catelogId;
    }

    public void setCatelogId(Long catelogId) {
        this.catelogId = catelogId;
    }

    public List<AttrEntity> getAttrEntityList() {
        return attrEntityList;
    }

    @JsonProperty(value = "attrs")
    public void setAttrEntityList(List<AttrEntity> attrEntityList) {
        this.attrEntityList = attrEntityList;
    }

    @Override
    public String toString() {
        return "AttrGroupWithAttrsVo{" +
                "attrGroupId=" + attrGroupId +
                ", attrGroupName='" + attrGroupName + '\'' +
                ", sort=" + sort +
                ", descript='" + descript + '\'' +
                ", icon='" + icon + '\'' +
                ", catelogId=" + catelogId +
                ", attrEntityList=" + attrEntityList +
                '}';
    }
}
