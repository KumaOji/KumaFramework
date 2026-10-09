package com.kuma.cloud.lab.observability;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ObservabilityLearningTest {
    @Test
    void realSdkCapturesSignalsAndRestoresCallerContext() throws Exception {
        Context before = Context.current();
        var report = OtelLearningDemo.run(null);
        assertSame(before, Context.current());
        assertEquals("LOCAL_ONLY", report.backendStatus());
        assertEquals(7, report.spans().size());
        assertEquals(2, report.logs().size());
        assertEquals(2, report.metrics().size());
        assertTrue(report.checks().values().stream().allMatch(Boolean::booleanValue));
        assertTrue(report.traceparent().matches("00-[a-f0-9]{32}-[a-f0-9]{16}-01"));
        assertEquals(6, report.spans().stream().filter(s -> s.traceId().equals(report.traceId())).count());
    }

    @Test
    void sdkLessonIsIsolatedFromAnExistingCallerTrace() throws Exception {
        var caller = Span.wrap(io.opentelemetry.api.trace.SpanContext.create(
                "11111111111111111111111111111111", "2222222222222222",
                io.opentelemetry.api.trace.TraceFlags.getSampled(), io.opentelemetry.api.trace.TraceState.getDefault()));
        try (Scope scope = caller.makeCurrent()) {
            var report = OtelLearningDemo.run(null);
            assertNotEquals(caller.getSpanContext().getTraceId(), report.traceId());
            assertEquals(caller.getSpanContext(), Span.current().getSpanContext());
        }
    }

    @Test
    void toolkitWithoutAgentReportsMissingTraceAndSimulatesOutcomes() throws Exception {
        var normal = SkyWalkingLearningDemo.checkout("normal");
        var slow = SkyWalkingLearningDemo.checkout("slow");
        var error = SkyWalkingLearningDemo.checkout("error");
        assertFalse(normal.activeTrace());
        assertEquals("SUCCESS", normal.outcome());
        assertEquals("SUCCESS", slow.outcome());
        assertTrue(slow.elapsedMs() >= 190);
        assertEquals("SIMULATED_FAILURE", error.outcome());
        assertThrows(IllegalArgumentException.class, () -> SkyWalkingLearningDemo.checkout("unknown"));
    }

    @Test
    void backendVerificationRequiresEverySpanAndTheCorrectTrace() throws Exception {
        var report = OtelLearningDemo.run(null);
        AtomicReference<String> response = new AtomicReference<>();
        String complete = report.spans().stream().filter(span -> span.traceId().equals(report.traceId()))
                .map(span -> "{\"traceId\":\"" + span.traceId() + "\",\"id\":\"" + span.spanId() + "\"}")
                .collect(java.util.stream.Collectors.joining(",", "[", "]"));
        response.set(complete);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/zipkin/api/v2/trace/" + report.traceId(), exchange -> {
            byte[] body = response.get().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) { output.write(body); }
        });
        server.start();
        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort();
            OtelLearningDemo.verifyBackend(report, url, Duration.ofSeconds(3));
            var first = report.spans().stream().filter(span -> span.traceId().equals(report.traceId())).findFirst().orElseThrow();
            response.set("[{\"traceId\":\"" + first.traceId() + "\",\"id\":\"" + first.spanId() + "\"}]");
            assertThrows(IllegalStateException.class,
                    () -> OtelLearningDemo.verifyBackend(report, url, Duration.ofMillis(100)));
            response.set(complete.replace(report.traceId(), "11111111111111111111111111111111"));
            assertThrows(IllegalStateException.class,
                    () -> OtelLearningDemo.verifyBackend(report, url, Duration.ofMillis(100)));
        } finally { server.stop(0); }
    }
}
