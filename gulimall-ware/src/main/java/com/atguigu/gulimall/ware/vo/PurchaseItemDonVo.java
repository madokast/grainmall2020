package com.atguigu.gulimall.ware.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Description
 * TODO
 * <p>
 * Data
 * 2020/6/6-22:28
 *
 * @author zrx
 * @version 1.0
 */

public class PurchaseItemDonVo implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseItemDonVo.class);


    /**
     * 采购项目 id
     */
    private Long itemId;

    /**
     * 状态 成功？ 失败？
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String reason;


    @Override
    public String toString() {
        return "PurchaseItemDonVo{" +
                "itemId=" + itemId +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
