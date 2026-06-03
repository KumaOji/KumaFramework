package com.kuma.boot.session.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.session.properties.SessionProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.StringUtils;

/**
 * 分布式 Session 自动配置
 *
 * <p>生效条件：{@code kuma.boot.session.enabled=true}，且类路径含 spring-session-data-redis
 * 和 RedisConnectionFactory。
 *
 * <p>该配置在 Spring Boot 自身 {@code SessionAutoConfiguration} 的基础上叠加以下增强：
 * <ul>
 *   <li><b>JSON 序列化</b>：以 Jackson 替换 JDK 序列化，Session 属性存为可读 JSON</li>
 *   <li><b>Cookie 定制</b>：统一设置 Cookie 名称、HttpOnly、Secure、SameSite、Domain</li>
 *   <li><b>并发登录控制</b>（可选）：当 Spring Security 及 indexed Session Repository 均存在时，
 *       自动注册 {@link SpringSessionBackedSessionRegistry}</li>
 * </ul>
 *
 * <p>标准 Session / Redis 配置仍通过 {@code spring.session.*} 和 {@code spring.data.redis.*} 设置：
 * <pre>
 * spring:
 *   session:
 *     store-type: redis
 *     timeout: 1800
 *     redis:
 *       namespace: spring:session
 *       repository-type: indexed   # 开启索引以启用并发登录控制
 * </pre>
 *
 * <p>如需监听 Session 生命周期事件，可在业务代码中注册
 * {@code org.springframework.session.events.SessionCreatedEvent} /
 * {@code SessionExpiredEvent} / {@code SessionDeletedEvent} 的 {@code @EventListener}。
 */
@AutoConfiguration
@EnableConfigurationProperties(SessionProperties.class)
@ConditionalOnProperty(prefix = SessionProperties.PREFIX, name = "enabled", havingValue = "true")
@ConditionalOnClass({RedisSessionRepository.class, RedisConnectionFactory.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SessionAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(SessionAutoConfiguration.class, StarterNameConstants.SESSION_STARTER);
    }

    // -------------------------------------------------------------------------
    // Session 属性 JSON 序列化
    // Spring Session 通过 @Qualifier("springSessionDefaultRedisSerializer") 注入此 Bean
    // -------------------------------------------------------------------------

    /**
     * 以 Jackson JSON 替换 Spring Session 默认的 JDK 序列化器。
     * Session 属性以可读 JSON 存入 Redis，跨 JVM 版本无序列化兼容问题。
     */
    @Bean("springSessionDefaultRedisSerializer")
    @ConditionalOnMissingBean(name = "springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJacksonJsonRedisSerializer(JacksonUtils.MAPPER);
    }

    // -------------------------------------------------------------------------
    // Cookie 定制
    // -------------------------------------------------------------------------

    /**
     * 定制 Session Cookie：名称、HttpOnly、Secure、SameSite、Path、Domain。
     * Spring Session 自动将 {@link CookieSerializer} 类型的 Bean 注入到
     * {@code CookieHttpSessionIdResolver}。
     */
    @Bean
    @ConditionalOnMissingBean(CookieSerializer.class)
    public DefaultCookieSerializer cookieSerializer(SessionProperties properties) {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        SessionProperties.Cookie cookie = properties.getCookie();
        serializer.setCookieName(cookie.getName());
        serializer.setUseHttpOnlyCookie(cookie.isHttpOnly());
        serializer.setUseSecureCookie(cookie.isSecure());
        if (StringUtils.hasText(cookie.getSameSite())) {
            serializer.setSameSite(cookie.getSameSite());
        }
        serializer.setCookiePath(cookie.getPath());
        if (StringUtils.hasText(cookie.getDomain())) {
            serializer.setDomainName(cookie.getDomain());
        }
        return serializer;
    }

    // -------------------------------------------------------------------------
    // Spring Security 并发登录控制（可选）
    // -------------------------------------------------------------------------

    /**
     * 并发 Session 控制注册表（需要 Spring Security + indexed Redis Session Repository）。
     *
     * <p>使用方式（Spring Security 配置）：
     * <pre>
     * &#64;Bean
     * SecurityFilterChain securityFilterChain(HttpSecurity http,
     *         SpringSessionBackedSessionRegistry&#60;?&#62; sessionRegistry) throws Exception {
     *     http.sessionManagement(sm -> sm
     *         .maximumSessions(${kuma.boot.session.max-sessions-per-user})
     *         .sessionRegistry(sessionRegistry));
     *     return http.build();
     * }
     * </pre>
     *
     * <p>前提：{@code spring.session.redis.repository-type=indexed}。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.security.core.session.SessionRegistry")
    static class SecuritySessionConfiguration {

        @Bean
        @ConditionalOnBean(FindByIndexNameSessionRepository.class)
        @ConditionalOnMissingBean
        public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry(
                FindByIndexNameSessionRepository<? extends Session> sessionRepository) {
            return new SpringSessionBackedSessionRegistry<>(sessionRepository);
        }
    }
}
