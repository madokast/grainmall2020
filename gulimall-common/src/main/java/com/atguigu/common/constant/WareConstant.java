package com.atguigu.common.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 * WareConstant
 * 库存常量
 * 2020年6月6日
 * <p>
 * Data
 * 2020/6/6-21:47
 *
 * @author zrx
 * @version 1.0
 */

public class WareConstant {
    private final static Logger LOGGER = LoggerFactory.getLogger(WareConstant.class);

    public enum PurchaseStatus {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVED(2, "已领取"),
        FINISHED(3, "已完成"),
        HAS_ERROR(4, "有异常");


        private int code;
        private String msg;

        PurchaseStatus(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum PurchaseDetailStatus {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISHED(3, "已完成"),
        FAILED(4, "已失败");


        private int code;
        private String msg;

        PurchaseDetailStatus(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
