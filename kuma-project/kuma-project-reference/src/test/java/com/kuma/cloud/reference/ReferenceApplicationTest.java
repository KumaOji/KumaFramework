package com.kuma.cloud.reference;

import static org.junit.jupiter.api.Assertions.*;

import com.kuma.cloud.reference.mapper.OrderMapper;
import com.kuma.cloud.reference.domain.entity.OrderDO;
import com.zaxxer.hikari.HikariDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceExecutor;
import com.kuma.cloud.reference.support.datasource.JdbcSchemaInspector;
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
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
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
    @Autowired JdbcSchemaInspector inspector;
    @Autowired DataSourceInvoker invoker;
    @Autowired OrderMapper orders;
    @Autowired TransactionTemplate transactions;
    @Autowired MybatisUtil kumaMybatisUtil;
    @Autowired SqlSessionFactory sqlSessionFactory;
    @Autowired org.springframework.context.ApplicationContext context;
    @Autowired UnsafeOrderMapper unsafeOrderMapper;
    @Autowired @Qualifier("asyncThreadPoolTaskExecutor") ThreadPoolTaskExecutor executor;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

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
                    schemas.execute(DataScope.SANDBOX.dataSource(), "sandbox", () -> {
                        assertEquals("sandbox", com.kuma.boot.common.holder.TenantContextHolder.getTenant());
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
        var body = JSON.readTree(created.body());
        var location = created.headers().firstValue("Location").orElseThrow();
        assertEquals("/api/orders/" + body.path("id").asString() + "?scope=MAIN", location);
        assertEquals(198, body.path("totalAmount").asInt());
        assertEquals("CREATED", body.path("status").asString());

        var read = request("GET", location, null);
        assertEquals(200, read.statusCode(), read.body());
        assertEquals("BOOK-001", JSON.readTree(read.body()).path("sku").asString());
        String confirmPath = "/api/orders/" + body.path("id").asString() + "/confirm?scope=MAIN";
        var confirmed = request("POST", confirmPath, null);
        assertEquals(200, confirmed.statusCode(), confirmed.body());
        assertEquals("CONFIRMED", JSON.readTree(confirmed.body()).path("status").asString());
        assertEquals(1, JSON.readTree(confirmed.body()).path("version").asInt());
        assertProblem(request("POST", confirmPath, null), 409);
    }

    @Test
    void validatesInputAndReturns404ForMissingOrders() throws Exception {
        long before = countOrders();
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"\",\"unitPrice\":99,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":-1,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":1.001,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":0}"), 400);
        assertProblem(request("POST", "/api/orders", "{\"sku\":\"../unsafe\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":1}"), 400);
        assertProblem(request("POST", "/api/orders", "not-json"), 400);
        assertProblem(request("GET", "/api/orders/" + UUID.randomUUID(), null), 404);
        assertProblem(request("GET", "/api/orders/not-a-uuid", null), 400);
        assertEquals(before, countOrders());
    }

    @Test
    void rejectsStaleWritesAndRollsBackFailedTransactions() throws Exception {
        var created = JSON.readTree(request("POST", "/api/orders", "{\"sku\":\"BOOK-001\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":1}").body());
        var id = UUID.fromString(created.path("id").asString());
        var snapshot = orders.selectById(id.toString()).toDomain();
        transactions.executeWithoutResult(status -> assertEquals(1,
                orders.updateStatus(id.toString(), "CONFIRMED", snapshot.version() + 1, snapshot.version())));
        transactions.executeWithoutResult(status -> assertEquals(0,
                orders.updateStatus(id.toString(), "CONFIRMED", snapshot.version() + 1, snapshot.version())));
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
            assertProblem(request("GET", "/api/orders/" + UUID.randomUUID(), null), 503);
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
    void qoVoPaginationAndSchemaRoutingKeepOrdersIsolated() throws Exception {
        String sku = "ISOLATION-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var main = JSON.readTree(request("POST", "/api/orders?scope=MAIN", "{\"sku\":\"" + sku + "\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":1}").body());
        var sandbox = JSON.readTree(request("POST", "/api/orders?scope=SANDBOX", "{\"sku\":\"" + sku + "\",\"productName\":\"Sample product\",\"unitPrice\":99.00,\"quantity\":2}").body());
        assertProblem(request("GET", "/api/orders/" + main.path("id").asString() + "?scope=SANDBOX", null), 404);
        assertProblem(request("GET", "/api/orders/" + sandbox.path("id").asString() + "?scope=MAIN", null), 404);
        for (DataScope scope : DataScope.values()) {
            var page = request("GET", "/api/orders?scope=" + scope + "&sku=" + sku + "&status=CREATED&page=1&size=1", null);
            assertEquals(200, page.statusCode(), page.body());
            var result = JSON.readTree(page.body());
            assertEquals(1, result.path("total").asInt());
            assertEquals(1, result.path("items").size());
            assertEquals(scope == DataScope.MAIN ? 1 : 2, result.path("items").get(0).path("quantity").asInt());
        }
        assertEquals(200, request("GET", "/api/orders", null).statusCode());
        assertProblem(request("GET", "/api/orders?page=0", null), 400);
        assertProblem(request("GET", "/api/orders?size=101", null), 400);
        assertProblem(request("GET", "/api/orders?status=WRONG", null), 400);
        assertProblem(request("GET", "/api/orders?scope=public", null), 400);
    }

    @Test
    void customLastParameterProcessorRestoresNestedAndExceptionalRoutes() {
        assertNull(DynamicDataSourceContextHolder.peek());
        schemas.execute(DataScope.MAIN.dataSource(), () -> {
            assertEquals("kuma_reference_main", inspector.inspect(DataScope.MAIN).schema());
            assertEquals("kuma_reference_sandbox", inspector.inspect(DataScope.SANDBOX).schema());
            assertEquals(DataScope.MAIN.dataSource(), DynamicDataSourceContextHolder.peek());
            return null;
        });
        assertThrows(IllegalStateException.class, () -> schemas.execute(DataScope.SANDBOX.dataSource(), () -> {
            throw new IllegalStateException("simulated failure");
        }));
        assertNull(DynamicDataSourceContextHolder.peek());
        assertThrows(RuntimeException.class, () -> invoker.invoke(() -> orders.count(null, null), "unknownDataSource"));
        assertNull(DynamicDataSourceContextHolder.peek());
        assertEquals("kuma_reference_main", inspector.inspect(DataScope.MAIN).schema());
        assertThrows(IllegalStateException.class, () -> transactions.execute(status ->
                schemas.execute(DataScope.SANDBOX.dataSource(), () -> "must not switch an active transaction")));
        assertNull(DynamicDataSourceContextHolder.peek());
    }

    @Test
    void concurrentRequestsReportTheActualSchemaWithoutPoolLeakage() throws Exception {
        var calls = new ArrayList<java.util.concurrent.CompletableFuture<HttpResponse<String>>>();
        for (int i = 0; i < 20; i++) {
            var scope = i % 2 == 0 ? DataScope.MAIN : DataScope.SANDBOX;
            var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/datasources/current?scope=" + scope))
                    .timeout(Duration.ofSeconds(8)).GET().build();
            calls.add(http.sendAsync(request, HttpResponse.BodyHandlers.ofString()));
        }
        for (int i = 0; i < calls.size(); i++) {
            var response = calls.get(i).get(8, TimeUnit.SECONDS);
            assertEquals(200, response.statusCode(), response.body());
            assertEquals(i % 2 == 0 ? "kuma_reference_main" : "kuma_reference_sandbox",
                    JSON.readTree(response.body()).path("schema").asString());
        }
        for (DataScope scope : DataScope.values()) {
            assertEquals(5, dataSource.getDataSource(scope.dataSource()).unwrap(HikariDataSource.class).getMaximumPoolSize());
        }
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
        assertTrue(problem.path("code").isTextual(), response.body());
        assertTrue(problem.path("message").isTextual(), response.body());
        assertFalse(response.body().contains("Exception"));
    }

    private long countOrders() throws Exception {
        try (var connection = dataSource.getConnection(); var query = connection.prepareStatement("SELECT COUNT(*) FROM purchase_orders");
             var result = query.executeQuery()) {
            result.next();
            return result.getLong(1);
        }
    }

}
