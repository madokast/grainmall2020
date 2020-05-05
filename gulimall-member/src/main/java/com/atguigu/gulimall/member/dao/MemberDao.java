package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author madokast
 * @email 578562554@qq.com
 * @date 2020-05-05 16:30:06
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
