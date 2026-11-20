package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 *
 * @author Java1234
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
