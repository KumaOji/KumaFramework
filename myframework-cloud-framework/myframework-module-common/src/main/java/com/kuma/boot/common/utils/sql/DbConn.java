/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.sql;

import com.kuma.boot.common.utils.common.TimeWatchUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public final class DbConn
implements AutoCloseable {
    private Connection conn;

    public DbConn(Connection conn) {
        this.conn = conn;
    }

    public DbConn() {
        try {
            this.conn = ContextUtils.getBean(DataSource.class, true).getConnection();
        }
        catch (Exception e) {
            throw new DbException("\u83b7\u53d6\u6570\u636e\u5e93\u8fde\u63a5\u5f02\u5e38", "", e);
        }
    }

    public DbConn(DataSource dataSource) {
        try {
            this.conn = dataSource.getConnection();
        }
        catch (Exception e) {
            throw new DbException("\u83b7\u53d6\u6570\u636e\u5e93\u8fde\u63a5\u5f02\u5e38", "", e);
        }
    }

    public DbConn(String url, String user, String password, String driver) {
        try {
            Class.forName(driver);
            this.conn = DriverManager.getConnection(url, user, password);
        }
        catch (Exception e) {
            throw new DbException("\u83b7\u53d6\u6570\u636e\u5e93\u8fde\u63a5\u5f02\u5e38", "", e);
        }
    }

    private boolean getPrintSql() {
        return true;
    }

    @Override
    public void close() {
        TimeWatchUtils.print(this.getPrintSql(), "[db]close", () -> {
            try {
                if (this.conn != null && !this.conn.isClosed()) {
                    this.conn.close();
                }
            }
            catch (Exception e) {
                throw new DbException("close", "", e);
            }
        });
    }

    public void beginTransaction(int level) {
        TimeWatchUtils.print(this.getPrintSql(), "[db]beginTransaction", () -> {
            try {
                if (this.conn != null) {
                    this.conn.setAutoCommit(false);
                    if (level > 0) {
                        this.conn.setTransactionIsolation(level);
                    }
                }
            }
            catch (Exception e) {
                throw new DbException("beginTransaction", "", e);
            }
        });
    }

    public void commit() {
        TimeWatchUtils.print(this.getPrintSql(), "[db]commit", () -> {
            try {
                if (this.conn != null) {
                    this.conn.commit();
                    this.conn.setAutoCommit(true);
                }
            }
            catch (Exception e) {
                throw new DbException("commit", "", e);
            }
        });
    }

    public void rollback() {
        TimeWatchUtils.print(this.getPrintSql(), "[db]rollback", () -> {
            try {
                if (this.conn != null) {
                    this.conn.rollback();
                    this.conn.setAutoCommit(true);
                }
            }
            catch (Exception e) {
                throw new DbException("rollback", "", e);
            }
        });
    }

    public int executeSql(String sql, Object[] parameterValues) {
        return TimeWatchUtils.print(this.getPrintSql(), "[db]" + sql, () -> {
            try {
                PreparedStatement statement = this.conn.prepareStatement(sql);
                this.attachParameterObjects(statement, parameterValues);
                return statement.executeUpdate();
            }
            catch (Exception e) {
                throw new DbException("executeSql", sql, e);
            }
        });
    }

    public Object executeScalar(String sql, Object[] parameterValues) {
        Object object;
        block9: {
            Object value = null;
            ResultSet rs = this.executeResultSet(sql, parameterValues);
            try {
                if (rs != null && rs.next()) {
                    value = rs.getObject(1);
                }
                object = value;
                if (rs == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    throw new DbException("executeScalar", sql, e);
                }
            }
            rs.close();
        }
        return object;
    }

    public ResultSet executeResultSet(String sql, Object[] parameterValues) {
        return TimeWatchUtils.print(this.getPrintSql(), "[db]" + sql, () -> {
            try {
                PreparedStatement statement = this.conn.prepareStatement(sql);
                this.attachParameterObjects(statement, parameterValues);
                ResultSet rs = statement.executeQuery();
                return rs;
            }
            catch (Exception e) {
                throw new DbException("executeResultSet", sql, e);
            }
        });
    }

    public List<Map<String, Object>> executeList(String sql, Object[] parameterValues) {
        return TimeWatchUtils.print(this.getPrintSql(), "[db]" + sql, () -> {
            try {
                PreparedStatement statement = this.conn.prepareStatement(sql);
                this.attachParameterObjects(statement, parameterValues);
                List<Map<String, Object>> map = null;
                try (ResultSet rs = statement.executeQuery();){
                    map = this.toMapList(rs);
                }
                return map;
            }
            catch (Exception e) {
                throw new DbException("executeResultSet", sql, e);
            }
        });
    }

    public List<Map<String, Object>> toMapList(ResultSet rs) {
        try {
            ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            if (rs != null && !rs.isClosed()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                int rowsCount = -1;
                while (rs.next()) {
                    HashMap<String, Object> map = rowsCount > 0 ? new HashMap<String, Object>(rowsCount) : new HashMap();
                    for (int i = 1; i <= colCount; ++i) {
                        String key = meta.getColumnName(i);
                        Object value = rs.getObject(i);
                        map.put(key, value);
                    }
                    rowsCount = map.size();
                    list.add(map);
                }
            }
            return list;
        }
        catch (Exception exp) {
            throw new DbException("toMapList", "", exp);
        }
    }

    private void attachParameterObjects(PreparedStatement statement, Object[] values) throws Exception {
        if (values != null) {
            for (int i = 0; i < values.length; ++i) {
                if (values[i] instanceof Date) {
                    statement.setObject(i + 1, new Timestamp(((Date)values[i]).getTime()));
                    continue;
                }
                statement.setObject(i + 1, values[i]);
            }
        }
    }

    public boolean tableIsExist(String tablename) {
        List<Map<String, Object>> ds = this.executeList("Select name from sysobjects where Name=?", new Object[]{tablename});
        return ds != null && ds.size() != 0;
    }

    public Connection getConn() {
        return this.conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public static class DbException
    extends RuntimeException {
        public DbException(String message, String sql, Exception exp) {
            super(message, exp);
            LogUtils.error("\u9519\u8befsql:" + sql, new Object[0]);
        }
    }
}

