package com.kuma.cloud.uaa.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * 签名密钥配置。
 *
 * <p>UAA 的 JWKS 一旦变更，所有业务方缓存的公钥与在途 JWT 都会失效，因此私钥必须持久化：
 * 首次启动生成 RSA 密钥对并以 PKCS#8 PEM 落盘，后续启动直接加载，kid 由公钥指纹推导而保持稳定。
 *
 * @author kuma
 */
@Slf4j
@Configuration
public class JwkConfig {

    private static final String PEM_HEADER = "-----BEGIN PRIVATE KEY-----";

    private static final String PEM_FOOTER = "-----END PRIVATE KEY-----";

    @Bean
    public JWKSource<SecurityContext> jwkSource(UaaProperties properties) {
        RSAKey rsaKey = loadOrCreateRsaKey(properties.getJwk());
        log.info("UAA JWKS 就绪，kid={}", rsaKey.getKeyID());
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    /**
     * 标记 @Primary：kuma-boot-starter-security-spring 的 JwtDecoderAutoConfiguration 也可能
     * 注册一个基于远端 jwk-set-uri 的 JwtDecoder，UAA 作为签发方必须使用本地密钥的解码器。
     */
    @Bean
    @Primary
    public JwtDecoder uaaJwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    private RSAKey loadOrCreateRsaKey(UaaProperties.Jwk jwk) {
        Path location = Path.of(jwk.getPrivateKeyLocation()).toAbsolutePath();
        RSAPrivateCrtKey privateKey = Files.exists(location)
                ? readPrivateKey(location)
                : writePrivateKey(location, generateKeyPair(jwk.getKeySize()));
        try {
            RSAPublicKeySpec publicKeySpec =
                    new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
            return new RSAKey.Builder(
                            (java.security.interfaces.RSAPublicKey)
                                    KeyFactory.getInstance("RSA").generatePublic(publicKeySpec))
                    .privateKey(privateKey)
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyIDFromThumbprint()
                    .build();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException exception) {
            throw new IllegalStateException("构建 UAA 签名密钥失败: " + location, exception);
        }
    }

    private KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keySize);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("生成 RSA 密钥对失败", exception);
        }
    }

    private RSAPrivateCrtKey readPrivateKey(Path location) {
        try {
            String pem = Files.readString(location, StandardCharsets.UTF_8)
                    .replace(PEM_HEADER, "")
                    .replace(PEM_FOOTER, "")
                    .replaceAll("\\s", "");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem));
            return (RSAPrivateCrtKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new IllegalStateException("加载 UAA 签名私钥失败: " + location, exception);
        }
    }

    private RSAPrivateCrtKey writePrivateKey(Path location, KeyPair keyPair) {
        if (!(keyPair.getPrivate() instanceof RSAPrivateCrtKey privateKey)) {
            throw new IllegalStateException("JDK 返回的 RSA 私钥不含 CRT 参数，无法推导公钥");
        }
        try {
            Files.createDirectories(location.getParent());
            String body = Base64.getMimeEncoder(64, System.lineSeparator().getBytes(StandardCharsets.UTF_8))
                    .encodeToString(privateKey.getEncoded());
            String pem = PEM_HEADER + System.lineSeparator() + body + System.lineSeparator() + PEM_FOOTER;
            Files.writeString(
                    location,
                    pem,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE);
            log.warn("已生成 UAA 签名私钥并写入 {}，请纳入密钥管理并避免提交到代码库", location);
            return privateKey;
        } catch (IOException exception) {
            throw new IllegalStateException("写入 UAA 签名私钥失败: " + location, exception);
        }
    }
}
