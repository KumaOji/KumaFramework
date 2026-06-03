package com.kuma.boot.sign.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sign.algorithm.HmacSha256SignAlgorithm;
import com.kuma.boot.sign.algorithm.Md5SignAlgorithm;
import com.kuma.boot.sign.algorithm.SignAlgorithm;
import com.kuma.boot.sign.core.SignatureValidator;
import com.kuma.boot.sign.properties.SignProperties;
import com.kuma.boot.sign.provider.AppSecretProvider;
import com.kuma.boot.sign.provider.PropertiesAppSecretProvider;
import com.kuma.boot.sign.store.InMemoryNonceStore;
import com.kuma.boot.sign.store.NonceStore;
import com.kuma.boot.sign.web.ApiSignatureInterceptor;
import com.kuma.boot.sign.web.SignBodyCachingFilter;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * API 签名验签自动配置
 *
 * <p>生效条件：{@code kuma.boot.sign.enabled=true}。
 * AppSecret 数据源 / nonce 存储均以 {@link ConditionalOnMissingBean} 暴露，
 * 业务可注册自定义 {@link AppSecretProvider}（DB 查询）或 {@link NonceStore}（Redis）覆盖默认实现。
 */
@AutoConfiguration
@EnableConfigurationProperties(SignProperties.class)
@ConditionalOnProperty(prefix = SignProperties.PREFIX, name = "enabled", havingValue = "true")
public class SignAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(SignAutoConfiguration.class, "kuma-boot-starter-sign", new String[0]);
    }

    @Bean
    public HmacSha256SignAlgorithm hmacSha256SignAlgorithm() {
        return new HmacSha256SignAlgorithm();
    }

    @Bean
    public Md5SignAlgorithm md5SignAlgorithm() {
        return new Md5SignAlgorithm();
    }

    @Bean
    @ConditionalOnMissingBean(AppSecretProvider.class)
    public AppSecretProvider appSecretProvider(SignProperties properties) {
        return new PropertiesAppSecretProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean(NonceStore.class)
    public NonceStore nonceStore() {
        return new InMemoryNonceStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public SignatureValidator signatureValidator(SignProperties properties,
                                                 AppSecretProvider appSecretProvider,
                                                 NonceStore nonceStore,
                                                 List<SignAlgorithm> algorithms) {
        return new SignatureValidator(properties, appSecretProvider, nonceStore, algorithms);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiSignatureInterceptor apiSignatureInterceptor(SignatureValidator validator) {
        return new ApiSignatureInterceptor(validator);
    }

    @Bean
    @ConditionalOnMissingBean
    public SignBodyCachingFilter signBodyCachingFilter() {
        return new SignBodyCachingFilter();
    }

    @Bean
    public FilterRegistrationBean<SignBodyCachingFilter> signBodyCachingFilterRegistration(
            SignBodyCachingFilter filter) {
        FilterRegistrationBean<SignBodyCachingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }
}
