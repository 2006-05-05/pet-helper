package com.java1234.controller;

import com.java1234.common.BusinessException;
import com.java1234.common.Result;
import com.java1234.dto.LoginDTO;
import com.java1234.dto.ProfileDTO;
import com.java1234.dto.RegisterDTO;
import com.java1234.entity.SysUser;
import com.java1234.config.JwtUtils;
import com.java1234.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * <p>提供登录、注册、获取当前登录用户信息等接口，后台与小程序共用。</p>
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** 用户服务 */
    private final SysUserService sysUserService;

    /** JWT 工具类 */
    private final JwtUtils jwtUtils;

    /**
     * 构造方法，注入依赖
     *
     * @param sysUserService 用户服务
     * @param jwtUtils       JWT 工具类
     */
    public AuthController(SysUserService sysUserService, JwtUtils jwtUtils) {
        this.sysUserService = sysUserService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 令牌与用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 查询用户
        SysUser user = sysUserService.getByUsername(loginDTO.getUsername());
        // 校验账号密码（密码为明文存储，固定 123456）
        if (user == null || !user.getPassword().equals(loginDTO.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        // 校验账号状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("该账号已被禁用，请联系管理员");
        }
        // 生成令牌
        String token = jwtUtils.createToken(user.getId(), user.getUsername(), user.getRole());
        // 返回结果中不携带密码
        user.setPassword(null);

        Map<String, Object> data = new HashMap<>(4);
        data.put("token", token);
        data.put("user", user);
        return Result.success(data);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册参数
     * @return 注册后的用户信息
     */
    @PostMapping("/register")
    public Result<SysUser> register(@Valid @RequestBody RegisterDTO registerDTO) {
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        // 密码统一按明文保存
        user.setPassword(registerDTO.getPassword());
        user.setNickname(registerDTO.getNickname());
        user.setPhone(registerDTO.getPhone());
        sysUserService.register(user);
        return Result.success("注册成功", user);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<SysUser> info() {
        String username = com.java1234.common.LoginUser.getUsername();
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 修改当前登录用户的个人资料
     *
     * <p>小程序端「我的-编辑资料」使用该接口，只允许修改自己的资料。</p>
     *
     * @param profileDTO 个人资料
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    public Result<SysUser> updateProfile(@RequestBody ProfileDTO profileDTO) {
        String username = com.java1234.common.LoginUser.getUsername();
        SysUser exist = sysUserService.getByUsername(username);
        if (exist == null) {
            throw new BusinessException("用户不存在或登录状态已失效");
        }
        // 组装需要更新的字段，null 表示不修改该字段
        SysUser user = new SysUser();
        user.setId(exist.getId());
        if (profileDTO.getNickname() != null && !profileDTO.getNickname().trim().isEmpty()) {
            user.setNickname(profileDTO.getNickname().trim());
        }
        if (profileDTO.getAvatar() != null) {
            user.setAvatar(profileDTO.getAvatar());
        }
        if (profileDTO.getPhone() != null) {
            user.setPhone(profileDTO.getPhone().trim());
        }
        if (profileDTO.getGender() != null && !profileDTO.getGender().trim().isEmpty()) {
            user.setGender(profileDTO.getGender().trim());
        }
        if (profileDTO.getCity() != null) {
            user.setCity(profileDTO.getCity().trim());
        }
        if (profileDTO.getIntro() != null) {
            user.setIntro(profileDTO.getIntro().trim());
        }
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(user);
        // 返回更新后的完整信息，供小程序端刷新本地缓存
        SysUser latest = sysUserService.getByUsername(username);
        if (latest != null) {
            latest.setPassword(null);
        }
        return Result.success("资料已更新", latest);
    }
}
