package cn.kuma.blog.framework.util;

import cn.kuma.blog.common.model.domain.UserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 用于生成和解析 JWT Token
 *
 * @author Kuma
 * @version 1.0
 */
@Component
public class JwtUtil {

    private static String secret;
    private static long expiration;

    @Value("${jwt.secret:jwt-secret-key-minimum-256-bits-long-for-hmac-sha256}")
    public void setSecret(String secret) {
        JwtUtil.secret = secret;
    }

    @Value("${jwt.expiration:86400000}")
    public void setExpiration(long expiration) {
        JwtUtil.expiration = expiration; // 默认 24 小时
    }

    /**
     * 生成 Token
     *
     * @param userDetail 用户信息
     * @return Token 字符串
     */
    public static String generateToken(UserDetail userDetail) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        // 将 UserDetail 对象序列化为 JSON 字符串
        String userDetailJson = JsonUtil.toJson(userDetail);

        return Jwts.builder()
                .subject(userDetail.getUserID() != null ? userDetail.getUserID() : "")
                .claim("userDetail", userDetailJson)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 Token 中解析用户信息
     *
     * @param token Token 字符串
     * @return 用户信息
     */
    public static UserDetail parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 从 Claims 中获取用户信息 JSON 字符串
            Object userDetailObj = claims.get("userDetail");
            if (userDetailObj != null) {
                // 如果是字符串，直接反序列化
                if (userDetailObj instanceof String) {
                    return JsonUtil.fromJson((String) userDetailObj, UserDetail.class);
                } else {
                    // 如果是 Map（旧格式兼容），转换为 UserDetail
                    return JsonUtil.DEFAULT_MAPPER.convertValue(userDetailObj, UserDetail.class);
                }
            }

            // 如果 Token 中没有用户详细信息，创建一个简单的 UserDetail
            UserDetail userDetail = new UserDetail();
            userDetail.setUserID(claims.getSubject());
            return userDetail;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token Token 字符串
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取签名密钥
     */
    private static SecretKey getSigningKey() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT secret 未初始化，请确保 JwtUtil 已被 Spring 容器正确初始化");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // 确保密钥长度至少为 256 位（32 字节）
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            return Keys.hmacShaKeyFor(paddedKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
