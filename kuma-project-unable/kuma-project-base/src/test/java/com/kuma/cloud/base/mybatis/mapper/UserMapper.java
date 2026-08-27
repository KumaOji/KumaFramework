package com.kuma.cloud.base.mybatis.mapper;

import com.kuma.cloud.base.mybatis.entity.User;

import java.util.List;

/**
 * UserMapper 接口，对应 resources/mybatis/mapper/UserMapper.xml
 *
 * <p>XML 中声明了 {@code <cache/>}，所有 select 语句默认加入二级缓存；
 * 所有 insert/update/delete 语句默认清空该 namespace 的二级缓存。
 */
public interface UserMapper {

    User selectById(Long id);

    List<User> selectAll();

    int updateAge(@org.apache.ibatis.annotations.Param("id") Long id,
                  @org.apache.ibatis.annotations.Param("age") Integer age);

    int deleteById(Long id);
}
