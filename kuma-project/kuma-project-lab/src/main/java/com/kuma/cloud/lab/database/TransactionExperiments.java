package com.kuma.cloud.lab.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.kuma.cloud.lab.database.DatabaseSupport.*;

final class TransactionExperiments {
    static void boundaries() throws Exception {
        System.out.println("--- 事务边界：自动提交、显式提交与回滚 ---");
        try (var db = new DatabaseSupport(); var writer = db.connection(); var observer = db.connection()) {
            check("初始自动提交", writer.getAutoCommit(), true);
            update(writer, "UPDATE accounts SET balance=balance-5 WHERE id=1");
            check("自动提交后其他连接立即可读", number(observer, "SELECT balance FROM accounts WHERE id=1") == 95, 95);
            writer.setAutoCommit(false);
            try {
                update(writer, "UPDATE accounts SET balance=balance-10 WHERE id=1");
                check("自身读取未提交值、其他连接读已提交值", number(writer, "SELECT balance FROM accounts WHERE id=1") == 85
                        && number(observer, "SELECT balance FROM accounts WHERE id=1") == 95, "writer=85, observer=95");
                writer.commit();
                check("commit后新语句可见", number(observer, "SELECT balance FROM accounts WHERE id=1") == 85, 85);
                update(writer, "UPDATE accounts SET balance=balance-20 WHERE id=1");
                writer.rollback();
                check("rollback撤销本事务修改", number(observer, "SELECT balance FROM accounts WHERE id=1") == 85, balances(observer));
            } finally { writer.rollback(); }
        }
    }

    static void transfer() throws Exception {
        System.out.println("--- 转账原子性：成功提交与扣款后故障回滚 ---");
        try (var db = new DatabaseSupport(); var connection = db.connection()) {
            connection.setAutoCommit(false);
            try {
                transfer(connection, 30, false); connection.commit();
                check("成功转账双方一起提交", number(connection, "SELECT balance FROM accounts WHERE id=1") == 70
                        && number(connection, "SELECT balance FROM accounts WHERE id=2") == 80, balances(connection));
                try { transfer(connection, 10, true); throw new AssertionError("Expected injected constraint failure"); }
                catch (SQLException expected) {
                    check("扣款后的故障被观察", expected.getSQLState().startsWith("23"), expected.getSQLState());
                    connection.rollback();
                }
                check("回滚恢复双方且不多记审计", number(connection, "SELECT balance FROM accounts WHERE id=1") == 70
                        && number(connection, "SELECT balance FROM accounts WHERE id=2") == 80
                        && number(connection, "SELECT SUM(balance) FROM accounts") == 150
                        && number(connection, "SELECT COUNT(*) FROM audit_log") == 1, balances(connection));
                connection.rollback();
                try { transfer(connection, 1000, false); throw new AssertionError("Expected insufficient balance"); }
                catch (IllegalArgumentException expected) { connection.rollback(); }
                check("余额不足条件更新拒绝扣款", number(connection, "SELECT SUM(balance) FROM accounts") == 150, balances(connection));
            } finally { connection.rollback(); }
        }
    }

    private static void transfer(Connection connection, int amount, boolean failAfterDebit) throws SQLException {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (update(connection, "UPDATE accounts SET balance=balance-? WHERE id=1 AND balance>=?", amount, amount) != 1)
            throw new IllegalArgumentException("Insufficient balance");
        if (failAfterDebit) update(connection, "INSERT INTO accounts VALUES(1,'injected-duplicate',0,0)");
        if (update(connection, "UPDATE accounts SET balance=balance+? WHERE id=2", amount) != 1) throw new SQLException("Recipient missing");
        update(connection, "INSERT INTO audit_log(note) VALUES(?)", "transfer " + amount);
    }

    static void savepoint() throws Exception {
        System.out.println("--- 保存点：回滚局部步骤后继续提交外层事务 ---");
        try (var db = new DatabaseSupport(); var connection = db.connection()) {
            connection.setAutoCommit(false);
            try {
                update(connection, "UPDATE accounts SET balance=balance-10 WHERE id=1");
                var point = connection.setSavepoint("before_optional_step");
                update(connection, "UPDATE accounts SET balance=balance+999 WHERE id=2");
                System.out.println("before rollback-to-savepoint=" + balances(connection));
                connection.rollback(point); connection.releaseSavepoint(point);
                check("保存点撤销局部操作但保留先前扣款", number(connection, "SELECT balance FROM accounts WHERE id=1") == 90
                        && number(connection, "SELECT balance FROM accounts WHERE id=2") == 50, balances(connection));
                update(connection, "UPDATE accounts SET balance=balance+10 WHERE id=2");
                connection.commit();
            } finally { connection.rollback(); }
            try (var observer = db.connection()) {
                check("外层继续提交", number(observer, "SELECT balance FROM accounts WHERE id=1") == 90
                        && number(observer, "SELECT balance FROM accounts WHERE id=2") == 60, balances(observer));
            }
        }
    }

    static void isolation() throws Exception {
        System.out.println("--- 隔离级别：两连接固定读写交错 RC / RR ---");
        for (int level : new int[] {Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ}) {
            try (var db = new DatabaseSupport(); var reader = db.connection(); var writer = db.connection()) {
                reader.setTransactionIsolation(level); reader.setAutoCommit(false); writer.setAutoCommit(false);
                try {
                    int before = number(reader, "SELECT balance FROM accounts WHERE id=1");
                    update(writer, "UPDATE accounts SET balance=110 WHERE id=1");
                    int uncommitted = number(reader, "SELECT balance FROM accounts WHERE id=1");
                    check("两种级别均不读取未提交修改", before == 100 && uncommitted == 100, "level=" + level + ", value=" + uncommitted);
                    writer.commit();
                    int afterCommit = number(reader, "SELECT balance FROM accounts WHERE id=1");
                    int expected = level == Connection.TRANSACTION_READ_COMMITTED ? 110 : 100;
                    check("RC新语句可见 / RR保持本事务读取", afterCommit == expected, "level=" + level + ", value=" + afterCommit);
                    reader.commit();
                    check("新事务读取最新已提交值", number(reader, "SELECT balance FROM accounts WHERE id=1") == 110, 110);
                } finally { reader.rollback(); writer.rollback(); }
            }
        }
        System.out.println("这是当前数据库版本的普通SELECT实验，不推断MySQL快照/当前读、间隙锁或幻读的全部行为。");
    }

    static void optimistic() throws Exception {
        System.out.println("--- 丢失更新与版本条件：两个连接的固定交错 ---");
        try (var db = new DatabaseSupport(); var left = db.connection(); var right = db.connection()) {
            int firstRead = number(left, "SELECT balance FROM accounts WHERE id=1");
            int secondRead = number(right, "SELECT balance FROM accounts WHERE id=1");
            update(left, "UPDATE accounts SET balance=? WHERE id=1", firstRead - 10);
            update(right, "UPDATE accounts SET balance=? WHERE id=1", secondRead - 20);
            check("旧值覆盖导致扣款丢失", number(left, "SELECT balance FROM accounts WHERE id=1") == 80, 80);
            update(left, "UPDATE accounts SET balance=100,version=0 WHERE id=1");
            int firstVersion = number(left, "SELECT version FROM accounts WHERE id=1");
            int staleVersion = number(right, "SELECT version FROM accounts WHERE id=1");
            check("第一个版本更新成功", update(left, "UPDATE accounts SET balance=balance-10,version=version+1 WHERE id=1 AND version=?", firstVersion) == 1, 1);
            check("旧版本更新影响零行", update(right, "UPDATE accounts SET balance=balance-20,version=version+1 WHERE id=1 AND version=?", staleVersion) == 0, 0);
            int refreshed = number(right, "SELECT version FROM accounts WHERE id=1");
            check("重新读取后进行一次有界重试", update(right, "UPDATE accounts SET balance=balance-20,version=version+1 WHERE id=1 AND version=?", refreshed) == 1, 1);
            check("两次扣款均保留", number(left, "SELECT balance FROM accounts WHERE id=1") == 70
                    && number(left, "SELECT version FROM accounts WHERE id=1") == 2, balances(left));
        }
        System.out.println("两连接按明确顺序模拟并发读写历史，不依赖调度概率。实际重试还需重新校验业务条件并限制次数。");
    }

    static void locking() throws Exception {
        System.out.println("--- SELECT FOR UPDATE：锁等待超时、释放与恢复 ---");
        try (var db = new DatabaseSupport(); var holder = db.connection()) {
            holder.setAutoCommit(false);
            var workers = Executors.newSingleThreadExecutor();
            try {
                query(holder, "SELECT id FROM accounts WHERE id=1 FOR UPDATE");
                var waiting = workers.submit(() -> {
                    try (var contender = db.connection()) {
                        contender.setAutoCommit(false);
                        try {
                            update(contender, "UPDATE accounts SET balance=balance+1 WHERE id=1");
                            throw new AssertionError("Expected row lock timeout");
                        } catch (SQLException expected) {
                            boolean postgres = "PostgreSQL".equals(contender.getMetaData().getDatabaseProductName());
                            check("数据库锁等待超时", postgres ? "55P03".equals(expected.getSQLState()) : expected.getErrorCode() == 50200,
                                    "state=" + expected.getSQLState() + ", code=" + expected.getErrorCode());
                            return true;
                        } finally { contender.rollback(); }
                    }
                });
                check("另一线程的写入等待并超时", waiting.get(8, TimeUnit.SECONDS), true);
                holder.commit();
                try (var recovered = db.connection()) {
                    check("释放锁后写入恢复", update(recovered, "UPDATE accounts SET balance=balance+1 WHERE id=1") == 1
                            && number(recovered, "SELECT balance FROM accounts WHERE id=1") == 101, balances(recovered));
                }
            } finally {
                try { holder.rollback(); }
                finally {
                    workers.shutdownNow();
                    if (!workers.awaitTermination(5, TimeUnit.SECONDS)) throw new IllegalStateException("Lock worker did not terminate");
                }
            }
        }
    }
}
