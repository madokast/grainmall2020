package com.atguigu.gulimall.ware.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Description
 * MergeVo
 * 采购需求合并
 * <p>
 * Data
 * 2020/6/6-21:40
 *
 * @author zrx
 * @version 1.0
 */

public class MergeVo implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(MergeVo.class);

    /**
     * 整单 id
     */
    private Long perchanceId;

    /**
     * 合并项目集合
     */
    private List<Long> items;

    @Override
    public String toString() {
        return "MergeVo{" +
                "perchanceId=" + perchanceId +
                ", items=" + items +
                '}';
    }

    public Long getPerchanceId() {


        return perchanceId;
    }

    public void setPerchanceId(Long perchanceId) {
        this.perchanceId = perchanceId;
    }

    public List<Long> getItems() {
        return items;
    }

    public void setItems(List<Long> items) {
        this.items = items;
    }
}
