package com.kuma.boot.sign.autoconfigure;

import com.kuma.boot.sign.properties.SignProperties;
import com.kuma.boot.sign.web.ApiSignatureInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 {@link ApiSignatureInterceptor} 到 Spring MVC 拦截器链
 *
 * <p>拦截器对全部路径注册，由其内部依据 {@code @ApiSignature} 注解决定是否验签。
 */
@AutoConfiguration(after = SignAutoConfiguration.class)
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnBean(ApiSignatureInterceptor.class)
@ConditionalOnProperty(prefix = SignProperties.PREFIX, name = "enabled", havingValue = "true")
public class SignWebMvcConfiguration implements WebMvcConfigurer {

    private final ApiSignatureInterceptor apiSignatureInterceptor;

    public SignWebMvcConfiguration(ApiSignatureInterceptor apiSignatureInterceptor) {
        this.apiSignatureInterceptor = apiSignatureInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiSignatureInterceptor).addPathPatterns("/**");
    }
}
