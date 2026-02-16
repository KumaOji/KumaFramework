package cn.kuma.blog.main.mapper;

import cn.kuma.blog.framework.mybatisplus.mapper.ExtendMapper;
import cn.kuma.blog.main.domain.entity.Music;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 音乐Mapper接口
 *
 * @author Kuma
 * @version 1.0
 */
@Mapper
public interface MusicMapper extends ExtendMapper<Music> {

    /**
     * 增加播放次数
     *
     * @param id 音乐ID
     * @return 影响行数
     */
    int incrementPlayCount(@Param("id") Long id);

    /**
     * 增加点赞数
     *
     * @param id 音乐ID
     * @return 影响行数
     */
    int incrementLikeCount(@Param("id") Long id);
}
