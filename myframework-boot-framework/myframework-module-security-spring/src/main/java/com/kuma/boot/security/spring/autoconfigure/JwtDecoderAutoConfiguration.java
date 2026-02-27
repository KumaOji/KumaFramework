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

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.support.function.FuncUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.autoconfigure.cloud.JwtCloudAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.cloud.JwtUriFactory;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.autoconfigure.properties.SecurityProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder;

/**
 * Oauth2ResourceSecurityConfigurer
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/8/25 09:57
 */
@AutoConfiguration(after = JwtCloudAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JwtDecoderAutoConfiguration implements InitializingBean {

    private final SecurityProperties securityProperties;
    private final CacheManager redisCacheManager;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
    private final OAuth2AuthorizationProperties oAuth2AuthorizationProperties;

    public JwtDecoderAutoConfiguration(
            SecurityProperties securityProperties,
            @Autowired(required = false) CacheManager redisCacheManager,
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
            OAuth2AuthorizationProperties oAuth2AuthorizationProperties) {
        this.securityProperties = securityProperties;
        this.redisCacheManager = redisCacheManager;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.oAuth2AuthorizationProperties = oAuth2AuthorizationProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(JwtDecoderAutoConfiguration.class, StarterNameConstants.SECURITY_SPRINGSECURITY_STARTER);
    }

    // @Bean
    // public JWKSource<SecurityContext> jwkSource(OAuth2AuthorizationProperties
    // authorizationProperties)
    //	throws NoSuchAlgorithmException {
    //	OAuth2AuthorizationProperties.Jwk jwk = authorizationProperties.getJwk();
    //	KeyPair keyPair = null;
    //	if (jwk.getCertificate() == Certificate.CUSTOM) {
    //		try {
    //			Resource[] resource = ResourceUtils.getResources(jwk.getJksKeyStore());
    //			if (ArrayUtils.isNotEmpty(resource)) {
    //				KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
    //					resource[0], jwk.getJksStorePassword().toCharArray());
    //				keyPair = keyStoreKeyFactory.getKeyPair(
    //					jwk.getJksKeyAlias(), jwk.getJksKeyPassword().toCharArray());
    //			}
    //		} catch (IOException e) {
    //			LogUtils.error("Read custom certificate under resource folder error!", e);
    //		}
    //	} else {
    //		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    //		keyPairGenerator.initialize(2048);
    //		keyPair = keyPairGenerator.generateKeyPair();
    //	}

    //	RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    //	RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    //	RSAKey rsaKey = new RSAKey.Builder(publicKey)
    //		.privateKey(privateKey)
    //		.keyID(UUID.randomUUID().toString())
    //		.build();
    //	JWKSet jwkSet = new JWKSet(rsaKey);
    //	return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    // }

    /// **
    // * jwt 解码
    // */
    // @Bean
    // public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    //	Set<JWSAlgorithm> jwsAlgs = new HashSet<>();
    //	jwsAlgs.addAll(JWSAlgorithm.Family.RSA);
    //	jwsAlgs.addAll(JWSAlgorithm.Family.EC);
    //	jwsAlgs.addAll(JWSAlgorithm.Family.HMAC_SHA);
    //	ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
    //	JWSKeySelector<SecurityContext> jwsKeySelector =
    //		new JWSVerificationKeySelector<>(jwsAlgs, jwkSource);
    //	jwtProcessor.setJWSKeySelector(jwsKeySelector);
    //	// Override the default Nimbus claims set verifier as NimbusJwtDecoder handles it instead
    //	jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
    //	});
    //	return new NimbusJwtDecoder(jwtProcessor);
    // }

    // private byte[] getKeySpec(String keyValue) {
    //	keyValue = keyValue.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC
    // KEY-----", "");
    //	return Base64.getMimeDecoder().decode(keyValue);
    // }
    // public String readPublicKey() throws IOException {
    //	String key = "spring.security.oauth2.resourceserver.public-key-location";
    //	Resource[] resource =
    // ResourceUtils.getResources(oAuth2AuthorizationProperties.getJwk().getJksPublicKeyStore());
    //	Resource publicKeyLocation = resource[0];
    //	Assert.notNull(publicKeyLocation, "PublicKeyLocation must not be null");
    //	if (!publicKeyLocation.exists()) {
    //		throw new InvalidConfigurationPropertyValueException(key, publicKeyLocation,
    //			"Public key location does not exist");
    //	}
    //	try (InputStream inputStream = publicKeyLocation.getInputStream()) {
    //		return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    //	}
    // }
    @Bean
    @ConditionalOnMissingBean(value = JwtDecoder.class)
    public JwtDecoder jwtDecoder(ObjectProvider<JwtUriFactory> jwtUriFactoryObjectProvider)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        String jwkSetUri = oAuth2ResourceServerProperties.getJwt().getJwkSetUri();

        JwtUriFactory jwtUriFactory = jwtUriFactoryObjectProvider.getIfAvailable();
        if (null != jwtUriFactory) {
            String s = jwtUriFactory.jwkSetUri();
            if (StrUtil.isNotBlank(s)) {
                jwkSetUri = s;
            }
        }

        // RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
        //	.generatePublic(new X509EncodedKeySpec(getKeySpec(readPublicKey())));

        // JwkSetUriJwtDecoderBuilder builder =
        // NimbusJwtDecoder.withJwkSetUri(this.properties.getJwkSetUri())
        //	.jwsAlgorithms(this::jwsAlgorithms);
        // customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        // NimbusJwtDecoder nimbusJwtDecoder = builder.build();
        // String issuerUri = this.properties.getIssuerUri();
        // Supplier<OAuth2TokenValidator<Jwt>> defaultValidator = (issuerUri != null)
        //	? () -> JwtValidators.createDefaultWithIssuer(issuerUri) : JwtValidators::createDefault;
        // nimbusJwtDecoder.setJwtValidator(getValidators(defaultValidator));
        // return nimbusJwtDecoder;

        JwkSetUriJwtDecoderBuilder jwkSetUriJwtDecoderBuilder =
                NimbusJwtDecoder.withJwkSetUri(
                                FuncUtil.predicate(
                                        jwkSetUri,
                                        StrUtil::isBlank,
                                        "http://127.0.0.1:33336/oauth2/jwks"))
                        // .withPublicKey(publicKey)
                        .jwsAlgorithm(SignatureAlgorithm.RS256);

        if (redisCacheManager != null) {
            Cache cache = redisCacheManager.getCache("jwt");
            if (cache != null) {
                jwkSetUriJwtDecoderBuilder.cache(cache);
            }
        }

        NimbusJwtDecoder nimbusJwtDecoder = jwkSetUriJwtDecoderBuilder.build();
        nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefault());
        return nimbusJwtDecoder;
    }
}
