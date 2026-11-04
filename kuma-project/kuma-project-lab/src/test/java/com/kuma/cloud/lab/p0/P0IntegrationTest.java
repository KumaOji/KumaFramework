package com.kuma.cloud.lab.p0;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.redis.core.StringRedisTemplate;
import static org.junit.jupiter.api.Assertions.*;

class P0IntegrationTest {
    @Test @Timeout(60)
    @EnabledIfEnvironmentVariable(named = "KUMA_LAB_P0_JDBC_URL", matches = "jdbc:mysql:.*")
    void mysqlRealPlansMvccLocksDeadlockAndSpringTransactions() throws Exception {
        var source = MysqlAdvancedLearningDemo.fromEnvironment();
        var report = MysqlAdvancedLearningDemo.run(source, "all");
        assertTrue(report.checks().size() >= 17);
        assertTrue(report.checks().values().stream().allMatch(Boolean::booleanValue));
        assertTrue(report.checks().get("readModifyWriteLosesFirstChange"));
        assertTrue(report.checks().get("boundedRetryPreservesBothChanges"));
        assertTrue(report.checks().get("caughtRequiredFailureStillMarksWholeTransactionRollbackOnly"));
        String table = (String) report.steps().getFirst().evidence();
        try (var c = source.getConnection()) {
            assertEquals(0, MysqlAdvancedLearningDemo.number(c, "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='kuma_p0_lab' AND table_name='" + table.substring(table.indexOf('.') + 1) + "'"));
        }
    }
    @Test @Timeout(60)
    @EnabledIfEnvironmentVariable(named = "KUMA_LAB_P0_JDBC_URL", matches = "jdbc:mysql:.*")
    @EnabledIfEnvironmentVariable(named = "KUMA_LAB_P0_REDIS_HOST", matches = ".+")
    void redisRealCacheRebuildConsistencyLeasesAndLua() throws Exception {
        var factory = RedisAdvancedLearningDemo.fromEnvironment();
        try {
            var redis = new StringRedisTemplate(factory);
            var report = new RedisAdvancedLearningDemo(redis).run(MysqlAdvancedLearningDemo.fromEnvironment(), "all");
            assertTrue(report.checks().size() >= 15);
            assertTrue(report.checks().values().stream().allMatch(Boolean::booleanValue));
            assertTrue(report.checks().get("leaseExpiryAloneCannotPreventStaleWrite"));
            assertTrue(report.checks().get("downstreamRejectsOlderFencingToken"));
            String prefix = (String) report.steps().getFirst().evidence();
            assertFalse(redis.hasKey(prefix + "stock"));
            assertFalse(redis.hasKey(prefix + "consistent"));
            assertFalse(redis.hasKey(prefix + "lease"));
        } finally { factory.destroy(); }
    }
}
