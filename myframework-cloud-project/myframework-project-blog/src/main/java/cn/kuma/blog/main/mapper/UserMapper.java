package cn.kuma.blog.main.mapper;

import cn.kuma.blog.framework.mybatisplus.mapper.ExtendMapper;
import cn.kuma.blog.main.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 *
 * @author Kuma
 * @version 1.0
 */
@Mapper
public interface UserMapper extends ExtendMapper<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User selectByUsername(@Param("username") String username);
}
