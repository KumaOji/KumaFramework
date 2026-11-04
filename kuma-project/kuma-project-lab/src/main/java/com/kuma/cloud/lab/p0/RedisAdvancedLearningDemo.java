package com.kuma.cloud.lab.p0;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import tools.jackson.databind.json.JsonMapper;
import javax.sql.DataSource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import static com.kuma.cloud.lab.p0.LabReport.*;

/** Real Redis commands and Lua, with real isolated MySQL reads for cache lessons. */
public final class RedisAdvancedLearningDemo {
    private static final Semaphore SLOT = new Semaphore(1);
    private static final Duration TTL = Duration.ofSeconds(60);
    private static final String UNLOCK = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
    private static final String RENEW = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('pexpire',KEYS[1],ARGV[2]) else return 0 end";
    private final StringRedisTemplate redis;
    private final String prefix = "lab:p0:{" + UUID.randomUUID() + "}:";
    private final Set<String> keys = ConcurrentHashMap.newKeySet();
    public RedisAdvancedLearningDemo(StringRedisTemplate redis) { this.redis = redis; }
    public static LettuceConnectionFactory fromEnvironment() {
        String host = System.getenv("KUMA_LAB_P0_REDIS_HOST");
        if (host == null || host.isBlank()) throw new IllegalArgumentException("Set KUMA_LAB_P0_REDIS_HOST");
        var settings = new RedisStandaloneConfiguration(host, Integer.parseInt(System.getenv().getOrDefault("KUMA_LAB_P0_REDIS_PORT", "6379")));
        String password = System.getenv("KUMA_LAB_P0_REDIS_PASSWORD");
        if (password != null && !password.isEmpty()) settings.setPassword(password);
        String username = System.getenv("KUMA_LAB_P0_REDIS_USERNAME");
        if (username != null && !username.isBlank()) settings.setUsername(username);
        settings.setDatabase(Integer.parseInt(System.getenv().getOrDefault("KUMA_LAB_P0_REDIS_DATABASE", "0")));
        var factory = new LettuceConnectionFactory(settings, LettuceClientConfiguration.builder().commandTimeout(Duration.ofSeconds(3)).shutdownTimeout(Duration.ofSeconds(1)).build());
        factory.afterPropertiesSet(); return factory;
    }
    public static void main(String[] args) throws Exception {
        var factory = fromEnvironment();
        try {
            String lesson = args.length == 0 ? "all" : args[0];
            var source = List.of("all", "cache", "consistency", "fencing").contains(lesson) ? MysqlAdvancedLearningDemo.fromEnvironment() : null;
            System.out.println(JsonMapper.builder().build().writeValueAsString(new RedisAdvancedLearningDemo(new StringRedisTemplate(factory)).run(source, lesson)));
        } finally { factory.destroy(); }
    }
    public LabReport run(DataSource source, String lesson) throws Exception {
        validate(lesson, "all", "cache", "avalanche", "consistency", "locks", "lua", "fencing");
        if (!SLOT.tryAcquire()) throw new IllegalStateException("Redis 进阶实验正在运行");
        try {
            var r = new Builder(lesson);
            r.step("隔离范围", "仅访问本次前缀的键，每个键带TTL，finally精确删除；不执行 FLUSHDB 或 KEYS。", prefix);
            if (selected(lesson, "cache") || selected(lesson, "consistency") || selected(lesson, "fencing")) {
                if (source == null) throw new IllegalArgumentException("Cache lessons require a real MySQL DataSource");
                try (var db = new MysqlAdvancedLearningDemo.Database(source)) {
                    if (selected(lesson, "cache")) cache(db, r);
                    if (selected(lesson, "consistency")) consistency(db, r);
                    if (selected(lesson, "fencing")) fencing(db, r);
                }
            }
            if (selected(lesson, "avalanche")) avalanche(r);
            if (selected(lesson, "locks")) locks(r);
            if (selected(lesson, "lua")) lua(r);
            return r.build();
        } finally {
            try { if (!keys.isEmpty()) redis.delete(keys); }
            finally { SLOT.release(); }
        }
    }
    private String key(String suffix) { String key = prefix + suffix; keys.add(key); return key; }
    private void set(String key, String value) { redis.opsForValue().set(key, value, TTL); }
    private long script(String code, String key, String... args) {
        Long result = redis.execute(new DefaultRedisScript<>(code, Long.class), List.of(key), (Object[]) args);
        if (result == null) throw new IllegalStateException("No Lua result"); return result;
    }
    private String readDatabase(MysqlAdvancedLearningDemo.Database db, boolean missing, AtomicInteger reads) throws Exception {
        reads.incrementAndGet();
        try (var c = db.source.getConnection()) {
            var rows = MysqlAdvancedLearningDemo.query(c, "SELECT score FROM " + db.table + " WHERE id=" + (missing ? -1 : 10));
            return rows.isEmpty() ? "<NULL>" : String.valueOf(rows.getFirst().get("score"));
        }
    }
    private void cache(MysqlAdvancedLearningDemo.Database db, Builder r) throws Exception {
        String missing = key("missing"); var reads = new AtomicInteger();
        for (int i = 0; i < 5; i++) if (redis.opsForValue().get(missing) == null) readDatabase(db, true, reads);
        int without = reads.get(); reads.set(0);
        for (int i = 0; i < 5; i++) if (redis.opsForValue().get(missing) == null) set(missing, readDatabase(db, true, reads));
        r.check("negativeCachingReducesDatabaseReads", without == 5 && reads.get() == 1);
        r.step("缓存穿透", "同一不存在ID请求5次；短TTL空值缓存把真实数据库查询从5次降为1次，生产需考虑新建数据使空值失效。", Map.of("withoutNegativeCache", without, "withNegativeCache", reads.get()));
        String hot = key("hot"); String lock = key("hot-lock");
        int unprotected = hotWave(db, hot, lock, false);
        redis.delete(hot);
        int protectedReads = hotWave(db, hot, lock, true);
        r.check("mutexRecheckPreventsDuplicateRebuild", unprotected == 6 && protectedReads == 1);
        r.step("热点击穿", "6个请求经屏障同时确认缓存未命中；对照直接查库与 SET NX 锁内二次检查。", Map.of("unprotectedDbReads", unprotected, "lockedDbReads", protectedReads));
    }
    private int hotWave(MysqlAdvancedLearningDemo.Database db, String hot, String lock, boolean protectedMode) throws Exception {
        var reads = new AtomicInteger(); var barrier = new CyclicBarrier(6); var pool = Executors.newFixedThreadPool(6);
        try {
            var tasks = new ArrayList<Future<String>>();
            for (int i = 0; i < 6; i++) tasks.add(pool.submit(() -> {
                if (redis.opsForValue().get(hot) != null) throw new IllegalStateException("Expected cold cache");
                barrier.await(5, TimeUnit.SECONDS);
                if (!protectedMode) { String value = readDatabase(db, false, reads); set(hot, value); return value; }
                String token = UUID.randomUUID().toString();
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
                while (System.nanoTime() < deadline) {
                    String cached = redis.opsForValue().get(hot);
                    if (cached != null) return cached;
                    if (Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(lock, token, Duration.ofSeconds(10)))) {
                        try {
                            cached = redis.opsForValue().get(hot);
                            if (cached == null) { cached = readDatabase(db, false, reads); set(hot, cached); }
                            return cached;
                        } finally { script(UNLOCK, lock, token); }
                    }
                    Thread.sleep(10);
                }
                throw new IllegalStateException("Cache rebuild timed out");
            }));
            for (var future : tasks) if (!future.get(8, TimeUnit.SECONDS).equals("1")) throw new IllegalStateException("Unexpected cached database value");
            return reads.get();
        } finally { ThreadPoolLearningDemo.stop(pool); }
    }
    private static void until(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(4);
        while (!condition.getAsBoolean()) { if (System.nanoTime() >= deadline) throw new IllegalStateException("Redis expiry observation timed out"); Thread.sleep(10); }
    }
    private void avalanche(Builder r) throws Exception {
        var same = new ArrayList<String>(); var staggered = new ArrayList<String>(); var ttls = new ArrayList<Long>();
        for (int i = 0; i < 8; i++) {
            String a = key("same-" + i), b = key("staggered-" + i); same.add(a); staggered.add(b);
            redis.opsForValue().set(a, "v", Duration.ofMillis(250));
            redis.opsForValue().set(b, "v", Duration.ofSeconds(10 + i));
            ttls.add(redis.getExpire(b, TimeUnit.MILLISECONDS));
        }
        until(() -> same.stream().noneMatch(k -> Boolean.TRUE.equals(redis.hasKey(k))));
        long remaining = staggered.stream().filter(k -> Boolean.TRUE.equals(redis.hasKey(k))).count();
        r.check("staggeredTtlsAvoidFirstExpiryWave", remaining == 8 && new HashSet<>(ttls).size() == 8);
        r.step("雪崩与错峰TTL", "8个250ms键集中到期；对照10～17秒错峰TTL在首波仍存活。只验证过期分布，不把小样本当吞吐压测。", Map.of("simultaneouslyExpired", 8, "staggeredRemaining", remaining, "staggeredTtlMs", ttls));
    }
    private void consistency(MysqlAdvancedLearningDemo.Database db, Builder r) throws Exception {
        String cache = key("consistent"), lock = key("consistent-lock"); var reads = new AtomicInteger();
        String stale = readDatabase(db, false, reads); // reader paused before filling cache
        try (var c = db.source.getConnection()) { MysqlAdvancedLearningDemo.execute(c, "UPDATE " + db.table + " SET score=2 WHERE id=10"); }
        redis.delete(cache); set(cache, stale); // reader resumes after writer invalidates
        r.check("deleteAfterWriteCanStillRaceWithStaleFill", "1".equals(redis.opsForValue().get(cache)) && "2".equals(readDatabase(db, false, reads)));
        redis.delete(cache);
        redis.opsForValue().setIfAbsent(lock, "reader", Duration.ofSeconds(10));
        String old = readDatabase(db, false, reads);
        r.check("writerCannotPassActiveReaderLock", !Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(lock, "writer", Duration.ofSeconds(10))));
        set(cache, old); script(UNLOCK, lock, "reader");
        r.check("writerAcquiresAfterReaderFinishes", Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(lock, "writer", Duration.ofSeconds(10))));
        try {
            try (var c = db.source.getConnection()) { MysqlAdvancedLearningDemo.execute(c, "UPDATE " + db.table + " SET score=3 WHERE id=10"); }
            redis.delete(cache);
        } finally { script(UNLOCK, lock, "writer"); }
        set(cache, readDatabase(db, false, reads));
        r.check("coordinatedRebuildMatchesDatabase", "3".equals(redis.opsForValue().get(cache)));
        r.step("数据库与缓存一致性", "用真实MySQL和Redis按固定交错复现旧值回填，再展示读回填与写入共用锁。锁过期/进程崩溃仍需版本或持久化失效事件等设计，本实验不声称跨存储原子性。", Map.of("staleFill", stale, "finalDatabaseAndCache", "3"));
    }
    private void locks(Builder r) throws Exception {
        String lock = key("lease");
        r.check("firstOwnerAcquires", Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(lock, "A", Duration.ofMillis(150))));
        until(() -> !Boolean.TRUE.equals(redis.hasKey(lock)));
        r.check("newOwnerAfterExpiry", Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(lock, "B", Duration.ofSeconds(5))));
        redis.delete(lock); // deliberately wrong unlock, isolated key only
        r.check("unconditionalDeleteRemovesNewOwner", !Boolean.TRUE.equals(redis.hasKey(lock)));
        redis.opsForValue().setIfAbsent(lock, "B", Duration.ofSeconds(5));
        r.check("tokenUnlockRejectsStaleOwner", script(UNLOCK, lock, "A") == 0 && "B".equals(redis.opsForValue().get(lock)));
        r.check("nonOwnerCannotRenew", script(RENEW, lock, "A", "10000") == 0);
        r.check("ownerCanRenew", script(RENEW, lock, "B", "10000") == 1 && redis.getExpire(lock, TimeUnit.MILLISECONDS) > 5000);
        r.check("ownerCanUnlock", script(UNLOCK, lock, "B") == 1);
        r.step("租约过期、误释放与续期", "真实等待A租约到期；演示错误DEL删除B，再用token校验Lua原子解锁/续期。手动续期不等于自动watchdog，也不能阻止已过期持有者继续写业务。", Map.of("expiredOwner", "A", "newOwner", "B", "renewedLeaseMs", 10000));
    }
    private void lua(Builder r) throws Exception {
        String stock = key("stock"); set(stock, "5");
        String decrement = "local n=tonumber(redis.call('get',KEYS[1]) or '0'); if n>0 then redis.call('decr',KEYS[1]); return 1 else return 0 end";
        var pool = Executors.newFixedThreadPool(4);
        try {
            var tasks = new ArrayList<Future<Long>>();
            for (int i = 0; i < 12; i++) tasks.add(pool.submit(() -> script(decrement, stock)));
            long successes = 0;
            for (var task : tasks) successes += task.get(5, TimeUnit.SECONDS);
            r.check("atomicStockNeverOversells", successes == 5 && "0".equals(redis.opsForValue().get(stock)));
            r.step("Lua原子扣减", "12个请求竞争5份库存，检查和扣减在同一段Lua执行；仅演示单Redis状态原子性。", Map.of("requests", 12, "successes", successes, "remaining", 0));
        } finally { ThreadPoolLearningDemo.stop(pool); }
    }
    private void fencing(MysqlAdvancedLearningDemo.Database db, Builder r) throws Exception {
        String lease = key("fencing-lease"), sequence = key("fencing-sequence");
        set(sequence, "0");
        // Issue and install a token atomically; all keys share this run's Redis hash slot.
        String acquire = "if redis.call('exists',KEYS[1])==0 then local n=redis.call('incr',KEYS[2]); redis.call('psetex',KEYS[1],ARGV[1],tostring(n)); return n else return 0 end";
        var acquireScript = new DefaultRedisScript<>(acquire, Long.class);
        Long tokenA = redis.execute(acquireScript, List.of(lease, sequence), "150");
        r.check("firstFencingLeaseIssued", tokenA != null && tokenA == 1);
        until(() -> !Boolean.TRUE.equals(redis.hasKey(lease)));
        Long tokenB = redis.execute(acquireScript, List.of(lease, sequence), "10000");
        r.check("laterOwnerReceivesGreaterToken", tokenB != null && tokenB > tokenA);
        try (var c = db.source.getConnection()) {
            MysqlAdvancedLearningDemo.execute(c, "UPDATE " + db.table + " SET payload='owner-B' WHERE id=10");
            MysqlAdvancedLearningDemo.execute(c, "UPDATE " + db.table + " SET payload='stale-owner-A' WHERE id=10");
            r.check("leaseExpiryAloneCannotPreventStaleWrite", "stale-owner-A".equals(MysqlAdvancedLearningDemo.query(c, "SELECT payload FROM " + db.table + " WHERE id=10").getFirst().get("payload")));
            MysqlAdvancedLearningDemo.execute(c, "ALTER TABLE " + db.table + " ADD COLUMN fence BIGINT NOT NULL DEFAULT 0");
            int accepted = MysqlAdvancedLearningDemo.update(c, "UPDATE " + db.table + " SET payload='owner-B',fence=" + tokenB + " WHERE id=10 AND fence<" + tokenB);
            int stale = MysqlAdvancedLearningDemo.update(c, "UPDATE " + db.table + " SET payload='stale-owner-A',fence=" + tokenA + " WHERE id=10 AND fence<" + tokenA);
            int duplicate = MysqlAdvancedLearningDemo.update(c, "UPDATE " + db.table + " SET payload='duplicate-B' WHERE id=10 AND fence<" + tokenB);
            r.check("downstreamRejectsOlderFencingToken", accepted == 1 && stale == 0);
            r.check("sameTokenReplayRejectedForSingleWriteProtocol", duplicate == 0);
            r.check("newOwnerValuePreserved", "owner-B".equals(MysqlAdvancedLearningDemo.query(c, "SELECT payload FROM " + db.table + " WHERE id=10").getFirst().get("payload")));
            r.step("租约失效与下游写入屏障", "A过期、B持锁时，A仍可向数据库写入；把单调token交给MySQL条件UPDATE后，数据库拒绝A的陈旧写。本例每token只允许一次写；token顺序仅在本次Redis计数器生命周期内成立，故障切换/计数器回退需由持久化序列或共识系统保证，不能仅凭Redis锁宣称一致性。", Map.of("oldToken", tokenA, "newToken", tokenB, "acceptedRows", accepted, "staleRows", stale, "duplicateRows", duplicate));
        } finally { script(UNLOCK, lease, String.valueOf(tokenB)); }
    }
}
