/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.support.generator;

import static com.google.common.collect.Collections2.transform;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.log.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * InsertSQLGenerator
 * @author kuma
 * @version 2023.12
 * @since 2023-12-18 15:19:15
 */
/**
 * InsertSQLGenerator
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class InsertSQLGenerator {

    private static final Joiner COMMA_JOINER = Joiner.on(", ");

    private Connection con;

    private final String tableName;

    public InsertSQLGenerator( String url, String username, String password, String tableName ) {
        try {
            this.con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            LogUtils.error(e);
        }

        this.tableName = tableName;
    }

    public String generateSQL() {
        List<String> columns = getColumns();

        return String.format(
                "insert into %s(%s) values(%s)",
                this.tableName,
                COMMA_JOINER.join(columns),
                COMMA_JOINER.join(Collections.nCopies(columns.size(), "?")));
    }

    public String generateParams() {
        return COMMA_JOINER.join(
                transform(
                        getColumns(),
                        input ->
                                "abc.get"
                                        + CaseFormat.LOWER_UNDERSCORE.to(
                                        CaseFormat.UPPER_CAMEL, input)
                                        + "()"));
    }

    private List<String> getColumns() {
        List<String> columns = Lists.newArrayList();
        try (PreparedStatement ps = this.con.prepareStatement("select * from " + this.tableName);
             ResultSet rs = ps.executeQuery();) {

            ResultSetMetaData rsm = rs.getMetaData();
            for (int i = 1; i <= rsm.getColumnCount(); i++) {
                String columnName = rsm.getColumnName(i);
                System.out.print("Name: " + columnName);
                LogUtils.info(", Type : " + rsm.getColumnClassName(i));
                columns.add(columnName);
            }

        } catch (SQLException e) {
            LogUtils.error(e);
            return new ArrayList<>();
        }

        return columns;
    }

    public void close() {
        try {
            this.con.close();
        } catch (SQLException e) {
            LogUtils.error(e);
        }
    }
}
