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

package com.kuma.boot.core.utils.sql;

import com.kuma.boot.common.model.Callable;
import java.util.HashMap;
import javax.sql.DataSource;

/**
 * 简化使用和事务支持
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:06:04
 */
public class DbUtils {

    /** CONN_TRANSACTION_THEAD_LOCAL */
    private static final ThreadLocal<HashMap<DataSource, DbConn>> CONN_TRANSACTION_THEAD_LOCAL =
            new ThreadLocal<>();

    /**
     * transactionGet
     * @param dataSource dataSource
     * @param action0 action0
     * @param <T> T
     * @return T
     * @since 2021-09-02 20:06:13
     */
    public static <T> T transactionGet(DataSource dataSource, Callable.Func1<T, DbConn> action0) {
        // 若有线程同一个数据源事务,则使用事务
        if (CONN_TRANSACTION_THEAD_LOCAL.get() != null
                && CONN_TRANSACTION_THEAD_LOCAL.get().containsKey(dataSource)) {
            return action0.invoke(CONN_TRANSACTION_THEAD_LOCAL.get().get(dataSource));
        } else {
            return get(dataSource, action0);
        }
    }

    /**
     * transactionCall
     * @param dataSource dataSource
     * @param action0 action0
     * @since 2021-09-02 20:06:30
     */
    public static void transactionCall(DataSource dataSource, Callable.Action1<DbConn> action0) {
        transactionGet(
                dataSource,
                (c) -> {
                    action0.invoke(c);
                    return true;
                });
    }

    /**
     * call
     * @param dataSource dataSource
     * @param action0 action0
     * @since 2021-09-02 20:06:33
     */
    public static void call(DataSource dataSource, Callable.Action1<DbConn> action0) {
        get(
                dataSource,
                (db) -> {
                    action0.invoke(db);
                    return true;
                });
    }

    /**
     * get
     * @param dataSource dataSource
     * @param action0 action0
     * @param <T> T
     * @return T
     * @since 2021-09-02 20:06:36
     */
    public static <T> T get(DataSource dataSource, Callable.Func1<T, DbConn> action0) {
        try (DbConn db2 = new DbConn(dataSource)) {
            return action0.invoke(db2);
        }
    }

    /**
     * transaction
     * @param dataSource dataSource
     * @param level level
     * @param action0 action0
     * @since 2021-09-02 20:06:38
     */
    public static void transaction(DataSource dataSource, int level, Callable.Action0 action0) {
        // 事务嵌套,则以最外层事务优先（事务仅对同一个数据源有效,不同数据源不互相影响）
        if (CONN_TRANSACTION_THEAD_LOCAL.get() != null
                && CONN_TRANSACTION_THEAD_LOCAL.get().containsKey(dataSource)) {
            action0.invoke();
        } else {
            DbConn db = null;
            try {
                // 如果设置事务隔离级别,则开启事务;否则不使用事务。
                if (level > 0) {
                    if (CONN_TRANSACTION_THEAD_LOCAL.get() == null) {
                        CONN_TRANSACTION_THEAD_LOCAL.set(new HashMap<>(16));
                    }
                    CONN_TRANSACTION_THEAD_LOCAL.get().put(dataSource, new DbConn(dataSource));
                    db = CONN_TRANSACTION_THEAD_LOCAL.get().get(dataSource);
                    db.beginTransaction(level);
                }
                action0.invoke();
                if (db != null) {
                    db.commit();
                }
            } catch (Exception e) {
                if (db != null) {
                    db.rollback();
                }
                throw e;
            } finally {
                if (db != null) {
                    db.close();
                    CONN_TRANSACTION_THEAD_LOCAL.set(null);
                }
            }
        }
    }
}
