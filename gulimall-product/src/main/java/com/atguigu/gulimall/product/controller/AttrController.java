package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 商品属性
 *
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-04 23:20:01
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 2020年6月7日
     * spu对应的属性维护
     * @param spuId spu id
     * @return 结果
     */
    // base/listforspu/{spuId}
    @GetMapping("base/listforspu/{spuId}")
    public R baseListForSpu(@PathVariable("spuId") long spuId){

        List<ProductAttrValueEntity> data =
                productAttrValueService.baseListForSpu(spuId);

        return R.ok().put("data", data);
    }


    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId") long spuId,
                           @RequestBody List<ProductAttrValueEntity> productAttrValueEntityList){

        productAttrValueService.updateSpuAttr(spuId,productAttrValueEntityList);

        return R.ok();
    }

    /**
     * 列表
     */
    @Deprecated
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 2020年5月31日
     * 规格属性
     * 高级查询
     * @param params 分页 + 对应三级分类 + key模糊查询
     */
    @GetMapping("/base/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
        PageUtils page = attrService.queryBaseAttr(params,catelogId);

        return R.ok().put("page", page);
    }

    /**
     * 2020年6月1日
     * 销售属性
     * @param params 分页
     * @param catelogId 对应的三级分类
     */
    @GetMapping("/sale/list/{catelogId}")
    public R saleList(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
        PageUtils page = attrService.querySaleAttr(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     * {
     * 	"msg": "success",
     * 	"code": 0,
     * 	"attr": {
     * 		"attrId": 4,
     * 		"attrName": "aad",
     * 		"searchType": 1,
     * 		"valueType": 1,
     * 		"icon": "qq",
     * 		"valueSelect": "v;q;w",
     * 		"attrType": 1,
     * 		"enable": 1,
     * 		"showDesc": 1,
     * 		"attrGroupId": 1, //分组id
     * 		"catelogId": 225, //分类id
     * 		"catelogPath": [2, 34, 225] //分类完整路径
     *        }
     * }
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attr = attrService.getAttrRespVoById(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     * 2020年5月31日 把接收的数据改为 AttrVo
     * 因为 前端发的数据 和 数据库的 entity 不完全一致
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
