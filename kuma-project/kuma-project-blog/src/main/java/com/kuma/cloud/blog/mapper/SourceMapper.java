package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.Source;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SourceMapper extends BaseMapper<Source> {
}
