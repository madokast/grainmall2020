package com.atguigu.common.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 * ProductConstant
 * <p>
 * Data
 * 2020/6/14-20:21
 *
 * @author zrx
 * @version 1.0
 */

public class ProductConstant {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductConstant.class);

    public enum ProductStatus {
        NEW_SPU(0, "商品新建"),
        SPU_UP(1, "商品上架"),
        SPU_DOWN(1, "商品上架");


        private int code;
        private String msg;

        ProductStatus(int code, String msg) {
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
