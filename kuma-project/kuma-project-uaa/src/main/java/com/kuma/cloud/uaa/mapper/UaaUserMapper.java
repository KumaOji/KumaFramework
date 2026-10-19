package com.kuma.cloud.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.uaa.domain.entity.UaaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface UaaUserMapper extends BaseMapper<UaaUser> {

    @Select("SELECT * FROM uaa_user WHERE username = #{username}")
    @Results({
            @Result(column = "locked", property = "lockStatus"),
            @Result(column = "mfa_enabled", property = "mfaStatus")
    })
    UaaUser selectByUsername(@Param("username") String username);

    @Update("""
            UPDATE uaa_user
            SET last_login_at = #{loginAt}, last_login_ip = #{loginIp}
            WHERE id = #{userId}
            """)
    int updateLastLogin(
            @Param("userId") Long userId,
            @Param("loginAt") LocalDateTime loginAt,
            @Param("loginIp") String loginIp);
}
