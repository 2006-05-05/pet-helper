package com.java1234.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.common.BusinessException;
import com.java1234.common.PageResult;
import com.java1234.common.Result;
import com.java1234.entity.SysUser;
import com.java1234.service.SysUserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理控制器（管理后台）
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    /** 用户服务 */
    private final SysUserService sysUserService;

    /**
     * 构造方法，注入依赖
     *
     * @param sysUserService 用户服务
     */
    public UserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 分页查询用户列表
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  关键字（账号或昵称）
     * @param role     角色
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(@RequestParam(defaultValue = "1") Long page,
                                            @RequestParam(defaultValue = "10") Long pageSize,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String role) {
        Page<SysUser> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword).or().like(SysUser::getNickname, keyword));
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserService.page(pageInfo, wrapper);
        // 列表不返回密码字段
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 查询用户详情
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<SysUser> detail(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        if (sysUserService.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, user.getUsername())) > 0) {
            throw new BusinessException("该账号已存在");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        if (user.getRole() == null) {
            user.setRole("user");
        }
        user.setId(null);
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.save(user);
        return Result.success("新增成功", null);
    }

    /**
     * 修改用户
     *
     * @param id   用户ID
     * @param user 用户信息
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        SysUser exist = sysUserService.getById(id);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        // 密码为空表示不修改
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(null);
        }
        user.setId(id);
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(user);
        return Result.success("修改成功", null);
    }

    /**
     * 修改用户状态（启用 / 禁用）
     *
     * @param id 用户ID
     * @return 是否成功
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        SysUser update = new SysUser();
        update.setId(id);
        update.setStatus(user.getStatus() != null && user.getStatus() == 1 ? 0 : 1);
        update.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(update);
        return Result.success("操作成功", null);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == 1L) {
            throw new BusinessException("超级管理员账号不允许删除");
        }
        sysUserService.removeById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 查询用户下拉选项（供其它模块使用）
     *
     * @return 用户列表
     */
    @GetMapping("/options")
    public Result<List<SysUser>> options() {
        List<SysUser> users = sysUserService.list(Wrappers.<SysUser>lambdaQuery()
                .select(SysUser::getId, SysUser::getUsername, SysUser::getNickname)
                .orderByAsc(SysUser::getId));
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }
}
