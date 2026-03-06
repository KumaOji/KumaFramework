package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.PlaylistMusic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaylistMusicMapper extends BaseMapper<PlaylistMusic> {

    int deleteByPlaylistId(@Param("playlistId") Long playlistId);

    List<Long> selectMusicIdsByPlaylistId(@Param("playlistId") Long playlistId);
}
