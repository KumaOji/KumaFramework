package com.kuma.cloud.uaa.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * 客户端视图，不返回密钥密文。
 */
@Data
public class ClientVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String clientId;
    private String clientName;
    private Instant clientIdIssuedAt;
    private Set<String> grantTypes;
    private Set<String> authenticationMethods;
    private Set<String> redirectUris;
    private Set<String> postLogoutRedirectUris;
    private Set<String> scopes;
    private boolean requireAuthorizationConsent;
    private boolean requireProofKey;
    private long accessTokenTtlSeconds;
    private long refreshTokenTtlSeconds;
}
