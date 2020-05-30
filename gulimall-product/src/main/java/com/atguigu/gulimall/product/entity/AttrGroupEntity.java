package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 属性分组
 * 
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-04 22:45:30
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
	private static final long serialVersionUID = 1L;

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
	 * 所属三级分类id完整路径
	 * 如 1-10-255
	 */
	@TableField(exist = false)
	private List<Long> catelogIdPath;

	public List<Long> getCatelogIdPath() {
		return catelogIdPath;
	}

	public void setCatelogIdPath(List<Long> catelogIdPath) {
		this.catelogIdPath = catelogIdPath;
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
}
