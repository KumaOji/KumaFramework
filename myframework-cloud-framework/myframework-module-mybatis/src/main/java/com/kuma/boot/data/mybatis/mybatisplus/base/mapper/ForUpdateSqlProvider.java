package com.kuma.boot.data.mybatis.mybatisplus.base.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

import java.util.Map;

/**
 * ForUpdateSqlProvider
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class ForUpdateSqlProvider<T> {

    public String selectForUpdate( final Map<String, Object> params ) {
        final Wrapper<?> wrapper = (Wrapper<?>) params.get("ew");
        final String sql = wrapper.getCustomSqlSegment();
        return sql + " FOR UPDATE";
    }
}
