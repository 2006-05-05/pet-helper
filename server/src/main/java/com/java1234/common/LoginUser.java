package com.java1234.common;

import com.java1234.entity.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户工具类
 *
 * <p>从 Spring Security 上下文中获取当前登录账号，供业务层使用。</p>
 *
 * @author Java1234
 */
public class LoginUser {

    /** 工具类禁止实例化 */
    private LoginUser() {
    }

    /**
     * 获取当前登录账号
     *
     * @return 登录账号，未登录返回 null
     */
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * 获取当前登录用户对象
     *
     * @param userService 用户服务
     * @return 当前登录用户，未登录返回 null
     */
    public static SysUser getCurrentUser(com.java1234.service.SysUserService userService) {
        String username = getUsername();
        if (username == null) {
            return null;
        }
        return userService.getByUsername(username);
    }
}
