package com.kuma.cloud.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.uaa.domain.entity.UaaPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UaaPermissionMapper extends BaseMapper<UaaPermission> {

    /**
     * 查询用户经由角色获得的全部权限码：uaa_user_role → uaa_role_permission → uaa_permission。
     */
    @Select("""
            SELECT DISTINCT p.code FROM uaa_permission p
            INNER JOIN uaa_role_permission rp ON p.id = rp.permission_id
            INNER JOIN uaa_user_role ur ON rp.role_id = ur.role_id
            INNER JOIN uaa_role r ON r.id = ur.role_id
            WHERE ur.user_id = #{userId} AND p.status = 1 AND r.status = 1
            ORDER BY p.code
            """)
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT p.* FROM uaa_permission p
            INNER JOIN uaa_role_permission rp ON p.id = rp.permission_id
            WHERE rp.role_id = #{roleId}
            ORDER BY p.code
            """)
    List<UaaPermission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
}
