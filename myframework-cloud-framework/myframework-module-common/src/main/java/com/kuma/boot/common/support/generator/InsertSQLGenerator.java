/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CaseFormat
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.common.support.generator;

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

public class InsertSQLGenerator {
    private static final Joiner COMMA_JOINER = Joiner.on((String)", ");
    private Connection con;
    private final String tableName;

    public InsertSQLGenerator(String url, String username, String password, String tableName) {
        try {
            this.con = DriverManager.getConnection(url, username, password);
        }
        catch (SQLException e) {
            LogUtils.error(e);
        }
        this.tableName = tableName;
    }

    public String generateSQL() {
        List<String> columns = this.getColumns();
        return String.format("insert into %s(%s) values(%s)", this.tableName, COMMA_JOINER.join(columns), COMMA_JOINER.join(Collections.nCopies(columns.size(), "?")));
    }

    public String generateParams() {
        return COMMA_JOINER.join((Iterable)Collections2.transform(this.getColumns(), input -> "abc.get" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, input) + "()"));
    }

    private List<String> getColumns() {
        ArrayList columns = Lists.newArrayList();
        try (PreparedStatement ps = this.con.prepareStatement("select * from " + this.tableName);
             ResultSet rs = ps.executeQuery();){
            ResultSetMetaData rsm = rs.getMetaData();
            for (int i = 1; i <= rsm.getColumnCount(); ++i) {
                String columnName = rsm.getColumnName(i);
                System.out.print("Name: " + columnName);
                LogUtils.info(", Type : " + rsm.getColumnClassName(i), new Object[0]);
                columns.add(columnName);
            }
        }
        catch (SQLException e) {
            LogUtils.error(e);
            return new ArrayList<String>();
        }
        return columns;
    }

    public void close() {
        try {
            this.con.close();
        }
        catch (SQLException e) {
            LogUtils.error(e);
        }
    }
}

