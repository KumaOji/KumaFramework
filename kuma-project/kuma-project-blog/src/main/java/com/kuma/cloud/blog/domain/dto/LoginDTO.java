package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // TOTP 动态码，仅在用户开启了 MFA 时必填
    private String totpCode;
}
