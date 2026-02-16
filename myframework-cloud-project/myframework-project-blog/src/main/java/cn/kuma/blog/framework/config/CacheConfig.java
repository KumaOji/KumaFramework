package cn.kuma.blog.framework.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 缓存配置
 * 使用 @Lazy 延迟初始化，优化启动速度
 *
 * @author Kuma
 * @version 1.0
 */
@Configuration
@EnableCaching
@Lazy
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
public class CacheConfig {

    /**
     * 配置 Caffeine CacheManager
     * 默认缓存配置：
     * - 最大条目数：1000
     * - 过期时间：30分钟
     * - 初始容量：100
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * 配置 Caffeine 缓存构建器
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大条目数
                .maximumSize(1000)
                // 写入后过期时间（30分钟）
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 访问后过期时间（30分钟）
                .expireAfterAccess(30, TimeUnit.MINUTES)
                // 启用统计
                .recordStats();
    }
}
