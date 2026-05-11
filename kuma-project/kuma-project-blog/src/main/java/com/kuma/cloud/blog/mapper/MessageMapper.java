package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    int incrementLikeCount(@Param("id") Long id);

    List<Message> selectApprovedRepliesByParentIds(@Param("parentIds") List<Long> parentIds);
}
