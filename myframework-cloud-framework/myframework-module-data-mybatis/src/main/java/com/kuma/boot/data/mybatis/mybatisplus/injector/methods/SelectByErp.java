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

package com.kuma.boot.data.mybatis.mybatisplus.injector.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;

/**
 * 新增一个通用sql
 */
public class SelectByErp extends AbstractMethod {
    // 需要查询的列名
    private final String erpColumn = "erp";
    // sql方法名
    public static final String method = "selectByErp";
    // sql模板
    private final String sqlTemplate = "SELECT %s FROM %s WHERE %s=#{%s} %s";

    /**
     * @param methodName 方法名
     * @since 3.5.0
     */
    public SelectByErp(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(
            Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        // 获取需要查询的字段名及属性名
        TableFieldInfo erpFiled = getErpProperty(tableInfo);
        // 拼接组装sql
        SqlSource sqlSource =
                new RawSqlSource(
                        configuration,
                        String.format(
                                sqlTemplate,
                                sqlSelectColumns(tableInfo, false),
                                tableInfo.getTableName(),
                                erpFiled.getColumn(),
                                erpFiled.getProperty(),
                                tableInfo.getLogicDeleteSql(true, false)),
                        Object.class);
        return this.addSelectMappedStatementForTable(mapperClass, method, sqlSource, tableInfo);
    }

    /**
     * 查询erp列信息
     */
    private TableFieldInfo getErpProperty(TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        return fieldList.stream()
                .filter(filed -> filed.getColumn().equals(erpColumn))
                .findFirst()
                .get();
    }
}
