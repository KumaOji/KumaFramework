package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.FriendLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FriendLinkMapper extends BaseMapper<FriendLink> {

    int incrementViewCount(@Param("id") Long id);
}
