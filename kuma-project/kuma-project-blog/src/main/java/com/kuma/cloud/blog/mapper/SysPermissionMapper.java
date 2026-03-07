package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 查询用户通过角色获得的所有权限码
     * 路径：user → sys_user_role → sys_role_permission → sys_permission
     */
    @Select("""
            SELECT p.code FROM sys_permission p
            INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
            INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
            WHERE ur.user_id = #{userId}
            """)
    List<String> selectRolePermissionsByUserId(Long userId);

    /**
     * 查询用户直接被授予的权限码（授权用户专用）
     * 路径：user → sys_user_permission → sys_permission
     */
    @Select("""
            SELECT p.code FROM sys_permission p
            INNER JOIN sys_user_permission up ON p.id = up.permission_id
            WHERE up.user_id = #{userId}
            """)
    List<String> selectDirectPermissionsByUserId(Long userId);

    /**
     * 查询用户所属的角色码列表
     */
    @Select("""
            SELECT r.code FROM sys_role r
            INNER JOIN sys_user_role ur ON r.id = ur.role_id
            WHERE ur.user_id = #{userId}
            """)
    List<String> selectRoleCodesByUserId(Long userId);
}
