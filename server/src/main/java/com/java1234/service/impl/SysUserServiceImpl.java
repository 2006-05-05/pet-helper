package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.java1234.common.BusinessException;
import com.java1234.entity.SysUser;
import com.java1234.mapper.SysUserMapper;
import com.java1234.service.SysUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 *
 * @author Java1234
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 根据登录账号查询用户
     *
     * @param username 登录账号
     * @return 用户信息，不存在返回 null
     */
    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username), false);
    }

    /**
     * 注册普通用户
     *
     * @param sysUser 用户信息
     * @return 是否注册成功
     */
    @Override
    public boolean register(SysUser sysUser) {
        // 判断账号是否已存在
        if (this.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, sysUser.getUsername())) > 0) {
            throw new BusinessException("该账号已存在，请更换用户名");
        }
        // 新注册用户统一为普通用户、正常状态
        sysUser.setId(null);
        sysUser.setRole("user");
        sysUser.setStatus(1);
        if (sysUser.getNickname() == null || sysUser.getNickname().isEmpty()) {
            sysUser.setNickname(sysUser.getUsername());
        }
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        return this.save(sysUser);
    }

    /**
     * 获取用户列表
     *
     * @param ids 用户ID集合
     * @return 用户列表
     */
    @Override
    public List<SysUser> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return super.listByIds(ids);
    }
}
