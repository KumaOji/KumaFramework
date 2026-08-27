package com.kuma.cloud.uaa.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class ClientSaveDTO {

    @NotBlank(message = "客户端标识不能为空")
    private String clientId;

    /**
     * 明文密钥，仅在新建或重置时传入；持久化前由 PasswordEncoder 编码。
     * 机密客户端必填，公共客户端（authenticationMethod=none）留空。
     */
    private String clientSecret;

    @NotBlank(message = "客户端名称不能为空")
    private String clientName;

    @NotEmpty(message = "授权类型不能为空")
    private Set<String> grantTypes;

    private String authenticationMethod = "client_secret_basic";

    private Set<String> redirectUris;

    private Set<String> postLogoutRedirectUris;

    @NotEmpty(message = "授权范围不能为空")
    private Set<String> scopes;

    private boolean requireAuthorizationConsent;

    private boolean requireProofKey;
}
