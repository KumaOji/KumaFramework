package com.kuma.cloud.reference.mapper;

import com.kuma.boot.data.mybatis.mybatisplus.query.BaseMapperX;
import com.kuma.cloud.reference.domain.entity.OrderDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapperX<OrderDO> {}
