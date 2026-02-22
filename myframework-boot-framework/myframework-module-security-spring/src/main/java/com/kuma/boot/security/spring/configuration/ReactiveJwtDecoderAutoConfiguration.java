/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.support.function.FuncUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.dromara.hutool.core.text.StrUtil
 *  org.dromara.hutool.core.text.StrValidator
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
 *  org.springframework.cache.CacheManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
 *  org.springframework.security.oauth2.jwt.JwtValidators
 *  org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
 *  org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.common.support.function.FuncUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.configuration.cloud.JwtCloudAutoConfiguration;
import com.kuma.boot.security.spring.configuration.cloud.JwtUriFactory;
import com.kuma.boot.security.spring.properties.SecurityProperties;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.text.StrValidator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@AutoConfiguration(after={JwtCloudAutoConfiguration.class})
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveJwtDecoderAutoConfiguration
implements InitializingBean {
    private final SecurityProperties securityProperties;
    private final CacheManager redisCacheManager;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    public ReactiveJwtDecoderAutoConfiguration(SecurityProperties securityProperties, CacheManager redisCacheManager, OAuth2ResourceServerProperties oAuth2ResourceServerProperties) {
        this.securityProperties = securityProperties;
        this.redisCacheManager = redisCacheManager;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(ReactiveJwtDecoderAutoConfiguration.class, (String)"kuma-boot-starter-security-spring", (String[])new String[0]);
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(ObjectProvider<JwtUriFactory> jwtUriFactoryObjectProvider) {
        String s;
        String jwkSetUri = this.oAuth2ResourceServerProperties.getJwt().getJwkSetUri();
        JwtUriFactory jwtUriFactory = (JwtUriFactory)jwtUriFactoryObjectProvider.getIfAvailable();
        if (null != jwtUriFactory && StrUtil.isNotBlank((CharSequence)(s = jwtUriFactory.jwkSetUri()))) {
            jwkSetUri = s;
        }
        NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder = NimbusReactiveJwtDecoder.withJwkSetUri((String)((String)FuncUtil.predicate((Object)jwkSetUri, StrValidator::isBlank, (Object)"http://127.0.0.1:33336/oauth2/jwks"))).jwsAlgorithm(SignatureAlgorithm.RS256).build();
        nimbusReactiveJwtDecoder.setJwtValidator(JwtValidators.createDefault());
        return nimbusReactiveJwtDecoder;
    }
}

