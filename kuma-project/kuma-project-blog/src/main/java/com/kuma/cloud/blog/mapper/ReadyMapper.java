package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.ReadyItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReadyMapper extends BaseMapper<ReadyItem> {

    int deleteByIdLogic(@Param("id") Long id);

    int deleteByIdPhysical(@Param("id") Long id);
}
