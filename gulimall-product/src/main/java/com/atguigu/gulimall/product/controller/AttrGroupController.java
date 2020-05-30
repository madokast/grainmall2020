package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 属性分组
 *
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-04 23:20:01
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取属于某个[商品三级分类]下的所有属性分组
     * [商品三级分类]——即 [Category] 如 手机-手机通讯-手机
     * [属性分组]——即多个商品的属性(手机尺寸、手机重量..)会合为一组，如[手机规格]
     * 2020年5月28日 修改
     *
     * @param params 分页参数
     *               {
     *               page: 1,//当前页码
     *               limit: 10,//每页记录数
     *               sidx: 'id',//排序字段
     *               order: 'asc/desc',//排序方式
     *               key: '华为'//检索关键字 —— 全字段模糊查询
     *               }
     * @return 相应数据，见下
     * <pre>
     * {
     * 	"msg": "success",
     * 	"code": 0,
     * 	"page": {
     * 		"totalCount": 0, //总记录数
     * 		"pageSize": 10,  //每页大小
     * 		"totalPage": 0,  //总页码
     * 		"currPage": 1, //当前页码
     * 		"list": [{  //当前页所有数据
     * 			"brandId": 1,
     * 			"name": "aaa",
     * 			"logo": "abc",
     * 			"descript": "华为",
     * 			"showStatus": 1,
     * 			"firstLetter": null,
     * 			"sort": null
     *                }]    * 	}
     * }
     * </pre>
     */
    //http://192.168.2.13:20088/api/product/attrgroup/list/1?page=1&key=aa
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId) {

        PageUtils page =  attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();

        List<Long> catelogIdPath =  categoryService.findCatelogIdPath(catelogId);

        attrGroup.setCatelogIdPath(catelogIdPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
