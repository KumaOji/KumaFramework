package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户列表展示对象（脱敏，不含 password / totpSecret）。
 */
@Data
public class UserBriefVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private String phone;
    /** 0=普通 1=管理员 */
    private Integer isAdmin;
    /** 0=禁用 1=正常 */
    private Integer status;
    /** 是否已启用 TOTP 二次验证 */
    private Integer totpEnabled;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}
