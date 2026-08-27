package com.kuma.cloud.uaa.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private boolean locked;
    private boolean mfaEnabled;
    private String tenantId;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createTime;

    /** 角色编码，不含 ROLE_ 前缀 */
    private List<String> roles;

    /** 由角色聚合出的权限编码 */
    private List<String> permissions;
}
