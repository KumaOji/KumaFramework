package com.kuma.cloud.reference;

import static org.junit.jupiter.api.Assertions.*;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.cloud.reference.mapper.OrderMapper;
import com.kuma.cloud.reference.domain.entity.OrderDO;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceExecutor;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceInvoker;
import com.kuma.cloud.reference.mapper.UnsafeOrderMapper;
import com.kuma.boot.data.mybatis.utils.MybatisUtil;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.apache.ibatis.session.SqlSessionFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles("local")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.config.name=application",
        "kuma.boot.core.async.core-pool-size=1", "kuma.boot.core.async.max-pool-siz=1", "kuma.boot.core.async.queue-capacity=1"
})
class ReferenceApplicationTest {
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @LocalServerPort int port;
    @Autowired DynamicRoutingDataSource dataSource;
    @Autowired DataSourceExecutor schemas;
    @Autowired DataSourceInvoker invoker;
    @Autowired OrderMapper orders;
    @Autowired TransactionTemplate transactions;
    @Autowired MybatisUtil kumaMybatisUtil;
    @Autowired SqlSessionFactory sqlSessionFactory;
    @Autowired org.springframework.context.ApplicationContext context;
    @Autowired UnsafeOrderMapper unsafeOrderMapper;
    @Autowired @Qualifier("asyncThreadPoolTaskExecutor") ThreadPoolTaskExecutor executor;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

    @BeforeEach
    void prepareIsolatedTestTables() throws Exception {
        for (var scope : DataScope.values()) {
            try (var connection = dataSource.getDataSource(scope.dataSource()).getConnection();
                    var statement = connection.createStatement()) {
                String dialect = connection.getMetaData().getDatabaseProductName().equals("MySQL") ? "mysql" : "postgresql";
                String sql = java.nio.file.Files.readString(java.nio.file.Path.of("sql", dialect, "schema.sql"))
                        .replace("kuma_reference_main", scope.schema());
                org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript(connection,
                        new org.springframework.core.io.ByteArrayResource(sql.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
                statement.executeUpdate("DELETE FROM purchase_orders");
            }
        }
    }

    @Test
    void starterRestoresTenantAndDataSourceAfterNestedFailures() {
        assertEquals(1, context.getBeansOfType(
                com.kuma.boot.web.autoconfigure.WebMvcAutoConfiguration.class).size());
        assertInstanceOf(com.kuma.boot.data.datasource.dynamic.mybatisplus.config.DsLastParamProcessor.class,
                context.getBean(com.baomidou.dynamic.datasource.processor.DsProcessor.class));
        assertTrue(sqlSessionFactory.getConfiguration().getInterceptors().stream()
                .filter(MybatisPlusInterceptor.class::isInstance).map(MybatisPlusInterceptor.class::cast)
                .flatMap(interceptor -> interceptor.getInterceptors().stream())
                .anyMatch(com.kuma.boot.data.mybatis.mybatisplus.interceptor.tenant.SchemaInterceptor.class::isInstance));
        var previous = com.kuma.boot.common.holder.TenantContextHolder.getTenant();
        schemas.execute(DataScope.MAIN.dataSource(), "main", () -> {
            assertThrows(IllegalStateException.class, () ->
                    schemas.execute(DataScope.MYSQL.dataSource(), "mysql", () -> {
                        assertEquals("mysql", com.kuma.boot.common.holder.TenantContextHolder.getTenant());
                        throw new IllegalStateException("nested failure");
                    }));
            assertEquals("main", com.kuma.boot.common.holder.TenantContextHolder.getTenant());
            assertEquals(DataScope.MAIN.dataSource(), DynamicDataSourceContextHolder.peek());
            return null;
        });
        assertEquals(previous, com.kuma.boot.common.holder.TenantContextHolder.getTenant());
        assertNull(DynamicDataSourceContextHolder.peek());
    }

    @Test
    void kumaActuatorContributesHealthWithoutExposingExtraEndpoints() throws Exception {
        assertEquals(1, context.getBeansOfType(
                com.kuma.boot.actuator.health.KmcHealthIndicator.class).size());
        var health = request("GET", "/actuator/health", null);
        assertEquals(200, health.statusCode(), health.body());
        assertEquals("UP", JSON.readTree(health.body()).path("status").asString());
        assertEquals(404, request("GET", "/actuator/env", null).statusCode());
    }

    @Test
    void fullHttpFlowPersistsPriceSnapshotAndProtectsStateTransitions() throws Exception {
        var created = request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":2}");
        assertEquals(201, created.statusCode(), created.body());
        var body = successData(created);
        var location = created.headers().firstValue("Location").orElseThrow();
        assertEquals("/api/orders/" + body.path("id").asString() + "?scope=MAIN", location);
        assertEquals(198, body.path("totalAmount").asInt());
        assertEquals("CREATED", body.path("status").asString());

        var read = request("GET", location, null);
        assertEquals(200, read.statusCode(), read.body());
        assertEquals("BOOK-001", successData(read).path("sku").asString());
        String confirmPath = "/api/orders/" + body.path("id").asString() + "/confirm?scope=MAIN";
        var confirmed = request("POST", confirmPath, null);
        assertEquals(200, confirmed.statusCode(), confirmed.body());
        assertEquals("CONFIRMED", successData(confirmed).path("status").asString());
        assertEquals(1, successData(confirmed).path("version").asInt());
        var conflict = request("POST", confirmPath, null);
        assertProblem(conflict, 409);
        assertEquals(ResultEnum.REQUEST_OTHER_OPERATION.code().getCode(),
                JSON.readTree(conflict.body()).path("code").asString());
        assertEquals("只有待确认订单可以确认", JSON.readTree(conflict.body()).path("message").asString());
    }

    @Test
    void validatesInputAndReturns404ForMissingOrders() throws Exception {
        long before = countOrders();
        var invalid = request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"quantity\":1}");
        assertProblem(invalid, 400);
        var fieldErrors = JSON.readTree(invalid.body()).path("data").path("fieldErrors");
        assertTrue(fieldErrors.isArray(), invalid.body());
        assertTrue(fieldErrors.size() > 0, invalid.body());
        assertEquals(ResultEnum.BAD_REQUEST.codeDesc(), fieldErrors.get(0).path("code").asString());
        assertFalse(fieldErrors.get(0).path("property").asString().isBlank());
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"\",\"unitPrice\":99,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":-1,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":1.001,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":92233720368547758.08,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":92233720368547758.07,\"quantity\":2}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":0}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"../unsafe\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "not-json"), 400);
        var missingId = Long.MAX_VALUE;
        var missing = request("GET", "/api/orders/" + missingId, null);
        assertProblem(missing, 404);
        assertEquals(ResultEnum.DATA_NOT_EXIST_ERROR.code().getCode(),
                JSON.readTree(missing.body()).path("code").asString());
        assertEquals("订单不存在：" + missingId, JSON.readTree(missing.body()).path("message").asString());
        assertProblem(request("GET", "/api/orders/not-a-number", null), 400);
        assertEquals(before, countOrders());
    }

    @Test
    void rejectsStaleWritesAndRollsBackFailedTransactions() throws Exception {
        var created = successData(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":1}"));
        var id = created.path("id").asLong();
        var snapshot = orders.selectById(id).toDomain();
        var first = OrderDO.from(snapshot);
        var stale = OrderDO.from(snapshot);
        first.setOrderStatus("CONFIRMED");
        stale.setOrderStatus("CONFIRMED");
        transactions.executeWithoutResult(status -> assertEquals(1, orders.updateById(first)));
        transactions.executeWithoutResult(status -> assertEquals(0, orders.updateById(stale)));
        long before = countOrders();
        assertThrows(IllegalStateException.class, () -> transactions.executeWithoutResult(status -> {
            orders.insert(OrderDO.from(com.kuma.cloud.reference.domain.model.Order.create(snapshot.product(), 1, java.time.Instant.now())));
            throw new IllegalStateException("rollback");
        }));
        assertEquals(before, countOrders());
    }

    @Test
    void boundsTheQueueAndReturns503InsteadOfSilentlyDroppingWork() throws Exception {
        var running = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        try {
            executor.execute(() -> {
                running.countDown();
                try { release.await(5, TimeUnit.SECONDS); }
                catch (InterruptedException error) { Thread.currentThread().interrupt(); }
            });
            assertTrue(running.await(2, TimeUnit.SECONDS));
            var queued = executor.submitCompletable(() -> "queued");
            assertProblem(request("GET", "/api/orders/" + Long.MAX_VALUE, null), 503);
            release.countDown();
            assertEquals("queued", queued.get(2, TimeUnit.SECONDS));
        } finally { release.countDown(); }
    }

    @Test
    void propagatesMdcWithoutLeakingItIntoTheNextTask() throws Exception {
        assertInstanceOf(com.kuma.boot.core.support.thread.MDCThreadPoolTaskExecutor.class, executor);
        assertEquals(1, context.getBeansOfType(ThreadPoolTaskExecutor.class).size(),
                context.getBeansOfType(ThreadPoolTaskExecutor.class).keySet().toString());
        assertEquals(1, executor.getCorePoolSize());
        assertEquals(1, executor.getMaxPoolSize());
        assertEquals(1, executor.getQueueCapacity());
        try {
            MDC.put("requestId", "reference-test");
            assertEquals("reference-test", executor.submitCompletable(() -> MDC.get("requestId")).get(2, TimeUnit.SECONDS));
        } finally { MDC.clear(); }
        assertNull(executor.submitCompletable(() -> MDC.get("requestId")).get(2, TimeUnit.SECONDS));
    }

    @Test
    void asyncRequestsPreserveIncomingTraceIdAndGenerateOneForTheNextRequest() throws Exception {
        var incoming = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/orders"))
                .header("kmc-trace-id", "reference-correlation-test")
                .timeout(Duration.ofSeconds(5)).GET().build();
        var response = http.send(incoming, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), response.body());
        assertEquals("reference-correlation-test", response.headers().firstValue("kmc-trace-id").orElseThrow());
        successData(response);
        var next = request("GET", "/api/orders", null);
        assertEquals(200, next.statusCode(), next.body());
        var generated = next.headers().firstValue("kmc-trace-id").orElseThrow();
        assertFalse(generated.isBlank());
        assertNotEquals("reference-correlation-test", generated);
        successData(next);
    }

    @Test
    void asyncBusinessLogsCarryTenantAndVersionWithoutLeakingToTheNextRequest() throws Exception {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(
                com.kuma.cloud.reference.service.impl.OrderServiceImpl.class);
        var previousLevel = logger.getLevel();
        var events = new java.util.concurrent.CopyOnWriteArrayList<ch.qos.logback.classic.spi.ILoggingEvent>();
        var appender = new ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent>() {
            @Override
            protected void append(ch.qos.logback.classic.spi.ILoggingEvent event) {
                event.prepareForDeferredProcessing();
                events.add(event);
            }
        };
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
        try {
            var incoming = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/orders?scope=MAIN"))
                    .header("kmc-trace-id", "reference-tenant-version-test")
                    .header("kmc-tenant-id", "demo-tenant")
                    .header("kmc-request-version", "v2")
                    .timeout(Duration.ofSeconds(5)).GET().build();
            var response = http.send(incoming, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), response.body());
            assertEquals("demo-tenant", response.headers().firstValue("kmc-tenant-id").orElseThrow());
            assertEquals("v2", response.headers().firstValue("kmc-request-version").orElseThrow());
            var first = events.getFirst().getMDCPropertyMap();
            assertEquals("reference-tenant-version-test", first.get("kmc-trace-id"));
            assertEquals("demo-tenant", first.get("kmc-tenant-id"));
            assertEquals("v2", first.get("kmc-request-version"));

            assertEquals(200, request("GET", "/api/orders?scope=MAIN", null).statusCode());
            var next = events.getLast().getMDCPropertyMap();
            assertEquals("1", next.get("kmc-tenant-id"));
            assertNull(next.get("kmc-request-version"));
            assertNotEquals(first.get("kmc-trace-id"), next.get("kmc-trace-id"));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            logger.setLevel(previousLevel);
        }
    }

    @Test
    void asyncExceptionLogsKeepRequestMdcAndGeneratedTraceIds() throws Exception {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(
                com.kuma.boot.web.error.ErrorHandlingControllerAdvice.class);
        var events = new java.util.concurrent.CopyOnWriteArrayList<ch.qos.logback.classic.spi.ILoggingEvent>();
        var appender = new ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent>() {
            @Override
            protected void append(ch.qos.logback.classic.spi.ILoggingEvent event) {
                if (event.getLevel() == ch.qos.logback.classic.Level.ERROR) {
                    event.prepareForDeferredProcessing();
                    events.add(event);
                }
            }
        };
        appender.start();
        logger.addAppender(appender);
        try {
            var incoming = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/orders/" + Long.MAX_VALUE))
                    .header("kmc-trace-id", "async-error-correlation")
                    .header("kmc-tenant-id", "error-tenant")
                    .header("kmc-request-version", "v2")
                    .timeout(Duration.ofSeconds(5)).GET().build();
            var response = http.send(incoming, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), response.body());
            assertProblem(response, 404);
            var first = events.getFirst().getMDCPropertyMap();
            assertEquals("async-error-correlation", first.get("kmc-trace-id"));
            assertEquals("error-tenant", first.get("kmc-tenant-id"));
            assertEquals("v2", first.get("kmc-request-version"));

            var next = request("GET", "/api/orders/" + Long.MAX_VALUE, null);
            assertEquals(404, next.statusCode(), next.body());
            assertEquals(2, events.size());
            var nextMdc = events.getLast().getMDCPropertyMap();
            var generated = next.headers().firstValue("kmc-trace-id").orElseThrow();
            assertFalse(generated.isBlank());
            assertEquals(generated, nextMdc.get("kmc-trace-id"));
            assertNotEquals("async-error-correlation", generated);
            assertEquals("1", nextMdc.get("kmc-tenant-id"));
            assertNull(nextMdc.get("kmc-request-version"));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void qoVoPaginationAndSchemaRoutingKeepOrdersIsolated() throws Exception {
        String sku = "ISOLATION-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var main = successData(request("POST", "/api/orders?scope=MAIN", "{\"sku\":\"" + sku + "\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":1}"));
        var mysql = successData(request("POST", "/api/orders?scope=MYSQL", "{\"sku\":\"" + sku + "\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":2}"));
        for (DataScope scope : new DataScope[] {DataScope.MAIN, DataScope.MYSQL}) {
            var page = request("GET", "/api/orders?scope=" + scope + "&sku=" + sku + "&status=CREATED&currentPage=1&pageSize=1", null);
            assertEquals(200, page.statusCode(), page.body());
            var result = successData(page);
            assertEquals(1, result.path("totalSize").asInt());
            assertEquals(1, result.path("data").size());
            assertEquals(scope == DataScope.MAIN ? 1 : 2, result.path("data").get(0).path("quantity").asInt());
        }
        assertEquals(200, request("GET", "/api/orders", null).statusCode());
        assertProblem(request("GET", "/api/orders?page=0", null), 400);
        assertProblem(request("GET", "/api/orders?size=101", null), 400);
        assertProblem(request("GET", "/api/orders?status=WRONG", null), 400);
        assertProblem(request("GET", "/api/orders?scope=public", null), 400);
    }

    @Test
    void frameworkPaginationKeepsCountsAcrossPagesAndWhitelistsSorts() throws Exception {
        var ids = new java.util.ArrayList<Long>();
        for (int i = 0; i < 3; i++) {
            var created = request("POST", "/api/orders", "{\"sku\":\"PAGE-TEST\",\"productName\":\"Page item\",\"unitPrice\":1.00,\"quantity\":1}");
            assertEquals(201, created.statusCode(), created.body());
            ids.add(successData(created).path("id").asLong());
        }
        assertTrue(ids.getFirst() > 0);
        var response = request("GET", "/api/orders?sku=PAGE-TEST&currentPage=2&pageSize=1&sort=id&order=desc", null);
        assertEquals(200, response.statusCode(), response.body());
        var page = successData(response);
        assertEquals(3, page.path("totalSize").asInt());
        assertEquals(3, page.path("totalPage").asInt());
        assertEquals(2, page.path("currentPage").asInt());
        assertEquals(1, page.path("pageSize").asInt());
        assertEquals(ids.get(1).longValue(), page.path("data").get(0).path("id").asLong());
        var empty = successData(request("GET", "/api/orders?sku=PAGE-TEST&currentPage=4&pageSize=1", null));
        assertEquals(3, empty.path("totalSize").asInt());
        assertEquals(0, empty.path("data").size());
        assertProblem(request("GET", "/api/orders?sort=id%3BDROP%20TABLE%20purchase_orders", null), 400);
        assertProblem(request("GET", "/api/orders?sort=unknown", null), 400);
        var row = orders.selectById(ids.getFirst());
        assertNotNull(row.getCreateDate());
        assertNotNull(row.getModifyDate());
        assertEquals(3, countOrders());
    }

    @Test
    void customLastParameterProcessorRestoresNestedAndExceptionalRoutes() {
        assertNull(DynamicDataSourceContextHolder.peek());
        schemas.execute(DataScope.MAIN.dataSource(), () -> {
            assertEquals(DataScope.MAIN.dataSource(), DynamicDataSourceContextHolder.peek());
            return null;
        });
        assertThrows(IllegalStateException.class, () -> schemas.execute(DataScope.MYSQL.dataSource(), () -> {
            throw new IllegalStateException("simulated failure");
        }));
        assertNull(DynamicDataSourceContextHolder.peek());
        assertThrows(RuntimeException.class, () -> invoker.invoke(() -> orders.selectCount(null), "unknownDataSource"));
        assertNull(DynamicDataSourceContextHolder.peek());
        assertThrows(IllegalStateException.class, () -> transactions.execute(status ->
                schemas.execute(DataScope.MYSQL.dataSource(), () -> "must not switch an active transaction")));
        assertNull(DynamicDataSourceContextHolder.peek());
    }

    @Test
    void kumaMybatisIsActiveAndRejectsAnUpdateWithoutWhere() {
        assertNotNull(kumaMybatisUtil);
        assertEquals(1, context.getBeansOfType(SqlSessionFactory.class).size());
        assertInstanceOf(com.baomidou.mybatisplus.core.MybatisConfiguration.class, sqlSessionFactory.getConfiguration());
        var plugin = sqlSessionFactory.getConfiguration().getInterceptors().stream()
                .filter(MybatisPlusInterceptor.class::isInstance)
                .map(MybatisPlusInterceptor.class::cast).findFirst().orElseThrow();
        assertTrue(plugin.getInterceptors().stream().anyMatch(BlockAttackInnerInterceptor.class::isInstance));
        var error = assertThrows(RuntimeException.class, () -> transactions.executeWithoutResult(status -> {
            // 即使拦截器意外失效，也强制回滚，不能修改已有演示订单。
            try { unsafeOrderMapper.updateWithoutWhere(); }
            finally { status.setRollbackOnly(); }
        }));
        Throwable cause = error;
        while (cause.getCause() != null) cause = cause.getCause();
        assertInstanceOf(MybatisPlusException.class, cause);
    }

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).timeout(Duration.ofSeconds(8));
        if (body != null) builder.header("Content-Type", "application/json");
        return http.send(builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }

    private void assertProblem(HttpResponse<String> response, int status) {
        assertEquals(status, response.statusCode(), response.body());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("application/json"));
        JsonNode problem = JSON.readTree(response.body());
        assertEquals("FAILURE", problem.path("status").asString(), response.body());
        assertEnvelope(problem, response);
        assertTrue(problem.path("code").isTextual(), response.body());
        assertTrue(problem.path("message").isTextual(), response.body());
        assertFalse(response.body().contains("Exception"));
        if (status == 400) assertEquals(ResultEnum.BAD_REQUEST.codeDesc(), problem.path("code").asString(), response.body());
    }

    private JsonNode successData(HttpResponse<String> response) {
        JsonNode result = JSON.readTree(response.body());
        assertEquals("SUCCESS", result.path("status").asString(), response.body());
        assertEquals(ResultEnum.SUCCESS.codeDesc(), result.path("code").asString(), response.body());
        assertEnvelope(result, response);
        return result.path("data");
    }

    private void assertEnvelope(JsonNode result, HttpResponse<String> response) {
        assertTrue(result.path("timestamp").asLong() > 0, response.body());
        assertFalse(result.path("version").asString().isBlank(), response.body());
        assertEquals(response.headers().firstValue("kmc-trace-id").orElseThrow(),
                result.path("requestId").asString(), response.body());
    }

    private long countOrders() throws Exception {
        try (var connection = dataSource.getConnection(); var query = connection.prepareStatement("SELECT COUNT(*) FROM purchase_orders");
             var result = query.executeQuery()) {
            result.next();
            return result.getLong(1);
        }
    }

}
