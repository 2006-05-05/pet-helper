package com.java1234.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 安全配置类
 *
 * <p>配置跨域、关闭 CSRF、注册 JWT 过滤器并放行公共访问路径。</p>
 *
 * @author Java1234
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** JWT 登录鉴权过滤器 */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 构造方法，注入 JWT 过滤器
     *
     * @param jwtAuthenticationFilter JWT 登录鉴权过滤器
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 密码编码器（虽然本系统密码为明文，但仍保留以便扩展）
     *
     * @return 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置过滤器链
     *
     * @param http HttpSecurity
     * @return 过滤器链
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离项目关闭 CSRF
                .csrf(csrf -> csrf.disable())
                // 允许跨域请求
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 使用无状态会话，不创建 HttpSession
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 关闭默认登录页与 httpBasic 弹窗
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                // 授权规则
                .authorizeHttpRequests(auth -> auth
                        // 公共访问路径
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/uploads/**",
                                "/api/pet/list",
                                "/api/pet/detail/**",
                                "/api/category/list",
                                "/api/notice/list",
                                "/api/notice/detail/**",
                                "/api/comment/list/**",
                                "/api/stats/overview",
                                "/error"
                        ).permitAll()
                        // 管理后台接口需要管理员角色
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 其余接口需要登录
                        .anyRequest().authenticated())
                // 注册 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 跨域配置
     *
     * @return 跨域配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源（开发环境），便于微信小程序与前端本地调试
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
