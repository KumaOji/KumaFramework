package com.kuma.cloud.uaa.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class RoleSaveDTO {

    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]{1,63}$", message = "角色编码需为大写字母、数字与下划线，且以字母开头")
    private String code;

    @NotBlank(message = "角色名称不能为空")
    private String name;

    private String description;

    /**
     * 该角色持有的权限编码集合，为 null 表示不调整权限。
     */
    private Set<String> permissionCodes;
}
