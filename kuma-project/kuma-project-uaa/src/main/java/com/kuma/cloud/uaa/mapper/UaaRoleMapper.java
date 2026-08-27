package com.kuma.cloud.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.uaa.domain.entity.UaaRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UaaRoleMapper extends BaseMapper<UaaRole> {

    @Select("SELECT * FROM uaa_role WHERE code = #{code}")
    UaaRole selectByCode(@Param("code") String code);

    @Select("""
            SELECT r.code FROM uaa_role r
            INNER JOIN uaa_user_role ur ON r.id = ur.role_id
            WHERE ur.user_id = #{userId} AND r.status = 1
            ORDER BY r.code
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT r.* FROM uaa_role r
            INNER JOIN uaa_user_role ur ON r.id = ur.role_id
            WHERE ur.user_id = #{userId}
            ORDER BY r.code
            """)
    List<UaaRole> selectRolesByUserId(@Param("userId") Long userId);
}
