package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;

//  PageUtils 对象
//	/**
//	 * 总记录数
//	 */
//	private int totalCount;
//	/**
//	 * 每页记录数
//	 */
//	private int pageSize;
//	/**
//	 * 总页数
//	 */
//	private int totalPage;
//	/**
//	 * 当前页数
//	 */
//	private int currPage;
//	/**
//	 * 列表数据
//	 */
//	private List<?> list;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrService attrService;

    @Override
    @Deprecated
    public PageUtils queryPage(Map<String, Object> params) {
        // mybatis IPage 实现 Page
        // 学习下 IPage
        // 1. 具有排序功能，有升序、降序字段
        // 2. 分页相关信息

        // 学习 IPage 的实现 Page
        // 数据List 总数 当前页数


        // 这个方法
        //@Override
        //    public IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper) {
        //        return baseMapper.selectPage(page, queryWrapper);
        //    }
        //
        // 再底层
        // /**
        //     * 根据 entity 条件，查询全部记录（并翻页）
        //     *
        //     * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
        //     * @param queryWrapper 实体对象封装操作类（可以为 null）
        //     */
        //    IPage<T> selectPage(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);


        IPage<AttrGroupEntity> page = this.page(
                // 分页条件
                new Query<AttrGroupEntity>().getPage(params),

                // 实体对象封装
                new QueryWrapper<AttrGroupEntity>()
        );

        // new PageUtils(page)
        //		this.list = page.getRecords();
        //		this.totalCount = (int)page.getTotal();
        //		this.pageSize = (int)page.getSize();
        //		this.currPage = (int)page.getCurrent();
        //		this.totalPage = (int)page.getPages();
        return new PageUtils(page);
    }

    /**
     * 如果三级分类id [catelogId] == 0，则查询所有
     * <p>
     * 2020年5月31日 重构
     *
     * @param params    分页相关参数
     *                  page
     *                  limit
     *                  sidx 排序字段
     *                  order 排序方式
     *                  key 全字段模糊查询
     * @param catelogId 三级分类id
     * @return 查询结果
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if (catelogId != 0) {
            // key 是全字段模糊查询

            // pms_attr_group 表信息
            //CREATE TABLE `pms_attr_group` (
            //   `attr_group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分组id',
            //   `attr_group_name` char(20) DEFAULT NULL COMMENT '组名',
            //   `sort` int(11) DEFAULT NULL COMMENT '排序',
            //   `descript` varchar(255) DEFAULT NULL COMMENT '描述',
            //   `icon` varchar(255) DEFAULT NULL COMMENT '组图标',
            //   `catelog_id` bigint(20) DEFAULT NULL COMMENT '所属分类id',
            //   PRIMARY KEY (`attr_group_id`)
            // ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='属性分组'

            // select * from pms_attr_group where catelog_id = ${catelogId} and
            // (其他字段 like ${key})

            // 构造查询器
            wrapper.and(obj -> {
                obj.eq("catelog_id", catelogId);
            });

        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper = wrapper.and(obj -> {
                obj.eq("attr_group_id", key)
                        .or()
                        .like("attr_group_name", key)
                        .or()
                        .like("descript", key);
            });
        }

        // 查询
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }


    /**
     * 删除 attrAttrgroupRelationEntities
     *
     * @param attrAttrgroupRelationEntities
     */
    @Override
    public void deleteRelation(List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities) {
        if (!attrAttrgroupRelationEntities.isEmpty()) {
            QueryWrapper<AttrAttrgroupRelationEntity> objectQueryWrapper = new QueryWrapper<>();

            attrAttrgroupRelationEntities.forEach(
                    attrAttrgroupRelationEntity -> {
                        Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                        Long attrId = attrAttrgroupRelationEntity.getAttrId();
                        objectQueryWrapper.or(
                                obj -> obj.eq("attr_id", attrId).eq("attr_group_id", attrGroupId)
                        );
                    }
            );

            attrAttrgroupRelationDao.delete(objectQueryWrapper);
        }


    }

    /**
     * 获取 catelogId 对应的商品三级分类 对应的所有 分组attr group
     * 同时每个分组带有全部的属性信息 attr
     *
     * @param catelogId 商品三级分类 id
     * @return AttrGroupWithAttrsVo
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

        List<AttrGroupEntity> attrGroupEntities = this.list(
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId)
        );

        return attrGroupEntities.stream().map(attrGroupEntity -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroupEntity, attrGroupWithAttrsVo);

            Long attrGroupId = attrGroupEntity.getAttrGroupId();

            List<AttrEntity> relationAttrs = attrService.getRelationAttr(attrGroupId);

            attrGroupWithAttrsVo.setAttrEntityList(relationAttrs);

            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());

    }
}