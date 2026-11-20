package com.java1234.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * <p>负责生成登录令牌、解析令牌中的用户信息，供登录鉴权过滤器使用。</p>
 *
 * @author Java1234
 */
@Component
public class JwtUtils {

    /** 签名密钥 */
    @Value("${pet.jwt.secret}")
    private String secret;

    /** 令牌有效期（毫秒） */
    @Value("${pet.jwt.expire}")
    private Long expire;

    /**
     * 生成 JWT 令牌
     *
     * @param userId   用户ID
     * @param username 登录账号
     * @param role     角色：admin / user
     * @return 令牌字符串
     */
    public String createToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        Date now = new Date();
        Date end = new Date(now.getTime() + expire);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(end)
                .signWith(key)
                .compact();
    }

    /**
     * 解析令牌内容
     *
     * @param token 令牌字符串
     * @return 令牌载荷，解析失败返回 null
     */
    public Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌字符串
     * @return 用户ID，解析失败返回 null
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userId = claims.get("userId");
        return userId == null ? null : Long.valueOf(userId.toString());
    }

    /**
     * 从令牌中获取登录账号
     *
     * @param token 令牌字符串
     * @return 登录账号，解析失败返回 null
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object username = claims.get("username");
        return username == null ? null : username.toString();
    }

    /**
     * 从令牌中获取角色
     *
     * @param token 令牌字符串
     * @return 角色，解析失败返回 null
     */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object role = claims.get("role");
        return role == null ? null : role.toString();
    }
}
