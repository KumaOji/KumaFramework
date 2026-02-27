/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.autoconfigure;

// import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
// import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
// import com.alibaba.cloud.nacos.NacosServiceManager;
// import com.alibaba.nacos.api.naming.listener.NamingEvent;
// import com.alibaba.nacos.api.naming.pojo.Instance;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.support.function.FuncUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.autoconfigure.cloud.JwtCloudAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.cloud.JwtUriFactory;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.security.spring.autoconfigure.properties.SecurityProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

/**
 * Oauth2ResourceSecurityConfigurer
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/8/25 09:57
 */
@AutoConfiguration(after = JwtCloudAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveJwtDecoderAutoConfiguration implements InitializingBean {

    public ReactiveJwtDecoderAutoConfiguration(
            SecurityProperties securityProperties,
            CacheManager redisCacheManager,
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties) {
        this.securityProperties = securityProperties;
        this.redisCacheManager = redisCacheManager;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(ReactiveJwtDecoderAutoConfiguration.class, StarterNameConstants.SECURITY_SPRINGSECURITY_STARTER);
    }

    //	@Bean
    //	public JWKSource<SecurityContext> jwkSource(OAuth2AuthorizationProperties
    // authorizationProperties)
    //		throws NoSuchAlgorithmException {
    //		OAuth2AuthorizationProperties.Jwk jwk = authorizationProperties.getJwk();
    //		KeyPair keyPair = null;
    //		if (jwk.getCertificate() == Certificate.CUSTOM) {
    //			try {
    //				Resource[] resource = ResourceUtils.getResources(jwk.getJksKeyStore());
    //				if (ArrayUtils.isNotEmpty(resource)) {
    //					KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
    //						resource[0], jwk.getJksStorePassword().toCharArray());
    //					keyPair = keyStoreKeyFactory.getKeyPair(
    //						jwk.getJksKeyAlias(), jwk.getJksKeyPassword().toCharArray());
    //				}
    //			} catch (IOException e) {
    //				LogUtils.error("Read custom certificate under resource folder error!", e);
    //			}
    //		} else {
    //			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    //			keyPairGenerator.initialize(2048);
    //			keyPair = keyPairGenerator.generateKeyPair();
    //		}
    //
    //		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    //		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    //		RSAKey rsaKey = new RSAKey.Builder(publicKey)
    //			.privateKey(privateKey)
    //			.keyID(UUID.randomUUID().toString())
    //			.build();
    //		JWKSet jwkSet = new JWKSet(rsaKey);
    //		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    //	}
    //
    //	/**
    //	 * jwt 解码
    //	 */
    //	@Bean
    //	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    //		Set<JWSAlgorithm> jwsAlgs = new HashSet<>();
    //		jwsAlgs.addAll(JWSAlgorithm.Family.RSA);
    //		jwsAlgs.addAll(JWSAlgorithm.Family.EC);
    //		jwsAlgs.addAll(JWSAlgorithm.Family.HMAC_SHA);
    //		ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
    //		JWSKeySelector<SecurityContext> jwsKeySelector =
    //			new JWSVerificationKeySelector<>(jwsAlgs, jwkSource);
    //		jwtProcessor.setJWSKeySelector(jwsKeySelector);
    //		// Override the default Nimbus claims set verifier as NimbusJwtDecoder handles it instead
    //		jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
    //		});
    //		return new NimbusJwtDecoder(jwtProcessor);
    //	}

    private final SecurityProperties securityProperties;
    private final CacheManager redisCacheManager;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    //	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:#{null}}")
    //	private String jwkSetUri;

    @Bean
    public ReactiveJwtDecoder jwtDecoder(
            ObjectProvider<JwtUriFactory> jwtUriFactoryObjectProvider) {
        String jwkSetUri = oAuth2ResourceServerProperties.getJwt().getJwkSetUri();

        JwtUriFactory jwtUriFactory = jwtUriFactoryObjectProvider.getIfAvailable();
        if (null != jwtUriFactory) {
            String s = jwtUriFactory.jwkSetUri();
            if (StrUtil.isNotBlank(s)) {
                jwkSetUri = s;
            }
        }

        NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder =
                NimbusReactiveJwtDecoder.withJwkSetUri(
                                FuncUtil.predicate(
                                        jwkSetUri,
                                        StrUtil::isBlank,
                                        "http://127.0.0.1:33336/oauth2/jwks"))
                        .jwsAlgorithm(SignatureAlgorithm.RS256)
                        .build();

        // String issuerUri = null;
        // Supplier<OAuth2TokenValidator<Jwt>> defaultValidator = (issuerUri != null)
        //	? () -> JwtValidators.createDefaultWithIssuer(issuerUri) : JwtValidators::createDefault;
        // nimbusReactiveJwtDecoder.setJwtValidator(defaultValidator.get());

        nimbusReactiveJwtDecoder.setJwtValidator(JwtValidators.createDefault());
        return nimbusReactiveJwtDecoder;

        // return NimbusReactiveJwtDecoder
        //	.withJwkSetUri(FuncUtil.predicate(jwkSetUri, StrUtil::isBlank,
        //		"http://127.0.0.1:33336/oauth2/jwks"))
        //	.build();
    }

    // @Configuration
    // @ConditionalOnClass(name = "com.alibaba.cloud.nacos.NacosServiceManager")
    // @ConditionalOnNacosDiscoveryEnabled
    // public static class ReactiveNimbusJwtDecoderNacosServiceListener implements InitializingBean
    // {
    //
    //	private final NacosServiceManager nacosServiceManager;
    //	private final NacosDiscoveryProperties properties;
    //
    //	public ReactiveNimbusJwtDecoderNacosServiceListener(NacosServiceManager nacosServiceManager,
    //		NacosDiscoveryProperties properties) {
    //		this.nacosServiceManager = nacosServiceManager;
    //		this.properties = properties;
    //	}
    //
    //	@Override
    //	public void afterPropertiesSet() throws Exception {
    //		nacosServiceManager
    //			.getNamingService()
    //			.subscribe(
    //				ServiceName.KUMA_CLOUD_AUTH,
    //				this.properties.getGroup(),
    //				List.of(this.properties.getClusterName()),
    //				event -> {
    //					if (event instanceof NamingEvent) {
    //						List<Instance> instances = ((NamingEvent) event).getInstances();
    //						if (instances.isEmpty()) {
    //							return;
    //						}
    //						Instance instance = instances.get(0);
    //						String jwkSetUri = String.format(
    //							"http://%s:%s" + "/oauth2/jwks", instance.getIp(),
    //							instance.getPort());
    //
    //						NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder =
    //							NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri)
    //								.jwsAlgorithm(SignatureAlgorithm.RS256)
    //								.build();
    //
    //						nimbusReactiveJwtDecoder.setJwtValidator(JwtValidators.createDefault());
    //						ContextUtils.destroySingletonBean("reactiveJwtDecoder");
    //						ContextUtils.registerSingletonBean("reactiveJwtDecoder",
    //							nimbusReactiveJwtDecoder);
    //					}
    //				});
    //	}
    // }
}
