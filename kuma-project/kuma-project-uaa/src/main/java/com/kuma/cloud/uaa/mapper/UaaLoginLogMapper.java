package com.kuma.cloud.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.uaa.domain.entity.UaaLoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UaaLoginLogMapper extends BaseMapper<UaaLoginLog> {}
