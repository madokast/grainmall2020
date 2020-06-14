package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 2020年5月7日
     *
     * @return 查出所有分类，以树形结构组装
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        // 查出所有分类
        List<CategoryEntity> entities = list();

        // sort 去空
        entities.stream().filter(e -> e.getSort() == null).forEach(e -> e.setSort(0));

        // 组成成树  一级菜单
        return entities.stream()
                // 找到所有一级分类
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                // 添加上子分类
                .peek(menu -> menu.setChildren(getChildren(menu, entities)))
                // 按照 sort 排序
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
    }

    /**
     * 获取 root 的所有子分类
     * 2020年5月7日
     *
     * @param root root分类
     * @param all  全部分类信息
     * @return root 的所有子分类
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        //LOGGER.debug("root = {}", root);

        return all.stream()
                // 找到 all 中父分类id == root.id 的
                .filter(menu -> Objects.equals(menu.getParentCid(), (root.getCatId())))
                // 递归，为找到的子分类，再找子子分类
                .peek(menu -> menu.setChildren(getChildren(menu, all)))
                // 按照 sort 排序
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
    }

    /**
     * 我们使用逻辑删除方法，也就是有一个标志位 showStatus
     * 是否显示[0-不显示，1显示]
     * 删除只是把 showStatus 置为 0
     * mybatis plus 提供了方便的逻辑删除的注解
     * https://mp.baomidou.com/guide/logic-delete.html
     * <p>
     * 首先配置全局逻辑删除规则
     * <p>
     * yml 加入如下配置
     * mybatis-plus:
     * global-config:
     * db-config:
     * logic-delete-field: flag  #全局逻辑删除字段值 3.3.0开始支持，详情看下面。
     * logic-delete-value: 1 # 逻辑已删除值(默认为 1)
     * logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
     * （可以不用配置）
     * <p>
     * 只需要在实体类  CategoryEntity 的字段 showStatus 加上注解
     *
     * @param idList 要删除的 CategoryEntity 的 catId 编号
     * @TableLogic(value = "1",delval = "0")
     */
    @Override
    public boolean removeMenuByIds(List<Long> idList) {

        LOGGER.info("removeMenuByIds idList = {}", idList);

        // 查出所有分类，按照 CatId 组成 map
        List<CategoryEntity> all = list();
        Map<Long, CategoryEntity> entityMap = all.stream()
                .collect(Collectors.toMap(CategoryEntity::getCatId, categoryEntity -> categoryEntity));

        // 递归删除——指的是若删除的节点还有子节点，那就也把子节点删除掉。
        // 查看递归删除需要删除多少个节点
        Set<Long> recurRemoveSet = new HashSet<>();
        for (Long catId : idList) {
            if (entityMap.containsKey(catId)) {
                CategoryEntity entity = entityMap.get(catId);

                // 递归删除需要的栈
                Stack<CategoryEntity> stack = new Stack<>();

                stack.push(entity);

                // 递归删除
                while (!stack.empty()) {
                    CategoryEntity pop = stack.pop();

                    // 先删除自己
                    recurRemoveSet.add(pop.getCatId());
                    LOGGER.info("recurRemoveSet = {}", recurRemoveSet);

                    if (recurRemoveSet.size() > idList.size()) {
                        // 递归删除需要删除的东西比 idList 的要多，直接失败
                        LOGGER.info("递归删除需要删除的东西比 idList 的要多，直接失败");
                        return false;
                    }

                    // 将 pop 的直接孩子加入栈中
                    stack.addAll(
                            all.stream().filter(categoryEntity ->
                                    Objects.equals(categoryEntity.getParentCid(), pop.getCatId()))
                                    .collect(Collectors.toSet())
                    );
                }

            } else {
                // 不存在 catId，直接失败
                LOGGER.info("不存在 catId，直接失败");
                return false;
            }
        }

        LOGGER.info("idList = {}", idList);
        LOGGER.info("recurRemoveSet = {}", recurRemoveSet);

        //判断
        if (recurRemoveSet.size() > idList.size()) {
            LOGGER.info("递归删除需要删除的东西比 idList 的要多");
            return false;
        }

        LOGGER.info("删除{}", idList);

        // 可以执行删除
        removeByIds(idList);

        return true;
    }


    /**
     * 保存节点
     * 保存节点时，至少需要传入 节点父id 节点名字
     * 若父id无效，禁止保存
     * 若父id是叶子层级=3，也禁止保存
     * 因为目录最多分为三级
     *
     * @param entity 节点
     * @return 是否保存成功
     */
    @Override
    public boolean save(CategoryEntity entity) {
        LOGGER.info("entity = {}", entity);

        // 检插这是不是有效的节点
        Long parentCid = entity.getParentCid();
        CategoryEntity byId = getById(parentCid);
        if (byId == null) {
            LOGGER.info("不是有效的节点");
            return false;
        }


        Integer catLevel = byId.getCatLevel();
        if (catLevel == 3) {
            LOGGER.info("叶子节点禁止新增子节点");
            return false;
        }


        entity.setCatLevel(catLevel);

        // 默认可见（未删除）
        entity.setShowStatus(1);

        LOGGER.info("执行保存");

        // 执行保存
        boolean save = super.save(entity);

        return save;

    }

    /**
     * 修改分类信息
     * 不认可其中的 父id 层级 showStatue 信息
     *
     * @param entity 分类
     * @return 是否修改成功
     */
    @Override
    public boolean updateById(CategoryEntity entity) {
        LOGGER.info("entity = {}", entity);

        if (entity.getName() == null || entity.getName().length() == 0) {
            LOGGER.info("name无效");

            return false;
        }

        Long catId = entity.getCatId();

        // 原节点
        CategoryEntity origin = getById(catId);

        if (origin == null) {
            LOGGER.info("catId无效");
            return false;
        }

        entity.setParentCid(origin.getParentCid());

        entity.setCatLevel(entity.getCatLevel());

        entity.setShowStatus(entity.getShowStatus());

        LOGGER.info("do update");

        return super.updateById(entity);
    }

    /**
     * 2020年5月31日
     * 找出 catelogId 所属三级分类的完整路径
     * 所谓完整路径，就是指从根到自己的路径上 catelogId 数组
     * 例如[2,25,225]
     *
     * @param catelogId 三级分类
     * @return 三级分类的完整路径
     */
    @Override
    public List<Long> findCatelogIdPath(Long catelogId) {
        Deque<Long> path = new LinkedList<>();

        CategoryEntity cur = this.getById(catelogId);
        path.addLast(cur.getCatId());

        Long parentId = null;
        while ((parentId = cur.getParentCid()) != null) {
            cur = this.getById(parentId);
            if (cur == null) break;
            path.addFirst(cur.getCatId());
        }

        return (List<Long>) path;
    }

    /**
     * 级联更新
     *
     * @param category
     * @return
     */
    @Override
    @Transactional
    public boolean updateCascade(CategoryEntity category) {
        LOGGER.info("category = {}", category);
        String name = category.getName();
        LOGGER.info("name = {}", name);
        if (!StringUtils.isEmpty(name)) {
            Long catId = category.getCatId();
            categoryBrandRelationService.updateCategory(catId, name);
        }

        // 更新自己
        this.updateById(category);

        return true;
    }

    @Override
    public List<CategoryEntity> getLeaveOneCategoris() {
        LOGGER.info("查询一级分类");

        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("parent_cid", 0); //parentCid

        return list(queryWrapper);

    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {

        // 一级分类
        List<CategoryEntity> leaveOneCategoris = getLeaveOneCategoris();

        return leaveOneCategoris.stream().collect(Collectors.toMap(
                c -> c.getCatId().toString(),
                c -> {
                    Long catId = c.getCatId();
                    List<CategoryEntity> catelog2List = list(new QueryWrapper<CategoryEntity>().eq("parent_cid", catId));
                    List<Catelog2Vo> catelog2VoList = null;
                    if (catelog2List != null) {
                        catelog2VoList = catelog2List.stream().map(c2 -> {
                            Long c2Id = c2.getCatId();

                            List<CategoryEntity> catelog3List = list(new QueryWrapper<CategoryEntity>().eq("parent_cid", c2Id));
                            List<Catelog2Vo.Catalog3Vo> catalog3VoList = null;
                            if (catelog3List != null) {
                                catalog3VoList = catelog3List.stream().map(c3 -> new Catelog2Vo.Catalog3Vo(
                                        c2.getCatId().toString(), c3.getCatId().toString(), c3.getName()
                                )).collect(Collectors.toList());
                            }

                            return new Catelog2Vo(c.getCatId().toString(), catalog3VoList, c2Id.toString(), c2.getName());
                        }).collect(Collectors.toList());
                    }

                    return catelog2VoList;
                }
        ));

    }
}