package cn.kuma.blog.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 登录限流配置：对 POST /auth/login 按 IP 限流，防暴力破解。
 *
 * @author Kuma
 * @version 1.0
 */
@Configuration
public class LoginRateLimitConfig {

    @Value("${blog.login-rate-limit.max-attempts:5}")
    private int maxAttempts;

    @Value("${blog.login-rate-limit.window-seconds:60}")
    private long windowSeconds;

    @Bean
    @ConditionalOnProperty(name = "blog.login-rate-limit.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<LoginRateLimitFilter> loginRateLimitFilterRegistrationBean() {
        FilterRegistrationBean<LoginRateLimitFilter> bean = new FilterRegistrationBean<>(
                new LoginRateLimitFilter(maxAttempts, windowSeconds));
        bean.addUrlPatterns("/auth/login");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return bean;
    }
}
