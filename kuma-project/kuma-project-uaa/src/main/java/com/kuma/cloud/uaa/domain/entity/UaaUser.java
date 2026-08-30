package com.kuma.cloud.uaa.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("uaa_user")
public class UaaUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("avatar")
    private String avatar;

    @TableField("status")
    private Integer status;

    @TableField("locked")
    private Integer lockStatus;

    @TableField("mfa_enabled")
    private Integer mfaStatus;

    @TableField("mfa_secret")
    private String mfaSecret;

    @TableField("tenant_id")
    private String tenantId;

    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    public boolean isEnabled() {
        return status != null && status == 1;
    }

    public boolean isLocked() {
        return lockStatus != null && lockStatus == 1;
    }

    public boolean isMfaEnabled() {
        return mfaStatus != null && mfaStatus == 1;
    }
}
