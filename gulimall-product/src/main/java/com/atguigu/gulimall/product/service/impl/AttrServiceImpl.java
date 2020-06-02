package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 2020年5月31日
     * 先保存 attr 基本信息
     * 然后再保存 AttrAttrgroupRelation 信息
     *
     * @param attr AttrVo 这是一个VO 直接和前端对象统一
     */
    @Override
    @Transactional
    public void saveAttr(AttrVo attr) {

        // 保存 attr entity 数据
        AttrEntity attrEntity = new AttrEntity();
        // 自动把 AttrVo 中的数据封装到 AttrEntity 中
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);

        // bug# 只有基本属性才有对应的 属性group
        if (attrEntity.getAttrType() == 1) {

            // 保存 AttrAttrgroupRelation 信息
            //	 * 属性id
            //	private Long attrId;
            //	 * 属性分组id
            //	private Long attrGroupId;
            //	 * 属性组内排序
            //	private Integer attrSort;
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());  // 上面的 attrEntity save 完成后就有了自己的id？？
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }


    /**
     * Attr 表的查询
     * 这个表同时包括 销售属性、基本属性（规格属性）
     *
     * @param params    分页信息
     * @param catelogId 对用的三级分类id 0表示所有
     * @param type      销售属性？规格属性？
     */
    public PageUtils queryAttr(Map<String, Object> params, Long catelogId, AttrType type) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            attrEntityQueryWrapper = attrEntityQueryWrapper.and(obj -> {
                obj.eq("attr_id", key)
                        .or().like("attr_name", key);
            });
        }

        if (catelogId != 0) {
            attrEntityQueryWrapper = attrEntityQueryWrapper.and(obj -> {
                obj.eq("catelog_id", catelogId);
            });
        }

        if (AttrType.ATTR_TYPE_SALE.equals(type)) {
            attrEntityQueryWrapper = attrEntityQueryWrapper.and(obj -> {
                obj.eq("attr_type", type.getCode());
            });
        } else if (AttrType.ATTR_TYPE_BASE.equals(type)) {
            attrEntityQueryWrapper = attrEntityQueryWrapper.and(obj -> {
                obj.eq("attr_type", type.getCode());
            });
        }


        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);

        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> attrRespVoList = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            // 分类名
            Long catelogIdEach = attrEntity.getCatelogId();
            CategoryEntity categoryEntity = categoryDao.selectById(catelogIdEach);
            attrRespVo.setCatelogName(categoryEntity.getName());

            // #bug 销售属性不存在分组（attr-group-relation）
            if (AttrType.ATTR_TYPE_BASE.equals(type)) {
                // 分组名

                Long attrId = attrEntity.getAttrId();
                if (attrId != null) {
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
                            new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId)
                    );
                    if(attrAttrgroupRelationEntity!=null){
                        Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();

                        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);

                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }


            return attrRespVo;
        }).collect(Collectors.toList());

        // 设置回去
        pageUtils.setList(attrRespVoList);

        return pageUtils;
    }

    /**
     * 销售属性
     *
     * @param params    分页
     * @param catelogId 对用的三级分类id 0表示所有
     */
    @Override
    public PageUtils querySaleAttr(Map<String, Object> params, Long catelogId) {
        return queryAttr(params, catelogId, AttrType.ATTR_TYPE_SALE);
    }

    /**
     * 2020年5月31日
     * 比 queryPage 高级一点
     *
     * @param params    分页信息 + key
     * @param catelogId 对用的三级分类id 0表示所有
     */
    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId) {
        return queryAttr(params, catelogId, AttrType.ATTR_TYPE_BASE);
    }


    @Override
    public AttrRespVo getAttrRespVoById(Long attrId) {
        AttrEntity attrEntity = getById(attrId);

        AttrRespVo attrRespVo = new AttrRespVo();

        // 基本数据库信息
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        // bug# 只有基本属性才有 group 分组信息
        // 分组名
        if (AttrType.ATTR_TYPE_BASE.getCode().equals(attrEntity.getAttrType())) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId)
            );
            if (attrAttrgroupRelationEntity != null) {
                Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                attrRespVo.setAttrGroupId(attrGroupId);

                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }

            }
        }


        // 三级分类路径
        List<Long> catelogIdPath = categoryService.findCatelogIdPath(attrRespVo.getCatelogId());
        attrRespVo.setCatelogPath(catelogIdPath);

        // 三级分类名字
        CategoryEntity categoryEntity = categoryDao.selectById(attrRespVo.getCatelogId());
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }


        return attrRespVo;
    }

    @Override
    @Transactional
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        updateById(attrEntity);

        // bug# 只有基本属性才有 group 分组信息
        // 修改分组关联
        if (AttrType.ATTR_TYPE_BASE.getCode().equals(attr.getAttrType())) {
            Long attrGroupId = attr.getAttrGroupId();
            if (attrGroupId != null) {

                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
                attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());

                Integer count = attrAttrgroupRelationDao.selectCount(
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId())
                );


                // 修改
                if (count > 0) {

                    attrAttrgroupRelationDao.update(
                            attrAttrgroupRelationEntity,
                            new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId())
                    );

                } else { //新增
                    attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
                }
            }
        }
    }

    /**
     * 获取 分类 group 下的所有 attr （基本属性）
     *
     * @param attrGroupId attrGroupId
     * @return 分类 group 下的所有 attr
     */
    @Transactional
    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {

        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityList = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));

        if (attrAttrgroupRelationEntityList.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Long> attrIdList = attrAttrgroupRelationEntityList.stream()
                    .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

            Collection<AttrEntity> attrEntities = listByIds(attrIdList);

            return (List<AttrEntity>) attrEntities;
        }


    }

    /**
     * 获取当前group没有关联的、其他group也没有关联、合法分类的所有属性attr
     *
     * @param params      分页
     * @param attrGroupId attrGroupId
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId) {
        // 找 group 所属分类
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        // 分类id
        Long catelogId = attrGroupEntity.getCatelogId();

        // 当前分类下的所有分组
        List<AttrGroupEntity> attrGroupEntityList = attrGroupDao.selectList(
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId)
        );

        // 这些分组的ids
        List<Long> groupIds = attrGroupEntityList.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        // 这些分组的所有属性
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityList = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds)
        );

        // 他们的 attr id
        List<Long> attrIds = attrAttrgroupRelationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        // catelogId 下所有 attr
        // 且不能在 attrAttrgroupRelationEntityList 中
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>()
                // 三级分类
                .eq("catelog_id", catelogId)
                // 基本类型
                .eq("attr_type", AttrType.ATTR_TYPE_BASE);
        if (!attrIds.isEmpty()) {
            attrEntityQueryWrapper = attrEntityQueryWrapper.notIn("attr_id", attrIds);
        }

        // 模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            attrEntityQueryWrapper = attrEntityQueryWrapper.and(obj -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );


        return new PageUtils(page);
    }
}