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

    /**
     * 2020年5月31日
     * 比 queryPage 高级一点
     *
     * @param params    分页信息 + key
     * @param catelogId 对用的三级分类id 0表示所有
     */
    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId) {
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

            // 分组名
            Long attrId = attrEntity.getAttrId();
            if (attrId != null) {
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId)
                );
                Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();

                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);

                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        // 设置回去
        pageUtils.setList(attrRespVoList);

        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrRespVoById(Long attrId) {
        AttrEntity attrEntity = getById(attrId);

        AttrRespVo attrRespVo = new AttrRespVo();

        // 基本数据库信息
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        // 分组名
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

        // 修改分组关联
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