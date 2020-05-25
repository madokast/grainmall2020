package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.IntListValue;
import com.atguigu.common.valid.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 学习数据校验
 * group 指定接口数组
 * 在 common 模块中定义
 * 1. com.atguigu.common.valid.AddGroup
 * 2. com.atguigu.common.valid.UpdateGroup
 * <p>
 * 在 controller 中指定分组
 * 使用 @Validated 注解
 * public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand)
 * <p>
 * 一些注意事项：
 * 分组生效问题：
 * 1. 若在 controller 层加了分组，如 @Validated(AddGroup.class) @RequestBody BrandEntity brand
 * 2. 但是 实体类 字段的校验没有指定分组，如 @URL(message = "logo必须是一个合法的URL地址")
 * 3. 那么校验不会生效
 * 4. 一句话：分组校验下，不带分组的校验不生效
 * <p>
 * 校验生效问题
 * 1. 很多校验是这样的：
 * 2. 若字段为空，就不校验，如 @URL(message = "logo必须是一个合法的URL地址")
 * 3. 只有当字段非空时，才会校验
 * <p>
 * 自定义校验规则
 * 1. 自定义校验注解
 * 2. 自定义校验器
 * 3. 关联以上两者
 * 具体如下
 * 0. 导包（因为实在 common 模块定义）
 * <dependency>
 * <groupId>javax.validation</groupId>
 * <artifactId>validation-api</artifactId>
 * <version>2.0.1.Final</version>
 * </dependency>
 * 1. 自定义校验注解
 *
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-04 22:45:30
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @Null(message = "新增品牌时不能指定id", groups = AddGroup.class) // 新增时不要提交
    @NotNull(message = "修改品牌时必须指定id", groups = UpdateGroup.class) // 修改时必须提交
    @TableId
    private Long brandId;

    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名必须提交且有效", groups = AddGroup.class)
    private String name;
    /**
     * 品牌logo地址
     */
    @NotEmpty(message = "logo必须提交且有效", groups = AddGroup.class)
    @URL(message = "logo必须是一个合法的URL地址", groups = {AddGroup.class, UpdateGroup.class}) // 新增/修改时都要生效
    private String logo;

    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @NotNull(message = "显示状态必须提交", groups = AddGroup.class)
    @IntListValue(values = {0, 1}, message = "显示状态必须是0或1", groups = {AddGroup.class, UpdateGroup.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotNull(message = "检索首字母必须提交且有效", groups = AddGroup.class)
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(message = "排序必须提交")
    @Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
    private Integer sort;


    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public Integer getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(Integer showStatus) {
        this.showStatus = showStatus;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
