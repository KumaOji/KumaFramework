package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户的完整生效权限，按来源分层，便于管理界面区分「角色继承」与「单独授权」。
 */
@Data
public class UserAuthoritiesVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色码列表，如 ROLE_ADMIN / ROLE_USER */
    private List<String> roles;
    /** 由角色继承得到的权限码，如 article:* */
    private List<String> rolePermissions;
    /** 单独授予该用户的权限码（sys_user_permission） */
    private List<String> directPermissions;
}
