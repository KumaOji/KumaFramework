package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.model.domain.UserDetail;
import cn.kuma.blog.framework.util.JsonUtil;
import cn.kuma.blog.main.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Token 服务实现类
 * 使用 Redis 存储和管理 Token
 *
 * @author Kuma
 * @version 1.0
 */
@Service
@ConditionalOnBean(RedisTemplate.class)
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_PREFIX = "blog:token:";
    private static final String USER_TOKEN_PREFIX = "blog:user:token:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveToken(String token, UserDetail userDetail, long expireSeconds) {
        String tokenKey = TOKEN_PREFIX + token;
        String userTokenKey = USER_TOKEN_PREFIX + userDetail.getUserID() + ":" + token;

        // 存储 token -> userDetail 映射
        redisTemplate.opsForValue().set(tokenKey, userDetail, expireSeconds, TimeUnit.SECONDS);

        // 存储 userId:token -> token 映射（用于快速查找用户的所有 token）
        redisTemplate.opsForValue().set(userTokenKey, token, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public UserDetail getUserDetailByToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        Object value = redisTemplate.opsForValue().get(tokenKey);
        
        if (value == null) {
            return null;
        }
        
        // 如果已经是 UserDetail 对象，直接返回
        if (value instanceof UserDetail) {
            return (UserDetail) value;
        }
        
        // 如果是字符串，尝试反序列化
        if (value instanceof String) {
            return JsonUtil.fromJson((String) value, UserDetail.class);
        }
        
        // 如果是 Map，转换为 UserDetail
        return JsonUtil.DEFAULT_MAPPER.convertValue(value, UserDetail.class);
    }

    @Override
    public boolean validateToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
    }

    @Override
    public void deleteToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        UserDetail userDetail = getUserDetailByToken(token);
        
        // 删除 token
        redisTemplate.delete(tokenKey);
        
        // 删除用户 token 映射
        if (userDetail != null && userDetail.getUserID() != null) {
            String userTokenKey = USER_TOKEN_PREFIX + userDetail.getUserID() + ":" + token;
            redisTemplate.delete(userTokenKey);
        }
    }

    @Override
    public void refreshToken(String token, long expireSeconds) {
        String tokenKey = TOKEN_PREFIX + token;
        UserDetail userDetail = getUserDetailByToken(token);
        
        if (userDetail != null) {
            // 重新保存 token，更新过期时间
            saveToken(token, userDetail, expireSeconds);
        }
    }

    @Override
    public void deleteTokensByUserId(String userId) {
        // 查找该用户的所有 token key
        String pattern = USER_TOKEN_PREFIX + userId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        
        if (keys != null && !keys.isEmpty()) {
            // 删除所有相关的 token
            for (String userTokenKey : keys) {
                Object tokenObj = redisTemplate.opsForValue().get(userTokenKey);
                if (tokenObj != null) {
                    String token = tokenObj.toString();
                    deleteToken(token);
                }
            }
        }
    }
}
