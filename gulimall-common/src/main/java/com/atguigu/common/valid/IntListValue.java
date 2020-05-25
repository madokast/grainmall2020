package com.atguigu.common.valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Description
 * 自定义的校验注解
 * 这个校验指的是，字段的值只能是给定的 list 中的值
 * 例如 list = {1,2}
 * 那么字段的值就只能是 1 2，其他不行
 * 使用示例 @IntListValue(values = {0, 1}, message = "显示状态必须是0或1", groups = {AddGroup.class, UpdateGroup.class})
 * <p>
 * Data
 * 2020/5/25-21:51
 *
 * @author zrx
 * @version 1.0
 */

@Documented
@Constraint(validatedBy = IntListValueConstraintValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface IntListValue {

    String message() default "{com.atguigu.common.valid.IntListValue.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * 字段的取值
     */
    int[] values();

}
