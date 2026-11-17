package com.kuma.cloud.lab.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.kuma.cloud.lab.database.DatabaseSupport.*;

/** PostgreSQL-specific MVCC and lock evidence, retained together in one schema per command. */
public final class PostgresConcurrencyLearningDemo {
    private PostgresConcurrencyLearningDemo() {}

    public static void main(String[] args) throws Exception {
        String lesson = args.length == 0 ? "all" : args[0];
        if (!List.of("all", "mvcc", "phantom", "row-locks", "table-locks", "lock-monitor", "serializable").contains(lesson))
            throw new IllegalArgumentException("Choose all|mvcc|phantom|row-locks|table-locks|lock-monitor|serializable");
        if (!"postgresql".equals(System.getProperty("kuma.lab.database.engine", "postgresql")))
            throw new IllegalArgumentException("These lessons require real PostgreSQL; H2 is not supported");
        System.setProperty("kuma.lab.database.lesson", lesson);
        try (LabDatabase database = LabDatabase.open("pg_concurrency", 4, 2000)) {
            HikariDataSource source = database.source();
            try (var connection = source.getConnection()) {
                update(connection, "CREATE TABLE observations(id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, step VARCHAR(80) NOT NULL, details TEXT NOT NULL, observed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            }
            if (selected(lesson, "mvcc")) mvcc(source);
            if (selected(lesson, "phantom")) phantom(source);
            if (selected(lesson, "row-locks")) rowLocks(source);
            if (selected(lesson, "table-locks")) tableLocks(source);
            if (selected(lesson, "lock-monitor")) monitor(source);
            if (selected(lesson, "serializable")) serializable(source);
            try (var connection = source.getConnection()) {
                query(connection, "SELECT step,details,observed_at FROM observations ORDER BY id");
            }
        }
        System.out.println("PostgreSQL concurrency: " + lesson + " passed");
    }

    private static boolean selected(String requested, String lesson) { return requested.equals("all") || requested.equals(lesson); }
    private static void accounts(Connection connection, String table) throws SQLException {
        // Every caller uses a fixed source-code identifier, never an input-derived table name.
        if (!table.matches("[a-z_]+")) throw new IllegalArgumentException("Invalid experiment table");
        update(connection, "CREATE TABLE " + table + "(id INT PRIMARY KEY, balance INT NOT NULL)");
        update(connection, "INSERT INTO " + table + " VALUES(1,100),(2,50)");
    }
    private static void evidence(Connection connection, String step, Object details) throws SQLException {
        update(connection, "INSERT INTO observations(step,details) VALUES(?,?)", step, details.toString());
        System.out.println("EVIDENCE " + step + " -> " + details);
    }
    @FunctionalInterface private interface SqlWork { void run() throws SQLException; }
    private static void unavailable(Connection connection, String label, SqlWork action) throws SQLException {
        try { action.run(); throw new AssertionError("Expected lock rejection: " + label); }
        catch (SQLException expected) {
            check(label, "55P03".equals(expected.getSQLState()), expected.getSQLState());
            connection.rollback(); // A PostgreSQL error aborts this transaction until rollback.
        }
    }

    private static void mvcc(HikariDataSource source) throws Exception {
        System.out.println("--- MVCC：同一主键的旧版本、新版本与快照 ---");
        try (var reader = source.getConnection(); var writer = source.getConnection(); var observer = source.getConnection()) {
            accounts(observer, "mvcc_accounts");
            reader.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); reader.setAutoCommit(false);
            writer.setAutoCommit(false);
            try {
                var before = query(reader, "SELECT id,balance,xmin::text AS xmin,ctid::text AS ctid FROM mvcc_accounts WHERE id=1");
                var snapshot = query(reader, "SELECT pg_current_snapshot()::text AS snapshot");
                update(writer, "UPDATE mvcc_accounts SET balance=110 WHERE id=1");
                check("普通读仍读旧值，不等待该行未提交更新", number(observer, "SELECT balance FROM mvcc_accounts WHERE id=1") == 100, 100);
                writer.commit();
                var old = query(reader, "SELECT id,balance,xmin::text AS xmin,ctid::text AS ctid FROM mvcc_accounts WHERE id=1");
                var fresh = query(observer, "SELECT id,balance,xmin::text AS xmin,ctid::text AS ctid FROM mvcc_accounts WHERE id=1");
                check("RR旧快照与新语句看到不同版本", before.equals(old)
                        && ((Number) fresh.getFirst().get("balance")).intValue() == 110
                        && !old.getFirst().get("xmin").equals(fresh.getFirst().get("xmin")), "old=" + old + ", fresh=" + fresh);
                evidence(observer, "mvcc-versions", "snapshot=" + snapshot + ", old=" + old + ", fresh=" + fresh);
                reader.commit();
                check("结束旧事务后读取新版本", number(reader, "SELECT balance FROM mvcc_accounts WHERE id=1") == 110, 110);
            } finally { reader.rollback(); writer.rollback(); }
        }
    }

    private static void phantom(HikariDataSource source) throws Exception {
        System.out.println("--- MVCC：RC与RR对新插入行的可见性 ---");
        for (int isolation : new int[] {Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ}) {
            String table = isolation == Connection.TRANSACTION_READ_COMMITTED ? "phantom_rc" : "phantom_rr";
            try (var reader = source.getConnection(); var writer = source.getConnection()) {
                accounts(writer, table);
                reader.setTransactionIsolation(isolation); reader.setAutoCommit(false);
                try {
                    int before = number(reader, "SELECT COUNT(*) FROM " + table + " WHERE balance>=50");
                    update(writer, "INSERT INTO " + table + " VALUES(3,75)");
                    int after = number(reader, "SELECT COUNT(*) FROM " + table + " WHERE balance>=50");
                    check("RC新语句看新行，PostgreSQL RR保持原集合", before == 2 && after == (table.equals("phantom_rc") ? 3 : 2), table + ": " + before + " -> " + after);
                    evidence(writer, table, "before=" + before + ", sameTransaction=" + after);
                    reader.commit();
                    check("新事务都看到三行", number(reader, "SELECT COUNT(*) FROM " + table + " WHERE balance>=50") == 3, 3);
                } finally { reader.rollback(); }
            }
        }
    }

    private static void rowLocks(HikariDataSource source) throws Exception {
        System.out.println("--- 行锁：不同记录、NOWAIT与SKIP LOCKED ---");
        try (var holder = source.getConnection(); var other = source.getConnection()) {
            accounts(holder, "row_accounts");
            holder.setAutoCommit(false); other.setAutoCommit(false);
            try {
                query(holder, "SELECT id FROM row_accounts WHERE id=1 FOR UPDATE");
                check("行锁不阻止另一连接普通SELECT", number(other, "SELECT balance FROM row_accounts WHERE id=1") == 100, 100);
                unavailable(other, "NOWAIT拒绝竞争同一行", () -> query(other, "SELECT id FROM row_accounts WHERE id=1 FOR UPDATE NOWAIT"));
                var available = query(other, "SELECT id FROM row_accounts ORDER BY id FOR UPDATE SKIP LOCKED");
                check("SKIP LOCKED只返回未锁行2", available.size() == 1 && ((Number) available.getFirst().get("id")).intValue() == 2, available);
                check("锁住行1时仍能更新行2", update(other, "UPDATE row_accounts SET balance=balance+5 WHERE id=2") == 1, 1);
                other.commit(); holder.commit();
                evidence(other, "row-locks", "NOWAIT=55P03; skipped id=1; id=2 updated independently");
                other.commit();
            } finally { holder.rollback(); other.rollback(); }
        }
    }

    private static void tableLocks(HikariDataSource source) throws Exception {
        System.out.println("--- 表锁：SHARE允许普通读，ACCESS EXCLUSIVE阻止读 ---");
        try (var holder = source.getConnection(); var other = source.getConnection(); var observer = source.getConnection()) {
            accounts(observer, "table_accounts");
            holder.setAutoCommit(false); other.setAutoCommit(false);
            int pid = number(holder, "SELECT pg_backend_pid()");
            try {
                update(holder, "LOCK TABLE table_accounts IN SHARE MODE");
                check("SHARE表锁允许普通SELECT", number(other, "SELECT COUNT(*) FROM table_accounts") == 2, 2);
                var share = query(observer, "SELECT mode,granted FROM pg_locks WHERE pid=? AND relation='table_accounts'::regclass AND locktype='relation'", pid);
                check("观测到已授予的ShareLock", share.stream().anyMatch(row -> row.get("mode").equals("ShareLock") && Boolean.TRUE.equals(row.get("granted"))), share);
                unavailable(other, "SHARE与写操作的ROW EXCLUSIVE冲突", () -> update(other, "UPDATE table_accounts SET balance=balance+1 WHERE id=2"));
                holder.commit();
                update(holder, "LOCK TABLE table_accounts IN ACCESS EXCLUSIVE MODE");
                var exclusive = query(observer, "SELECT mode,granted FROM pg_locks WHERE pid=? AND relation='table_accounts'::regclass AND locktype='relation'", pid);
                unavailable(other, "ACCESS EXCLUSIVE阻止普通SELECT", () -> query(other, "SELECT * FROM table_accounts"));
                holder.commit();
                check("释放表锁后普通读恢复", number(other, "SELECT COUNT(*) FROM table_accounts") == 2, 2);
                other.commit();
                evidence(observer, "table-locks", "SHARE=" + share + "; ACCESS EXCLUSIVE=" + exclusive);
            } finally { holder.rollback(); other.rollback(); }
        }
    }

    private static void monitor(HikariDataSource source) throws Exception {
        System.out.println("--- 阻塞观测：pg_blocking_pids / pg_locks / pg_stat_activity ---");
        try (var holder = source.getConnection(); var waiter = source.getConnection(); var observer = source.getConnection()) {
            accounts(observer, "monitor_accounts");
            holder.setAutoCommit(false); waiter.setAutoCommit(false);
            int holderPid = number(holder, "SELECT pg_backend_pid()"), waiterPid = number(waiter, "SELECT pg_backend_pid()");
            update(waiter, "SET LOCAL lock_timeout = '4s'");
            var workers = Executors.newSingleThreadExecutor();
            try {
                query(holder, "SELECT id FROM monitor_accounts WHERE id=1 FOR UPDATE");
                var blocked = workers.submit(() -> {
                    try {
                        int changed = update(waiter, "UPDATE monitor_accounts SET balance=balance+1 WHERE id=1");
                        waiter.commit(); return changed;
                    } finally { waiter.rollback(); }
                });
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
                while (number(observer, "SELECT CASE WHEN ?=ANY(pg_blocking_pids(?)) THEN 1 ELSE 0 END", holderPid, waiterPid) != 1) {
                    if (System.nanoTime() >= deadline) throw new IllegalStateException("Blocked backend was not observed");
                    TimeUnit.MILLISECONDS.sleep(5);
                }
                var activity = query(observer, "SELECT pid,state,wait_event_type,wait_event,pg_blocking_pids(pid)::text AS blockers FROM pg_stat_activity WHERE pid=?", waiterPid);
                var locks = query(observer, "SELECT pid,locktype,mode,granted FROM pg_locks WHERE pid IN (?,?) ORDER BY pid,locktype,mode", holderPid, waiterPid);
                check("等待者在真实Lock等待且有未授予锁", activity.size() == 1 && "Lock".equals(activity.getFirst().get("wait_event_type"))
                        && number(observer, "SELECT COUNT(*) FROM pg_locks WHERE pid=? AND NOT granted", waiterPid) > 0, activity);
                evidence(observer, "blocking-chain", "holder=" + holderPid + ", waiter=" + waiterPid + ", activity=" + activity + ", locks=" + locks);
                holder.commit();
                check("释放后阻塞任务完成", blocked.get(6, TimeUnit.SECONDS) == 1, 1);
                check("阻塞链已消失且修改提交", number(observer, "SELECT cardinality(pg_blocking_pids(?))", waiterPid) == 0
                        && number(observer, "SELECT balance FROM monitor_accounts WHERE id=1") == 101, 101);
            } finally {
                try { holder.rollback(); }
                finally {
                    workers.shutdownNow();
                    if (!workers.awaitTermination(6, TimeUnit.SECONDS)) throw new IllegalStateException("Blocking worker did not terminate");
                }
            }
        }
    }

    private static void serializable(HikariDataSource source) throws Exception {
        System.out.println("--- MVCC：RR写偏差与SERIALIZABLE冲突拒绝 ---");
        for (int isolation : new int[] {Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_SERIALIZABLE}) {
            String table = isolation == Connection.TRANSACTION_REPEATABLE_READ ? "skew_rr" : "skew_serializable";
            try (var first = source.getConnection(); var second = source.getConnection(); var observer = source.getConnection()) {
                update(observer, "CREATE TABLE " + table + "(id INT PRIMARY KEY,on_duty BOOLEAN NOT NULL)");
                update(observer, "INSERT INTO " + table + " VALUES(1,TRUE),(2,TRUE)");
                first.setTransactionIsolation(isolation); second.setTransactionIsolation(isolation);
                first.setAutoCommit(false); second.setAutoCommit(false);
                try {
                    check("两个事务都读到两人值班", number(first, "SELECT COUNT(*) FROM " + table + " WHERE on_duty") == 2
                            && number(second, "SELECT COUNT(*) FROM " + table + " WHERE on_duty") == 2, table);
                    update(first, "UPDATE " + table + " SET on_duty=FALSE WHERE id=1"); first.commit();
                    boolean rejected = false;
                    try { update(second, "UPDATE " + table + " SET on_duty=FALSE WHERE id=2"); second.commit(); }
                    catch (SQLException expected) {
                        check("仅可串行化分支拒绝写偏差", isolation == Connection.TRANSACTION_SERIALIZABLE && "40001".equals(expected.getSQLState()), expected.getSQLState());
                        rejected = true; second.rollback();
                    }
                    int remaining = number(observer, "SELECT COUNT(*) FROM " + table + " WHERE on_duty");
                    check("RR允许本例写偏差，SERIALIZABLE拒绝一个事务", isolation == Connection.TRANSACTION_REPEATABLE_READ
                            ? !rejected && remaining == 0 : rejected && remaining == 1, "table=" + table + ", remaining=" + remaining);
                    if (rejected) {
                        check("整个事务重试时重新检查业务条件", number(second, "SELECT COUNT(*) FROM " + table + " WHERE on_duty") == 1, "keep doctor 2 on duty");
                        second.commit();
                    }
                    evidence(observer, table, "serializationFailure=" + rejected + ", remainingOnDuty=" + remaining);
                } finally { first.rollback(); second.rollback(); }
            }
        }
    }
}
