package com.kuma.cloud.lab.observability;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import tools.jackson.databind.json.JsonMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/** Real JDBC work confined to one fixed schema/database and unique rows per run. */
public final class DatabaseObservabilityLearningDemo {
    public static final String NAMESPACE = "kuma_observability_lab";
    private DatabaseObservabilityLearningDemo() { }

    @FunctionalInterface
    public interface Connections { Connection open() throws SQLException; }
    public record SqlEvidence(String operation, String sql, Integer value, String sqlState, double durationMs) { }
    public record Report(String database, String namespace, String runId, String scenario, String transaction,
                         String traceId, String nativeTraceId, int stock, int payments,
                         List<SqlEvidence> sql, List<OtelLearningDemo.SpanEvidence> spans,
                         Map<String, Boolean> checks, String backendStatus) { }

    public static Connections fromEnvironment() {
        return fromSettings(System.getenv("KUMA_LAB_OBSERVABILITY_JDBC_URL"),
                requiredEnv("KUMA_LAB_OBSERVABILITY_JDBC_USER"), requiredEnv("KUMA_LAB_OBSERVABILITY_JDBC_PASSWORD"));
    }

    static Connections fromSettings(String url, String user, String password) {
        if (url == null || !(url.startsWith("jdbc:postgresql:") || url.startsWith("jdbc:mysql:"))) {
            throw new IllegalArgumentException("Set KUMA_LAB_OBSERVABILITY_JDBC_URL to a PostgreSQL or MySQL JDBC URL");
        }
        Properties properties = new Properties();
        if (user == null || user.isBlank() || password == null) throw new IllegalArgumentException("Configure the database user/password");
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        boolean postgres = url.startsWith("jdbc:postgresql:");
        properties.setProperty("connectTimeout", postgres ? "5" : "5000");
        properties.setProperty("socketTimeout", postgres ? "10" : "10000");
        return () -> DriverManager.getConnection(url, properties);
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Set " + name);
        return value;
    }

    public static void main(String[] args) throws Exception {
        boolean nativeMode = args.length == 1 && args[0].equals("--native");
        boolean online = args.length == 3 && args[0].equals("--export");
        if (args.length != 0 && !nativeMode && !online) {
            throw new IllegalArgumentException("Usage: DatabaseObservabilityLearningDemo [--native | --export <OTLP URL> <query URL>]");
        }
        Connections connections = fromEnvironment();
        if (nativeMode) Thread.sleep(3000);
        List<Report> reports = new ArrayList<>();
        for (String scenario : List.of("normal", "slow", "error")) {
            Report report = run(connections, scenario, nativeMode, online ? args[1] : null);
            if (online) OtelLearningDemo.verifyTrace(report.traceId(), report.spans().stream()
                    .map(OtelLearningDemo.SpanEvidence::spanId).toList(), args[2], Duration.ofSeconds(90));
            if (nativeMode && !validNativeId(report.nativeTraceId())) {
                throw new IllegalStateException("No active native trace; check the Agent/toolkit configuration");
            }
            reports.add(online ? new Report(report.database(), report.namespace(), report.runId(), report.scenario(),
                    report.transaction(), report.traceId(), report.nativeTraceId(), report.stock(), report.payments(),
                    report.sql(), report.spans(), report.checks(), "VERIFIED_IN_SKYWALKING") : report);
        }
        System.out.println(JsonMapper.builder().build().writerWithDefaultPrettyPrinter().writeValueAsString(reports));
        if (nativeMode) Thread.sleep(5000);
    }

    @Trace(operationName = "lab.database.checkout")
    public static Report run(Connections connections, String scenario, boolean nativeMode, String exportEndpoint) throws SQLException {
        if (!List.of("normal", "slow", "error").contains(scenario)) throw new IllegalArgumentException("Unknown scenario");
        List<SpanData> captured = new CopyOnWriteArrayList<>();
        var builder = SdkTracerProvider.builder().setResource(Resource.create(Attributes.builder()
                .put("service.name", "kuma-lab-database-otel").build()));
        if (!nativeMode) {
            builder.addSpanProcessor(SimpleSpanProcessor.create(new SpanExporter() {
                public CompletableResultCode export(Collection<SpanData> spans) { captured.addAll(spans); return flush(); }
                public CompletableResultCode flush() { return CompletableResultCode.ofSuccess(); }
                public CompletableResultCode shutdown() { return flush(); }
            }));
            if (exportEndpoint != null) builder.addSpanProcessor(SimpleSpanProcessor.create(OtlpHttpSpanExporter.builder()
                    .setEndpoint(exportEndpoint).setTimeout(Duration.ofSeconds(5)).build()));
        }
        try (var provider = builder.build(); Connection connection = connections.open()) {
            String product = connection.getMetaData().getDatabaseProductName();
            boolean postgres = product.equals("PostgreSQL");
            if (!postgres && !product.equals("MySQL")) throw new IllegalArgumentException("Only PostgreSQL and MySQL are supported");
            String namespace = postgres ? "\"" + NAMESPACE + "\"" : "`" + NAMESPACE + "`";
            String inventory = namespace + ".inventory";
            String payment = namespace + ".payments";
            String runId = UUID.randomUUID().toString();
            // DDL outside the transaction: MySQL CREATE DATABASE/TABLE may implicitly commit.
            connection.setAutoCommit(true);
            try (var statement = connection.createStatement()) {
                statement.setQueryTimeout(5);
                statement.execute(postgres ? "CREATE SCHEMA IF NOT EXISTS " + namespace
                        : "CREATE DATABASE IF NOT EXISTS " + namespace + " CHARACTER SET utf8mb4");
                statement.execute("CREATE TABLE IF NOT EXISTS " + inventory
                        + " (run_id VARCHAR(36) PRIMARY KEY, stock INTEGER NOT NULL)" + (postgres ? "" : " ENGINE=InnoDB"));
                statement.execute("CREATE TABLE IF NOT EXISTS " + payment
                        + " (run_id VARCHAR(36) NOT NULL, receipt_id INTEGER NOT NULL, amount INTEGER NOT NULL,"
                        + " PRIMARY KEY (run_id, receipt_id))" + (postgres ? "" : " ENGINE=InnoDB"));
            }
            try (var seed = connection.prepareStatement("INSERT INTO " + inventory + " (run_id, stock) VALUES (?, 10)")) {
                seed.setQueryTimeout(5);
                seed.setString(1, runId);
                seed.executeUpdate();
            }
            Tracer tracer = nativeMode ? null : provider.get("kuma-database-lesson");
            Span root = tracer == null ? Span.getInvalid() : tracer.spanBuilder("database.checkout")
                    .setParent(Context.root()).setAttribute("lab.scenario", scenario).startSpan();
            List<SqlEvidence> sql = new ArrayList<>();
            String transaction = "COMMIT";
            int stock;
            int payments;
            connection.setAutoCommit(false);
            try (Scope scope = root.makeCurrent()) {
                ActiveSpan.tag("lab.scenario", scenario);
                ActiveSpan.tag("lab.namespace", NAMESPACE);
                try {
                    execute(connection, tracer, postgres, "inventory.select", "SELECT stock FROM " + inventory + " WHERE run_id = ?", true, sql, runId);
                    if (scenario.equals("slow")) execute(connection, tracer, postgres, "database.slow-query",
                            postgres ? "SELECT pg_sleep(0.2)" : "SELECT SLEEP(0.2)", true, sql);
                    int affected = execute(connection, tracer, postgres, "inventory.decrement",
                            "UPDATE " + inventory + " SET stock = stock - 1 WHERE run_id = ? AND stock > 0", false, sql, runId);
                    if (affected != 1) throw new SQLException("Expected one isolated inventory row", "LAB01");
                    execute(connection, tracer, postgres, "payment.insert", "INSERT INTO " + payment
                            + " (run_id, receipt_id, amount) VALUES (?, 1, 100)", false, sql, runId);
                    if (scenario.equals("error")) execute(connection, tracer, postgres, "payment.duplicate-key", "INSERT INTO " + payment
                            + " (run_id, receipt_id, amount) VALUES (?, 1, 100)", false, sql, runId);
                    connection.commit();
                    root.addEvent("transaction.commit");
                } catch (SQLException failure) {
                    connection.rollback();
                    transaction = "ROLLBACK";
                    root.recordException(failure);
                    root.setStatus(StatusCode.ERROR, "SQLSTATE=" + failure.getSQLState());
                    root.addEvent("transaction.rollback");
                    ActiveSpan.error(failure);
                    if (!scenario.equals("error") || failure.getSQLState() == null || !failure.getSQLState().startsWith("23")) throw failure;
                }
                // Fresh transaction boundary verifies persisted state, not an in-transaction snapshot.
                connection.setAutoCommit(true);
                stock = execute(connection, tracer, postgres, "verify.stock", "SELECT stock FROM " + inventory + " WHERE run_id = ?", true, sql, runId);
                payments = execute(connection, tracer, postgres, "verify.payments", "SELECT COUNT(*) FROM " + payment + " WHERE run_id = ?", true, sql, runId);
            } finally { root.end(); }
            var flush = provider.forceFlush().join(10, TimeUnit.SECONDS);
            if (!flush.isSuccess()) throw new IllegalStateException("Trace flush failed");
            boolean error = scenario.equals("error");
            Map<String, Boolean> checks = Map.of(
                    "isolatedNamespace", sql.stream().allMatch(s -> s.sql().contains(namespace)
                            || s.operation().equals("database.slow-query")),
                    "stockPersistedAsExpected", stock == (error ? 10 : 9),
                    "paymentsPersistedAsExpected", payments == (error ? 0 : 1),
                    "transactionAsExpected", transaction.equals(error ? "ROLLBACK" : "COMMIT"),
                    "databaseConstraintObserved", !error || sql.stream().anyMatch(s -> s.sqlState() != null && s.sqlState().startsWith("23")));
            if (checks.containsValue(false)) throw new IllegalStateException("Database checks failed: " + checks);
            return new Report(product, NAMESPACE, runId, scenario, transaction,
                    nativeMode ? "" : root.getSpanContext().getTraceId(), TraceContext.traceId(), stock, payments,
                    List.copyOf(sql), captured.stream().map(OtelLearningDemo::evidence).toList(), checks,
                    nativeMode ? "NATIVE_REQUIRES_QUERY_VERIFICATION" : exportEndpoint == null ? "LOCAL_ONLY" : "EXPORT_ATTEMPTED_NOT_YET_QUERIED");
        }
    }

    @Trace(operationName = "lab.database.sql")
    private static int execute(Connection connection, Tracer tracer, boolean postgres, String operation,
                               String sql, boolean query, List<SqlEvidence> evidence, Object... parameters) throws SQLException {
        Span span = tracer == null ? Span.getInvalid() : tracer.spanBuilder(operation).setSpanKind(SpanKind.CLIENT)
                .setAttribute("db.system.name", postgres ? "postgresql" : "mysql")
                .setAttribute("db.namespace", postgres ? connection.getCatalog() : NAMESPACE)
                .setAttribute("lab.db.schema", NAMESPACE).setAttribute("db.query.text", sql).startSpan();
        long start = System.nanoTime();
        ActiveSpan.tag("lab.sql.operation", operation);
        try (Scope scope = span.makeCurrent(); var statement = connection.prepareStatement(sql)) {
            statement.setQueryTimeout(5);
            for (int i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);
            int value;
            if (query) {
                try (var rows = statement.executeQuery()) {
                    if (!rows.next()) throw new SQLException("Missing isolated experiment row", "LAB02");
                    value = operation.equals("database.slow-query") ? 0 : rows.getInt(1);
                }
            } else value = statement.executeUpdate();
            evidence.add(new SqlEvidence(operation, sql, value, null, (System.nanoTime() - start) / 1_000_000.0));
            return value;
        } catch (SQLException failure) {
            span.recordException(failure);
            span.setStatus(StatusCode.ERROR, "SQLSTATE=" + failure.getSQLState());
            ActiveSpan.error(failure);
            evidence.add(new SqlEvidence(operation, sql, null, failure.getSQLState(), (System.nanoTime() - start) / 1_000_000.0));
            throw failure;
        } finally { span.end(); }
    }

    static boolean validNativeId(String id) {
        return id != null && !id.isBlank() && !id.equals("Ignored_Trace") && !id.equals("N/A");
    }
}
