package com.kuma.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.blog.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    List<Post> selectByTitleLike(@Param("title") String title);
}
