package com.kuma.cloud.reference.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/** 仅测试拦截器的拒绝行为；不会进入生产 JAR，也没有 HTTP 入口。 */
@Mapper
public interface UnsafeOrderMapper {
    @Update("UPDATE purchase_orders SET order_status = 'CONFIRMED'")
    int updateWithoutWhere();
}
