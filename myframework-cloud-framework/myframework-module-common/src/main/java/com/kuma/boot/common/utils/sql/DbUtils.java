/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.sql;

import com.kuma.boot.common.model.Callable;
import com.kuma.boot.common.utils.sql.DbConn;
import java.util.HashMap;
import javax.sql.DataSource;

public class DbUtils {
    private static final ThreadLocal<HashMap<DataSource, DbConn>> CONN_TRANSACTION_THEAD_LOCAL = new ThreadLocal();

    public static <T> T transactionGet(DataSource dataSource, Callable.Func1<T, DbConn> action0) {
        if (CONN_TRANSACTION_THEAD_LOCAL.get() != null && CONN_TRANSACTION_THEAD_LOCAL.get().containsKey(dataSource)) {
            return action0.invoke(CONN_TRANSACTION_THEAD_LOCAL.get().get(dataSource));
        }
        return DbUtils.get(dataSource, action0);
    }

    public static void transactionCall(DataSource dataSource, Callable.Action1<DbConn> action0) {
        DbUtils.transactionGet(dataSource, c -> {
            action0.invoke((DbConn)c);
            return true;
        });
    }

    public static void call(DataSource dataSource, Callable.Action1<DbConn> action0) {
        DbUtils.get(dataSource, db -> {
            action0.invoke((DbConn)db);
            return true;
        });
    }

    public static <T> T get(DataSource dataSource, Callable.Func1<T, DbConn> action0) {
        try (DbConn db2 = new DbConn(dataSource);){
            T t = action0.invoke(db2);
            return t;
        }
    }

    public static void transaction(DataSource dataSource, int level, Callable.Action0 action0) {
        if (CONN_TRANSACTION_THEAD_LOCAL.get() != null && CONN_TRANSACTION_THEAD_LOCAL.get().containsKey(dataSource)) {
            action0.invoke();
        } else {
            DbConn db = null;
            try {
                if (level > 0) {
                    if (CONN_TRANSACTION_THEAD_LOCAL.get() == null) {
                        CONN_TRANSACTION_THEAD_LOCAL.set(new HashMap(16));
                    }
                    CONN_TRANSACTION_THEAD_LOCAL.get().put(dataSource, new DbConn(dataSource));
                    db = CONN_TRANSACTION_THEAD_LOCAL.get().get(dataSource);
                    db.beginTransaction(level);
                }
                action0.invoke();
                if (db != null) {
                    db.commit();
                }
            }
            catch (Exception e) {
                if (db != null) {
                    db.rollback();
                }
                throw e;
            }
            finally {
                if (db != null) {
                    db.close();
                    CONN_TRANSACTION_THEAD_LOCAL.set(null);
                }
            }
        }
    }
}

