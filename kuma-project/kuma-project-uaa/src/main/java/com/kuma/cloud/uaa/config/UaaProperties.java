package com.kuma.cloud.uaa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * UAA 业务配置。
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = UaaProperties.PREFIX)
public class UaaProperties {

    public static final String PREFIX = "kuma.uaa";

    /**
     * Issuer Identifier，必须与各业务方 spring.security.oauth2.*.issuer-uri 完全一致，
     * 否则资源服务器校验 iss 声明会失败。
     */
    private String issuer = "http://127.0.0.1:33336";

    private Jwk jwk = new Jwk();

    private Token token = new Token();

    private Captcha captcha = new Captcha();

    private Login login = new Login();

    private Mfa mfa = new Mfa();

    private Admin admin = new Admin();

    /**
     * 启动时需要保证存在的接入客户端，按 clientId 幂等注册/更新。
     */
    private List<Client> clients = new ArrayList<>();

    @Data
    public static class Jwk {

        /**
         * RSA 私钥（PKCS#8 PEM）存放位置。首次启动若文件不存在则自动生成并落盘，
         * 保证重启后 JWKS 的 kid 与公钥不变，业务方缓存的 JWK 无需失效。
         */
        private String privateKeyLocation = "./data/uaa-jwt-rsa-private.pem";

        private int keySize = 2048;
    }

    @Data
    public static class Token {

        private Duration accessTokenTtl = Duration.ofMinutes(30);

        private Duration refreshTokenTtl = Duration.ofDays(30);

        private Duration authorizationCodeTtl = Duration.ofMinutes(5);

        /**
         * false 表示每次刷新都轮换 Refresh Token，旧 Token 立即失效。
         */
        private boolean reuseRefreshTokens = false;
    }

    @Data
    public static class Captcha {

        private boolean enabled = true;

        private int length = 4;

        private int width = 130;

        private int height = 48;

        private Duration ttl = Duration.ofMinutes(2);
    }

    @Data
    public static class Login {

        /**
         * 连续失败达到该次数后锁定账号登录入口。
         */
        private int maxFailCount = 5;

        private Duration lockDuration = Duration.ofMinutes(15);
    }

    @Data
    public static class Mfa {

        /**
         * 全局开关。关闭后登录不再校验 TOTP，且绑定/解绑接口不可用。
         */
        private boolean enabled = false;

        /**
         * 展示在身份验证器 App 中的发行方名称。
         */
        private String issuer = "KumaCloud";

        /**
         * 绑定流程中待确认密钥的暂存时长。
         */
        private Duration bindTtl = Duration.ofMinutes(10);
    }

    @Data
    public static class Admin {

        private boolean initEnabled = true;

        private String username = "admin";

        private String password = "admin123";

        private String nickname = "超级管理员";

        private String email = "admin@kumacloud.top";
    }

    @Data
    public static class Client {

        private String clientId;

        private String clientSecret;

        private String clientName;

        private Set<String> redirectUris = new LinkedHashSet<>();

        private Set<String> postLogoutRedirectUris = new LinkedHashSet<>();

        private Set<String> scopes = new LinkedHashSet<>();

        /**
         * 授权类型，取值为 OAuth2 标准值：authorization_code / refresh_token / client_credentials。
         */
        private Set<String> grantTypes = new LinkedHashSet<>();

        /**
         * 客户端认证方式，取值为 OAuth2 标准值：client_secret_basic / client_secret_post / none。
         */
        private String authenticationMethod = "client_secret_basic";

        /**
         * 第一方应用置 false 可跳过授权确认页，第三方接入建议置 true。
         */
        private boolean requireAuthorizationConsent = false;

        /**
         * 公共客户端（如 SPA、移动端）必须要求 PKCE。
         */
        private boolean requireProofKey = false;
    }
}
