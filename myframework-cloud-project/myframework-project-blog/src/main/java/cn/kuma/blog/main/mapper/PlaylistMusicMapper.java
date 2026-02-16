package cn.kuma.blog.main.mapper;

import cn.kuma.blog.framework.mybatisplus.mapper.ExtendMapper;
import cn.kuma.blog.main.domain.entity.PlaylistMusic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 播放列表音乐关联Mapper接口
 *
 * @author Kuma
 * @version 1.0
 */
@Mapper
public interface PlaylistMusicMapper extends ExtendMapper<PlaylistMusic> {

    /**
     * 根据播放列表ID删除所有关联
     *
     * @param playlistId 播放列表ID
     * @return 影响行数
     */
    int deleteByPlaylistId(@Param("playlistId") Long playlistId);

    /**
     * 根据播放列表ID查询音乐ID列表
     *
     * @param playlistId 播放列表ID
     * @return 音乐ID列表
     */
    List<Long> selectMusicIdsByPlaylistId(@Param("playlistId") Long playlistId);
}
