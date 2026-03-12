package com.kuma.cloud.blog.domain.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RevokePermissionRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "权限ID不能为空")
    private Long permissionId;
}
