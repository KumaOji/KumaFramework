package com.kuma.cloud.lab.p0;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.json.JsonMapper;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static com.kuma.cloud.lab.p0.LabReport.*;

/** Real MySQL/InnoDB evidence. All DDL is confined to kuma_p0_lab and unique per-run tables. */
public final class MysqlAdvancedLearningDemo {
    private static final Semaphore SLOT = new Semaphore(1);
    private MysqlAdvancedLearningDemo() {}
    public static DataSource fromEnvironment() {
        String url = System.getenv("KUMA_LAB_P0_JDBC_URL");
        if (url == null || !url.startsWith("jdbc:mysql:")) throw new IllegalArgumentException("Set KUMA_LAB_P0_JDBC_URL (jdbc:mysql:)");
        var source = new DriverManagerDataSource(url, required("KUMA_LAB_P0_JDBC_USER"), required("KUMA_LAB_P0_JDBC_PASSWORD"));
        var props = new Properties();
        props.setProperty("connectTimeout", "5000"); props.setProperty("socketTimeout", "10000");
        source.setConnectionProperties(props);
        return source;
    }
    private static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Set " + key);
        return value;
    }
    public static void main(String[] args) throws Exception {
        System.out.println(JsonMapper.builder().build().writeValueAsString(run(fromEnvironment(), args.length == 0 ? "all" : args[0])));
    }
    public static LabReport run(DataSource source, String lesson) throws Exception {
        validate(lesson, "all", "indexes", "isolation", "locks", "deadlock", "propagation", "optimistic");
        if (!SLOT.tryAcquire()) throw new IllegalStateException("MySQL 进阶实验正在运行");
        try (var db = new Database(source)) {
            var r = new Builder(lesson);
            r.step("隔离范围", "仅在固定实验库创建本次运行独立表，finally 删除；不修改业务表或全局参数。", db.table);
            if (selected(lesson, "indexes")) indexes(db, r);
            if (selected(lesson, "isolation")) isolation(db, r);
            if (selected(lesson, "locks")) locks(db, r);
            if (selected(lesson, "deadlock")) deadlock(db, r);
            if (selected(lesson, "propagation")) propagation(db, r);
            if (selected(lesson, "optimistic")) optimistic(db, r);
            return r.build();
        } finally { SLOT.release(); }
    }
    public static final class Database implements AutoCloseable {
        final DataSource source;
        final String table = "kuma_p0_lab.p0_" + UUID.randomUUID().toString().replace("-", "");
        public Database(DataSource source) throws SQLException {
            this.source = source;
            try (var c = source.getConnection()) {
                if (!c.getMetaData().getDatabaseProductName().equalsIgnoreCase("MySQL")) throw new IllegalArgumentException("This lesson requires MySQL/InnoDB");
                execute(c, "CREATE DATABASE IF NOT EXISTS kuma_p0_lab");
                execute(c, "CREATE TABLE " + table + " (id INT PRIMARY KEY, dept INT NOT NULL, score INT NOT NULL, payload VARCHAR(80), KEY ix_dept_score(dept,score)) ENGINE=InnoDB");
                try {
                    try (var p = c.prepareStatement("INSERT INTO " + table + " VALUES (?,?,?,?)")) {
                        p.setQueryTimeout(5);
                        for (int i = 1; i <= 200; i++) { p.setInt(1, i * 10); p.setInt(2, i % 10); p.setInt(3, i); p.setString(4, "value-" + i); p.addBatch(); }
                        p.executeBatch();
                    }
                } catch (SQLException | RuntimeException e) {
                    try { execute(c, "DROP TABLE IF EXISTS " + table); } catch (SQLException cleanup) { e.addSuppressed(cleanup); }
                    throw e;
                }
            }
        }
        Session session(int isolation) throws SQLException { return new Session(source, isolation); }
        public void close() throws SQLException {
            try (var c = source.getConnection()) { execute(c, "DROP TABLE IF EXISTS " + table); }
        }
    }
    static final class Session implements AutoCloseable {
        final Connection c;
        final int isolation;
        final boolean auto;
        final int lockWait;
        Session(DataSource source, int level) throws SQLException {
            c = source.getConnection();
            try {
                isolation = c.getTransactionIsolation(); auto = c.getAutoCommit();
                lockWait = number(c, "SELECT @@session.innodb_lock_wait_timeout");
            } catch (SQLException e) { try { c.close(); } catch (SQLException close) { e.addSuppressed(close); } throw e; }
            try { execute(c, "SET SESSION innodb_lock_wait_timeout=1"); c.setTransactionIsolation(level); c.setAutoCommit(false); }
            catch (SQLException e) { try { close(); } catch (SQLException close) { e.addSuppressed(close); } throw e; }
        }
        public void close() throws SQLException {
            SQLException failure = null;
            for (SqlAction action : List.<SqlAction>of(
                    () -> { if (!c.getAutoCommit()) c.rollback(); },
                    () -> execute(c, "SET SESSION innodb_lock_wait_timeout=" + lockWait),
                    () -> c.setTransactionIsolation(isolation), () -> c.setAutoCommit(auto), c::close)) {
                try { action.run(); }
                catch (SQLException e) { if (failure == null) failure = e; else failure.addSuppressed(e); }
            }
            if (failure != null) throw failure;
        }
    }
    @FunctionalInterface private interface SqlAction { void run() throws SQLException; }
    static void execute(Connection c, String sql) throws SQLException {
        try (var s = c.createStatement()) { s.setQueryTimeout(5); s.execute(sql); }
    }
    static int number(Connection c, String sql) throws SQLException {
        try (var s = c.createStatement()) { s.setQueryTimeout(5); try (var rows = s.executeQuery(sql)) { if (!rows.next()) throw new SQLException("No row"); return rows.getInt(1); } }
    }
    static List<Map<String, Object>> query(Connection c, String sql) throws SQLException {
        try (var s = c.createStatement()) {
            s.setQueryTimeout(5);
            try (var rows = s.executeQuery(sql)) {
                var result = new ArrayList<Map<String, Object>>(); var meta = rows.getMetaData();
                while (rows.next()) { var row = new LinkedHashMap<String, Object>(); for (int i = 1; i <= meta.getColumnCount(); i++) row.put(meta.getColumnLabel(i), rows.getObject(i)); result.add(row); }
                return result;
            }
        }
    }
    private static void indexes(Database db, Builder r) throws Exception {
        try (var c = db.source.getConnection()) {
            String covered = "SELECT dept,score FROM " + db.table + " FORCE INDEX(ix_dept_score) WHERE dept=1 AND score>=100";
            String lookup = "SELECT payload FROM " + db.table + " FORCE INDEX(ix_dept_score) WHERE dept=1 AND score>=100";
            var coveringPlan = query(c, "EXPLAIN " + covered);
            var lookupPlan = query(c, "EXPLAIN " + lookup);
            r.check("coveringIndexReported", String.valueOf(coveringPlan.getFirst().get("Extra")).contains("Using index"));
            r.check("payloadRequiresTableLookup", !String.valueOf(lookupPlan.getFirst().get("Extra")).matches(".*Using index(?:;|$).*"));
            r.step("覆盖索引与回表", "强制同一联合索引以稳定比较；dept/score 可由索引覆盖，payload 需要读取聚簇记录。", Map.of("coveredSql", covered, "coveredPlan", coveringPlan, "lookupSql", lookup, "lookupPlan", lookupPlan));
            String prefix = "SELECT * FROM " + db.table + " WHERE dept=1 AND score=101";
            String suffix = "SELECT * FROM " + db.table + " WHERE score=101";
            r.step("联合索引最左前缀", "保留优化器自然选择的实际计划；小表可能全扫，不能断言后缀条件绝不使用索引。", Map.of("prefixSql", prefix, "prefixPlan", query(c, "EXPLAIN " + prefix), "suffixSql", suffix, "suffixPlan", query(c, "EXPLAIN " + suffix)));
            r.check("indexedQueryReturnsExpectedRow", number(c, "SELECT COUNT(*) FROM " + db.table + " WHERE dept=1 AND score=101") == 1);
        }
    }
    private static void isolation(Database db, Builder r) throws Exception {
        for (int level : List.of(Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_READ_COMMITTED)) {
            try (var reader = db.session(level); var writer = db.session(level)) {
                int before = number(reader.c, "SELECT score FROM " + db.table + " WHERE id=10");
                execute(writer.c, "UPDATE " + db.table + " SET score=score+1 WHERE id=10"); writer.c.commit();
                int snapshot = number(reader.c, "SELECT score FROM " + db.table + " WHERE id=10");
                int current = number(reader.c, "SELECT score FROM " + db.table + " WHERE id=10 FOR UPDATE");
                boolean rr = level == Connection.TRANSACTION_REPEATABLE_READ;
                r.check(rr ? "repeatableReadKeepsSnapshot" : "readCommittedSeesNewCommit", snapshot == (rr ? before : before + 1));
                r.check(rr ? "rrLockingReadSeesLatest" : "rcLockingReadSeesLatest", current == before + 1);
                r.step(rr ? "RR / MVCC" : "RC / MVCC", "显式控制两个连接的提交顺序，对照普通快照读与 FOR UPDATE 当前读。", Map.of("before", before, "secondRead", snapshot, "lockingRead", current));
            }
        }
    }
    private static void locks(Database db, Builder r) throws Exception {
        try (var owner = db.session(Connection.TRANSACTION_REPEATABLE_READ); var contender = db.session(Connection.TRANSACTION_REPEATABLE_READ)) {
            query(owner.c, "SELECT id FROM " + db.table + " WHERE id=10 FOR UPDATE");
            expectLockTimeout(contender.c, "UPDATE " + db.table + " SET score=score+1 WHERE id=10", "rowLockBlocksSameRow", r);
            contender.c.rollback();
            execute(contender.c, "UPDATE " + db.table + " SET score=score+1 WHERE id=20"); contender.c.rollback();
            r.check("unrelatedRowRemainsWritable", true);
            owner.c.rollback();
            query(owner.c, "SELECT id FROM " + db.table + " WHERE id>20 AND id<30 FOR UPDATE");
            expectLockTimeout(contender.c, "INSERT INTO " + db.table + " VALUES(25,0,0,'gap')", "rrGapBlocksInsert", r);
            contender.c.rollback(); owner.c.rollback();
            execute(contender.c, "INSERT INTO " + db.table + " VALUES(25,0,0,'gap')"); contender.c.rollback();
            r.check("rollbackReleasesGapLock", true);
            r.step("行锁与间隙锁", "RR 下锁定主键行和空区间，另一连接等待1秒后收到1205；持有者回滚后写入可执行。", Map.of("lockWaitSeconds", 1, "expectedError", 1205));
        }
    }
    private static void expectLockTimeout(Connection c, String sql, String check, Builder r) throws SQLException {
        try { execute(c, sql); throw new IllegalStateException("Expected lock wait timeout"); }
        catch (SQLException e) { if (e.getErrorCode() != 1205) throw e; r.check(check, true); }
    }
    private static void deadlock(Database db, Builder r) throws Exception {
        try (var a = db.session(Connection.TRANSACTION_REPEATABLE_READ); var b = db.session(Connection.TRANSACTION_REPEATABLE_READ)) {
            if (number(a.c, "SELECT @@innodb_deadlock_detect") != 1) throw new IllegalStateException("Deadlock detection disabled; no global settings were changed");
            execute(a.c, "UPDATE " + db.table + " SET score=score+1 WHERE id=10");
            execute(b.c, "UPDATE " + db.table + " SET score=score+1 WHERE id=20");
            var gate = new CyclicBarrier(2);
            var pool = Executors.newFixedThreadPool(2);
            try {
                var first = pool.submit(() -> crossUpdate(a.c, db.table, 20, gate));
                var second = pool.submit(() -> crossUpdate(b.c, db.table, 10, gate));
                var errors = List.of(first.get(5, TimeUnit.SECONDS), second.get(5, TimeUnit.SECONDS));
                r.check("oneDeadlockVictimOneCommit", Collections.frequency(errors, 1213) == 1 && Collections.frequency(errors, 0) == 1);
                r.step("真实死锁", "两个事务先各持有一行，再同步请求对方行；InnoDB 选择一个牺牲者，另一个完成。", Map.of("mysqlErrorCodes", errors));
            } finally { ThreadPoolLearningDemo.stop(pool); }
        }
    }
    private static int crossUpdate(Connection c, String table, int id, CyclicBarrier gate) throws Exception {
        gate.await(5, TimeUnit.SECONDS);
        try { execute(c, "UPDATE " + table + " SET score=score+1 WHERE id=" + id); c.commit(); return 0; }
        catch (SQLException e) { c.rollback(); if (e.getErrorCode() != 1213) throw e; return e.getErrorCode(); }
    }
    private static void propagation(Database db, Builder r) throws Exception {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(DataSource.class, () -> db.source);
            context.registerBean(LessonTable.class, () -> new LessonTable(db.table));
            context.register(TxConfiguration.class); context.refresh();
            var outer = context.getBean(Outer.class); var inner = context.getBean("inner", Inner.class);
            outer.requiredRollback();
            r.check("requiredJoinsOuterRollback", count(db, 3001) == 0 && count(db, 3002) == 0);
            outer.requiresNewRollback();
            r.check("requiresNewSurvivesOuterRollback", count(db, 3011) == 0 && count(db, 3012) == 1);
            outer.nestedRollback();
            r.check("nestedSavepointRollsBackOnlyInner", count(db, 3021) == 1 && count(db, 3022) == 0);
            boolean unexpectedRollback = false;
            try { outer.catchRequiredFailure(); }
            catch (org.springframework.transaction.UnexpectedRollbackException expected) { unexpectedRollback = true; }
            r.check("caughtRequiredFailureStillMarksWholeTransactionRollbackOnly", unexpectedRollback && count(db, 3051) == 0 && count(db, 3052) == 0);
            outer.selfInvocation();
            r.check("selfInvocationBypassesTransactionalProxy", count(db, 3031) == 1);
            try { inner.checkedFailure(); } catch (CheckedFailure expected) { /* default commits */ }
            r.check("checkedExceptionCommitsByDefault", count(db, 3041) == 1);
            try { inner.checkedRollback(); } catch (CheckedFailure expected) { /* explicit rollback */ }
            r.check("rollbackForRollsBackCheckedException", count(db, 3042) == 0);
            r.step("Spring 真实事务代理", "独立 Spring 上下文 + JDBC 事务管理器，对照 REQUIRED / REQUIRES_NEW / NESTED、自调用和受检异常回滚规则；内层REQUIRED失败即使被外层捕获，共享事务仍为rollback-only，外层提交会抛UnexpectedRollbackException。", Map.of("required", "outer+inner rolled back", "requiresNew", "inner committed", "nested", "outer committed", "caughtRequiredFailure", "UnexpectedRollbackException", "selfInvocation", "autocommit"));
        }
    }
    private static int count(Database db, int id) throws SQLException {
        try (var c = db.source.getConnection()) { return number(c, "SELECT COUNT(*) FROM " + db.table + " WHERE id=" + id); }
    }
    private static void optimistic(Database db, Builder r) throws Exception {
        try (var c = db.source.getConnection()) {
            execute(c, "ALTER TABLE " + db.table + " ADD COLUMN version INT NOT NULL DEFAULT 0");
            execute(c, "UPDATE " + db.table + " SET score=100 WHERE id=10");
        }
        try (var a = db.session(Connection.TRANSACTION_READ_COMMITTED); var b = db.session(Connection.TRANSACTION_READ_COMMITTED)) {
            int readA = number(a.c, "SELECT score FROM " + db.table + " WHERE id=10");
            int readB = number(b.c, "SELECT score FROM " + db.table + " WHERE id=10");
            execute(a.c, "UPDATE " + db.table + " SET score=" + (readA - 10) + " WHERE id=10"); a.c.commit();
            execute(b.c, "UPDATE " + db.table + " SET score=" + (readB - 20) + " WHERE id=10"); b.c.commit();
            int lost = number(a.c, "SELECT score FROM " + db.table + " WHERE id=10");
            r.check("readModifyWriteLosesFirstChange", lost == 80);
            execute(a.c, "UPDATE " + db.table + " SET score=100,version=0 WHERE id=10"); a.c.commit();
            int versionA = number(a.c, "SELECT version FROM " + db.table + " WHERE id=10");
            int versionB = number(b.c, "SELECT version FROM " + db.table + " WHERE id=10");
            int accepted = update(a.c, "UPDATE " + db.table + " SET score=score-10,version=version+1 WHERE id=10 AND version=" + versionA); a.c.commit();
            int rejected = update(b.c, "UPDATE " + db.table + " SET score=score-20,version=version+1 WHERE id=10 AND version=" + versionB); b.c.commit();
            r.check("staleVersionRejectedByAffectedRows", accepted == 1 && rejected == 0);
            int currentVersion = number(b.c, "SELECT version FROM " + db.table + " WHERE id=10");
            int retry = update(b.c, "UPDATE " + db.table + " SET score=score-20,version=version+1 WHERE id=10 AND version=" + currentVersion); b.c.commit();
            r.check("boundedRetryPreservesBothChanges", retry == 1 && number(a.c, "SELECT score FROM " + db.table + " WHERE id=10") == 70);
            execute(a.c, "UPDATE " + db.table + " SET score=100 WHERE id=10"); a.c.commit();
            execute(a.c, "UPDATE " + db.table + " SET score=score-10 WHERE id=10 AND score>=10"); a.c.commit();
            execute(b.c, "UPDATE " + db.table + " SET score=score-20 WHERE id=10 AND score>=20"); b.c.commit();
            r.check("conditionalAtomicUpdatePreservesBothChanges", number(a.c, "SELECT score FROM " + db.table + " WHERE id=10") == 70);
            r.step("丢失更新与并发控制", "固定双连接交错：覆盖写丢更新；version条件写通过影响行数识别冲突，重新读取后只重试一次；简单计数可直接用带条件的原子UPDATE。事务隔离不能替代业务并发控制，重试前必须判断操作可否重复。", Map.of("initial", 100, "unsafeFinal", lost, "conflictAffectedRows", rejected, "versionedFinal", 70, "atomicFinal", 70));
        }
    }
    static int update(Connection c, String sql) throws SQLException {
        try (var statement = c.createStatement()) { statement.setQueryTimeout(5); return statement.executeUpdate(sql); }
    }
    public record LessonTable(String name) {}
    public static class CheckedFailure extends Exception {}
    // Registered explicitly in the isolated context; deliberately has no component stereotype.
    @EnableTransactionManagement(proxyTargetClass = true)
    public static class TxConfiguration {
        @Bean PlatformTransactionManager transactionManager(DataSource source) { return new DataSourceTransactionManager(source); }
        @Bean Inner inner(DataSource source, LessonTable table) { return new Inner(source, table); }
        @Bean Outer outer(DataSource source, LessonTable table, Inner inner) { return new Outer(source, table, inner); }
    }
    public static class Inner {
        final JdbcTemplate jdbc; final String table;
        public Inner(DataSource source, LessonTable table) { jdbc = new JdbcTemplate(source); jdbc.setQueryTimeout(5); this.table = table.name(); }
        void insert(int id) { jdbc.update("INSERT INTO " + table + " VALUES (?,0,0,'tx')", id); }
        @Transactional public void required(int id) { insert(id); }
        @Transactional public void requiredFailure(int id) { insert(id); throw new IllegalStateException("required rollback-only"); }
        @Transactional(propagation = Propagation.REQUIRES_NEW) public void requiresNew(int id) { insert(id); }
        @Transactional(propagation = Propagation.NESTED) public void nested(int id) { insert(id); throw new IllegalStateException("nested rollback"); }
        @Transactional public void checkedFailure() throws CheckedFailure { insert(3041); throw new CheckedFailure(); }
        @Transactional(rollbackFor = CheckedFailure.class) public void checkedRollback() throws CheckedFailure { insert(3042); throw new CheckedFailure(); }
    }
    public static class Outer extends Inner {
        private final Inner inner;
        public Outer(DataSource source, LessonTable table, Inner inner) { super(source, table); this.inner = inner; }
        @Transactional public void requiredRollback() { insert(3001); inner.required(3002); org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); }
        @Transactional public void requiresNewRollback() { inner.requiresNew(3012); insert(3011); org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); }
        @Transactional public void nestedRollback() { insert(3021); try { inner.nested(3022); } catch (IllegalStateException expected) { /* savepoint */ } }
        @Transactional public void catchRequiredFailure() { insert(3051); try { inner.requiredFailure(3052); } catch (IllegalStateException expected) { /* shared transaction remains rollback-only */ } }
        public void selfInvocation() { try { this.selfFailure(); } catch (IllegalStateException expected) { /* proxy bypass */ } }
        @Transactional public void selfFailure() {
            if (TransactionSynchronizationManager.isActualTransactionActive()) throw new IllegalStateException("Unexpected transaction");
            insert(3031); throw new IllegalStateException("self rollback bypassed");
        }
    }
}
