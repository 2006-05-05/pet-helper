package com.java1234.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.java1234.entity.SysUser;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author Java1234
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据登录账号查询用户
     *
     * @param username 登录账号
     * @return 用户信息，不存在返回 null
     */
    SysUser getByUsername(String username);

    /**
     * 注册普通用户
     *
     * @param sysUser 用户信息
     * @return 是否注册成功
     */
    boolean register(SysUser sysUser);

    /**
     * 获取用户列表（小程序端按 ID 集合查询昵称等）
     *
     * @param ids 用户ID集合
     * @return 用户列表
     */
    List<SysUser> listByIds(List<Long> ids);
}
