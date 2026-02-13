/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSONObject
 *  com.alibaba.fastjson2.util.TypeUtils
 *  com.google.common.base.CaseFormat
 */
package com.kuma.boot.common.utils.sql;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.TypeUtils;
import com.google.common.base.CaseFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;

public final class JdbcUtils {
    private JdbcUtils() {
    }

    private static List<JSONObject> select0(DataSource dataSource, String preparedSql, Object ... params) throws SQLException {
        try (Connection connection = dataSource.getConnection();){
            List<JSONObject> list = JdbcUtils.selectList(connection, preparedSql, params);
            return list;
        }
    }

    @Deprecated
    public static List<JSONObject> select(DataSource dataSource, String preparedSql, Object ... params) throws SQLException {
        return JdbcUtils.select0(dataSource, preparedSql, params);
    }

    public static List<JSONObject> selectList(DataSource dataSource, String preparedSql, Object ... params) throws SQLException {
        return JdbcUtils.select0(dataSource, preparedSql, params);
    }

    public static <T> List<T> selectList(Class<T> clazz, DataSource dataSource, String preparedSql, Object ... params) throws SQLException {
        List<JSONObject> jsonObjects = JdbcUtils.selectList(dataSource, preparedSql, params);
        return jsonObjects.stream().peek(JdbcUtils::updateKeysToCamelCase).map(x -> TypeUtils.cast((Object)x, (Class)clazz, null)).collect(Collectors.toList());
    }

    public static JSONObject selectOne(DataSource dataSource, String preparedSql, Object ... params) throws SQLException {
        return JdbcUtils.selectList(dataSource, preparedSql, params).stream().findFirst().orElse(null);
    }

    public static <T> T selectOne(Class<T> clazz, DataSource dataSource, String preparedSql, Object ... params) throws SQLException {
        return JdbcUtils.selectList(clazz, dataSource, preparedSql, params).stream().findFirst().orElse(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<JSONObject> select0(Connection connection, String preparedSql, Object ... params) throws SQLException {
        ArrayList<JSONObject> result = new ArrayList<JSONObject>();
        try (ResultSet resultSet = null;
             PreparedStatement preparedStatement = connection.prepareStatement(preparedSql);){
            if (params != null && params.length > 0) {
                int len = params.length;
                for (int i = 0; i < len; ++i) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnLabels = new String[columnCount];
            for (int i = 0; i < columnCount; ++i) {
                columnLabels[i] = metaData.getColumnLabel(i + 1);
            }
            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                for (String columnLabel : columnLabels) {
                    Object columnValue = resultSet.getObject(columnLabel);
                    row.put((Object)columnLabel, columnValue);
                }
                result.add(row);
            }
        }
        return result;
    }

    @Deprecated
    public static List<JSONObject> select(Connection connection, String preparedSql, Object ... params) throws SQLException {
        return JdbcUtils.select0(connection, preparedSql, params);
    }

    public static List<JSONObject> selectList(Connection connection, String preparedSql, Object ... params) throws SQLException {
        return JdbcUtils.select0(connection, preparedSql, params);
    }

    public static <T> List<T> selectList(Class<T> clazz, Connection connection, String preparedSql, Object ... params) throws SQLException {
        List<JSONObject> jsonObjects = JdbcUtils.selectList(connection, preparedSql, params);
        return jsonObjects.stream().peek(JdbcUtils::updateKeysToCamelCase).map(x -> TypeUtils.cast((Object)x, (Class)clazz, null)).collect(Collectors.toList());
    }

    public static JSONObject selectOne(Connection connection, String preparedSql, Object ... params) throws SQLException {
        return JdbcUtils.selectList(connection, preparedSql, params).stream().findFirst().orElse(null);
    }

    public static <T> T selectOne(Class<T> clazz, Connection connection, String preparedSql, Object ... params) throws SQLException {
        return JdbcUtils.selectList(clazz, connection, preparedSql, params).stream().findFirst().orElse(null);
    }

    private static void updateKeysToCamelCase(Map<String, Object> map) {
        ArrayList<String> columns = new ArrayList<String>(map.keySet());
        for (String column : columns) {
            String lowerCase = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column);
            String property = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, lowerCase);
            map.put(property, map.remove(column));
        }
    }
}

