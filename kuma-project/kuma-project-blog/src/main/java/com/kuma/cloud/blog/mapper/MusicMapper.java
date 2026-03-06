package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.Music;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MusicMapper extends BaseMapper<Music> {

    int incrementPlayCount(@Param("id") Long id);

    int incrementLikeCount(@Param("id") Long id);
}
