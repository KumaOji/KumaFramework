/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mybatis.mybatisplus.handler.typehandler;

import com.kuma.boot.common.enums.base.CommonEnum;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * CommonEnumTypeHandler
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public abstract class CommonEnumTypeHandler<T extends Enum<T> & CommonEnum> extends BaseTypeHandler<T> {

    private final List<T> commonEnums;

    protected CommonEnumTypeHandler( T[] commonEnums ) {
        this(Arrays.asList(commonEnums));
    }

    protected CommonEnumTypeHandler( List<T> commonEnums ) {
        this.commonEnums = commonEnums;
    }

    @Override
    public void setNonNullParameter(
            PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType )
            throws SQLException {
        preparedStatement.setInt(i, t.getCode());
    }

    @Override
    public T getNullableResult( ResultSet resultSet, String columnName ) throws SQLException {

        int code = resultSet.getInt(columnName);
        return commonEnums.stream()
                .filter(commonEnum -> commonEnum.match(String.valueOf(code)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public T getNullableResult( ResultSet resultSet, int i ) throws SQLException {
        int code = resultSet.getInt(i);
        return commonEnums.stream()
                .filter(commonEnum -> commonEnum.match(String.valueOf(code)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public T getNullableResult( CallableStatement callableStatement, int i ) throws SQLException {
        int code = callableStatement.getInt(i);
        return commonEnums.stream()
                .filter(commonEnum -> commonEnum.match(String.valueOf(code)))
                .findFirst()
                .orElse(null);
    }
}
