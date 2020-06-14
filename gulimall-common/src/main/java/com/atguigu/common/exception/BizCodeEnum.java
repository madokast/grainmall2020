package com.atguigu.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 * <pre>
 * 业务 JSON code 代码 枚举类
 * 例如：使用 5 位数字
 * 10 - 通用
 *      - 001 参数格式校验
 * 11 - 商品
 * 12 - 订单
 * 13 - 购物车
 * 14 - 物流
 * </pre>
 * <p>
 * Data
 * 2020/5/21-22:52
 *
 * @author zrx
 * @version 1.0
 */

public enum BizCodeEnum {
    UNKNOWN_EXCEPTION(10000,"系统未知错误"),
    VALID_EXCEPTION(10001,"参数格式校验错误"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");


    private int code;
    private String msg;

    private BizCodeEnum(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
