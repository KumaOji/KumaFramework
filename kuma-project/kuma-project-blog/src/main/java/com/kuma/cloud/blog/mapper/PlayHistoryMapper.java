package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.PlayHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlayHistoryMapper extends BaseMapper<PlayHistory> {
}
