package com.java1234.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 登录鉴权过滤器
 *
 * <p>从请求头中提取 Token，解析出用户信息并写入 Spring Security 上下文，
 * 若令牌无效则放行到后续的授权规则进行拦截。</p>
 *
 * @author Java1234
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** 令牌请求头名称 */
    private static final String TOKEN_HEADER = "Authorization";

    /** 令牌前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";

    /** JWT 工具类 */
    private final JwtUtils jwtUtils;

    /**
     * 构造方法，注入 JWT 工具类
     *
     * @param jwtUtils JWT 工具类
     */
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 执行过滤器逻辑
     *
     * @param request     请求对象
     * @param response    响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 获取请求头中的令牌
        String token = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
            // 解析令牌获取角色
            String role = jwtUtils.getRole(token);
            String username = jwtUtils.getUsername(token);
            if (role != null && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 组装认证信息并写入上下文
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
