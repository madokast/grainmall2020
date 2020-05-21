package com.atguigu.gulimall.product.exception;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description
 * 统一异常处理
 * <p>
 * Data
 * 2020/5/21-22:32
 *
 * @author zrx
 * @version 1.0
 */


// 指定这是一个 rest controller 的增加
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GulimallExceptionAdvice {
    private final static Logger LOGGER = LoggerFactory.getLogger(GulimallExceptionAdvice.class);

    @ExceptionHandler(value = MethodArgumentNotValidException.class) //要接收的异常。方法参数不合法异常
    public R handleValidException(MethodArgumentNotValidException e) {
        LOGGER.info("数据校验出现问题");

        BindingResult bindingResult = e.getBindingResult();

        Map<String, String> errorMap = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", errorMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleUnknownException(Throwable throwable) {
        LOGGER.info("出现未知异常 = {}", throwable.toString());

        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
