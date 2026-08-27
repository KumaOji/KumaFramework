package com.kuma.cloud.uaa.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserSaveDTO {

    @NotBlank(message = "登录名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]{3,64}$", message = "登录名仅支持字母、数字、下划线、点与中划线，长度 3-64")
    private String username;

    /**
     * 新建用户时必填；修改用户时留空表示不改密码。
     */
    @Size(min = 8, max = 64, message = "密码长度需为 8-64")
    private String password;

    @Size(max = 64, message = "昵称长度不能超过 64")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String avatar;

    /**
     * 角色编码集合，不含 {@code ROLE_} 前缀。
     */
    private Set<String> roleCodes;
}
