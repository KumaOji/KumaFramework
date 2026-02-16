package cn.kuma.blog.main.mapper;

import cn.kuma.blog.framework.mybatisplus.mapper.ExtendMapper;
import cn.kuma.blog.main.domain.entity.PlayHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 播放历史Mapper接口
 *
 * @author Kuma
 * @version 1.0
 */
@Mapper
public interface PlayHistoryMapper extends ExtendMapper<PlayHistory> {
}
