package com.kuma.cloud.lab.observability;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "KUMA_LAB_OBSERVABILITY_JDBC_URL", matches = "jdbc:(postgresql|mysql):.*")
class DatabaseObservabilityIntegrationTest {
    @Test
    void realTransactionsCommitDelayAndRollbackInTheirOwnNamespace() throws Exception {
        var connections = DatabaseObservabilityLearningDemo.fromEnvironment();
        for (String scenario : List.of("normal", "slow", "error")) {
            var report = DatabaseObservabilityLearningDemo.run(connections, scenario, false, null);
            assertEquals("kuma_observability_lab", report.namespace());
            assertTrue(report.checks().values().stream().allMatch(Boolean::booleanValue));
            assertTrue(report.sql().stream().allMatch(sql -> sql.sql().contains(report.namespace())
                    || sql.operation().equals("database.slow-query")));
            assertTrue(report.spans().stream().allMatch(span -> span.traceId().equals(report.traceId())));
            assertTrue(report.spans().stream().flatMap(span -> span.attributes().values().stream())
                    .noneMatch(value -> value.contains(report.runId())), "Bound values must not be recorded in SQL span attributes");
            if (scenario.equals("error")) {
                assertEquals("ROLLBACK", report.transaction());
                assertEquals(10, report.stock());
                assertEquals(0, report.payments());
                assertTrue(report.spans().stream().anyMatch(span -> span.name().equals("payment.duplicate-key")
                        && span.status().equals("ERROR") && span.events().contains("exception")));
            } else {
                assertEquals("COMMIT", report.transaction());
                assertEquals(9, report.stock());
                assertEquals(1, report.payments());
            }
            if (scenario.equals("slow")) assertTrue(report.sql().stream()
                    .filter(sql -> sql.operation().equals("database.slow-query")).anyMatch(sql -> sql.durationMs() >= 190));
        }
    }

    @Test
    void repeatedRunsPreservePreviousRows() throws Exception {
        var connections = DatabaseObservabilityLearningDemo.fromEnvironment();
        var first = DatabaseObservabilityLearningDemo.run(connections, "normal", false, null);
        var second = DatabaseObservabilityLearningDemo.run(connections, "normal", false, null);
        assertNotEquals(first.runId(), second.runId());
        try (var connection = connections.open(); var statement = connection.prepareStatement(
                "SELECT stock FROM kuma_observability_lab.inventory WHERE run_id = ?")) {
            for (String id : List.of(first.runId(), second.runId())) {
                statement.setString(1, id);
                try (var rows = statement.executeQuery()) {
                    assertTrue(rows.next());
                    assertEquals(9, rows.getInt(1));
                }
            }
        }
    }
}
