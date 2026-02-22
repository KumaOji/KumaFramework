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
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
 *  org.springframework.cache.Cache
 *  org.springframework.cache.CacheManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
 *  org.springframework.security.oauth2.jwt.JwtDecoder
 *  org.springframework.security.oauth2.jwt.JwtValidators
 *  org.springframework.security.oauth2.jwt.NimbusJwtDecoder
 *  org.springframework.security.oauth2.jwt.NimbusJwtDecoder$JwkSetUriJwtDecoderBuilder
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.common.support.function.FuncUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.configuration.cloud.JwtCloudAutoConfiguration;
import com.kuma.boot.security.spring.configuration.cloud.JwtUriFactory;
import com.kuma.boot.security.spring.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.properties.SecurityProperties;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.text.StrValidator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@AutoConfiguration(after={JwtCloudAutoConfiguration.class})
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
public class JwtDecoderAutoConfiguration
implements InitializingBean {
    private final SecurityProperties securityProperties;
    private final CacheManager redisCacheManager;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
    private final OAuth2AuthorizationProperties oAuth2AuthorizationProperties;

    public JwtDecoderAutoConfiguration(SecurityProperties securityProperties, @Autowired(required=false) CacheManager redisCacheManager, OAuth2ResourceServerProperties oAuth2ResourceServerProperties, OAuth2AuthorizationProperties oAuth2AuthorizationProperties) {
        this.securityProperties = securityProperties;
        this.redisCacheManager = redisCacheManager;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.oAuth2AuthorizationProperties = oAuth2AuthorizationProperties;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(JwtDecoderAutoConfiguration.class, (String)"kuma-boot-starter-security-spring", (String[])new String[0]);
    }

    @Bean
    @ConditionalOnMissingBean(value={JwtDecoder.class})
    public JwtDecoder jwtDecoder(ObjectProvider<JwtUriFactory> jwtUriFactoryObjectProvider) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Cache cache;
        String s;
        String jwkSetUri = this.oAuth2ResourceServerProperties.getJwt().getJwkSetUri();
        JwtUriFactory jwtUriFactory = (JwtUriFactory)jwtUriFactoryObjectProvider.getIfAvailable();
        if (null != jwtUriFactory && StrUtil.isNotBlank((CharSequence)(s = jwtUriFactory.jwkSetUri()))) {
            jwkSetUri = s;
        }
        NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder jwkSetUriJwtDecoderBuilder = NimbusJwtDecoder.withJwkSetUri((String)((String)FuncUtil.predicate((Object)jwkSetUri, StrValidator::isBlank, (Object)"http://127.0.0.1:33336/oauth2/jwks"))).jwsAlgorithm(SignatureAlgorithm.RS256);
        if (this.redisCacheManager != null && (cache = this.redisCacheManager.getCache("jwt")) != null) {
            jwkSetUriJwtDecoderBuilder.cache(cache);
        }
        NimbusJwtDecoder nimbusJwtDecoder = jwkSetUriJwtDecoderBuilder.build();
        nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefault());
        return nimbusJwtDecoder;
    }
}

