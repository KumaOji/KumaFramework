package com.kuma.cloud.lab.runtime;

import com.zaxxer.hikari.HikariDataSource;
import com.kuma.cloud.lab.database.LabDatabase;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kuma.cloud.lab.runtime.RuntimeLearningDemo.*;

/** Real HikariCP in dedicated PostgreSQL schemas; never uses the application's DataSource. */
public final class ConnectionPoolLearningDemo {
    private ConnectionPoolLearningDemo() {}

    public static void main(String[] args) throws Exception {
        String lesson = args.length == 0 ? "all" : args[0];
        System.setProperty("kuma.lab.database.lesson", lesson);
        switch (lesson) {
            case "reuse" -> reuse();
            case "timeout" -> timeout();
            case "transaction" -> transaction();
            case "capacity" -> capacity();
            case "all" -> { reuse(); timeout(); transaction(); capacity(); }
            default -> throw new IllegalArgumentException("Choose all|reuse|timeout|transaction|capacity");
        }
        System.out.println("Java connection pool: " + lesson + " passed");
    }

    private static void reuse() throws Exception {
        System.out.println("--- 连接池：逻辑 close 与真实物理连接复用 ---");
        try (LabDatabase database = LabDatabase.open("connection_pool", 1, 1000)) {
            HikariDataSource pool = database.source();
            Class<? extends Connection> physicalType = Class.forName(database.postgresql()
                    ? "org.postgresql.jdbc.PgConnection" : "org.h2.jdbc.JdbcConnection").asSubclass(Connection.class);
            Connection first = pool.getConnection();
            Connection physical;
            try (first) {
                physical = first.unwrap(physicalType);
                check("借出连接可执行SQL", scalar(first, "SELECT 42") == 42, 42);
            }
            check("逻辑连接close后标记关闭", first.isClosed(), true);
            try (Connection second = pool.getConnection()) {
                check("新逻辑句柄复用相同物理连接", first != second && physical == second.unwrap(physicalType)
                        && !physical.isClosed(), "different proxy, same physical connection");
            }
        }
        System.out.println("unwrap仅用于本实验观察身份，不直接关闭物理连接；业务仍用try-with-resources归还逻辑句柄。");
    }

    private static void timeout() throws Exception {
        System.out.println("--- 连接池：持有未归还 -> 耗尽超时 -> 归还后恢复 ---");
        try (LabDatabase database = LabDatabase.open("connection_pool", 1, 300)) {
            HikariDataSource pool = database.source();
            try (Connection held = pool.getConnection()) {
                workers(1, workers -> {
                    var result = workers.submit(() -> {
                        long start = System.nanoTime();
                        try (Connection unexpected = pool.getConnection()) { return false; }
                        catch (SQLTransientConnectionException expected) {
                            System.out.println("getConnection timed out; observedNs=" + (System.nanoTime() - start));
                            return true;
                        }
                    });
                    check("耗尽时抛出连接获取超时", get(result), true);
                    check("仍只有1个活动连接", pool.getHikariPoolMXBean().getActiveConnections() == 1, 1);
                });
            }
            try (Connection recovered = pool.getConnection()) {
                check("归还后可再次借出", recovered.isValid(1) && scalar(recovered, "SELECT 42") == 42, 42);
            }
        }
        System.out.println("这是连接获取超时，与SQL执行超时不同；它模拟暂时未归还，不等待泄漏检测日志作为断言。");
    }

    private static void transaction() throws Exception {
        System.out.println("--- 连接池：未提交事务回滚、状态复位与显式提交 ---");
        try (LabDatabase database = LabDatabase.open("connection_pool", 1, 1000)) {
            HikariDataSource pool = database.source();
            int baseline;
            try (Connection setup = pool.getConnection()) {
                execute(setup, "CREATE TABLE entries(id INT PRIMARY KEY)");
                baseline = setup.getTransactionIsolation();
            }
            try (Connection borrowed = pool.getConnection()) {
                borrowed.setAutoCommit(false);
                borrowed.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                execute(borrowed, "INSERT INTO entries VALUES(1)");
                // Return dirty connection: Hikari rolls it back and restores tracked JDBC properties.
            }
            try (Connection reused = pool.getConnection()) {
                check("未提交事务不泄漏到下一借用者", scalar(reused, "SELECT COUNT(*) FROM entries") == 0, 0);
                check("autoCommit与隔离级别恢复", reused.getAutoCommit() && reused.getTransactionIsolation() == baseline,
                        "autoCommit=" + reused.getAutoCommit() + ", isolation=" + reused.getTransactionIsolation());
                reused.setAutoCommit(false);
                execute(reused, "INSERT INTO entries VALUES(2)"); reused.commit();
                execute(reused, "INSERT INTO entries VALUES(3)"); reused.rollback();
                check("显式commit保留、rollback撤销", scalar(reused, "SELECT COUNT(*) FROM entries") == 1, 1);
            }
        }
        System.out.println("这是Hikari对本例JDBC属性的复位，不保证任意SQL会话变量都会复位；具体行为以当前数据库版本为准。");
    }

    private static void capacity() throws Exception {
        System.out.println("--- 线程池 × 连接池：4个工作线程、2个连接 ---");
        try (LabDatabase database = LabDatabase.open("connection_pool", 2, 3000)) {
            HikariDataSource pool = database.source();
            try (Connection setup = pool.getConnection()) { execute(setup, "CREATE TABLE completed(id INT PRIMARY KEY)"); }
            CountDownLatch twoInside = new CountDownLatch(2), release = new CountDownLatch(1);
            AtomicInteger active = new AtomicInteger(), peak = new AtomicInteger();
            workers(4, workers -> {
                ArrayList<Future<Integer>> results = new ArrayList<>();
                try {
                    for (int i = 0; i < 4; i++) {
                        int id = i;
                        results.add(workers.submit(() -> {
                            try (Connection connection = pool.getConnection()) {
                                int now = active.incrementAndGet(); peak.accumulateAndGet(now, Math::max);
                                try {
                                    twoInside.countDown(); await(release);
                                    execute(connection, "INSERT INTO completed VALUES(" + id + ")");
                                    return id;
                                } finally { active.decrementAndGet(); }
                            }
                        }));
                    }
                    await(twoInside);
                    until(() -> pool.getHikariPoolMXBean().getThreadsAwaitingConnection() > 0);
                    check("连接容量使部分工作线程等待", active.get() == 2 && pool.getHikariPoolMXBean().getActiveConnections() == 2,
                            "active=2, waiting=" + pool.getHikariPoolMXBean().getThreadsAwaitingConnection());
                } finally { release.countDown(); }
                for (Future<Integer> result : results) get(result);
            });
            try (Connection verify = pool.getConnection()) {
                check("4个任务完成且连接占用峰值2", scalar(verify, "SELECT COUNT(*) FROM completed") == 4
                        && peak.get() == 2 && active.get() == 0, "rows=4, peak=" + peak.get());
            }
        }
        System.out.println("增加工作线程不增加连接池容量；避免持有外层连接等待另一个需要新连接的嵌套任务。");
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement()) { statement.executeUpdate(sql); }
    }
    private static int scalar(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement(); var rows = statement.executeQuery(sql)) {
            if (!rows.next()) throw new SQLException("Expected one row");
            return rows.getInt(1);
        }
    }
}
