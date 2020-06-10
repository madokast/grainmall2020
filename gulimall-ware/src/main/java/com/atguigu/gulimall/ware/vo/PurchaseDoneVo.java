package com.atguigu.gulimall.ware.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Description
 * 完成采购单
 * <p>
 * Data
 * 2020/6/6-22:27
 *
 * @author zrx
 * @version 1.0
 */

public class PurchaseDoneVo implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseDoneVo.class);

    @NotNull
    private Long id;

    private List<PurchaseItemDonVo> items;

    @Override
    public String toString() {
        return "PurchaseDoneVo{" +
                "id=" + id +
                ", items=" + items +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PurchaseItemDonVo> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItemDonVo> items) {
        this.items = items;
    }
}
