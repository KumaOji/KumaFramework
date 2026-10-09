package com.kuma.cloud.lab.observability;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 使用真实 OTel SDK 的有界实验。每次创建独立 SDK，不注册 GlobalOpenTelemetry，
 * 因而不会替换业务应用的 Tracer。默认仅在内存中收集，--export 才发送 trace。
 * SimpleSpanProcessor 方便教学观察；生产通常使用 BatchSpanProcessor。
 */
public final class OtelLearningDemo {
    private OtelLearningDemo() { }

    public record SpanEvidence(String name, String traceId, String spanId, String parentSpanId,
                               String kind, String status, double durationMs, Map<String, String> attributes,
                               List<String> events) { }
    public record LogEvidence(String body, String traceId, String spanId, String severity) { }
    public record MetricEvidence(String name, String type, List<Double> values) { }
    public record Report(String traceId, String traceparent, List<SpanEvidence> spans,
                         List<LogEvidence> logs, List<MetricEvidence> metrics,
                         Map<String, Integer> sampledSpans, Map<String, Boolean> checks,
                         String backendStatus) { }

    public static void main(String[] args) throws Exception {
        if (args.length != 0 && (args.length != 3 || !args[0].equals("--export"))) {
            throw new IllegalArgumentException("Usage: OtelLearningDemo [--export <OTLP HTTP /v1/traces URL> <OAP query URL>]");
        }
        Report report = run(args.length == 0 ? null : args[1]);
        if (args.length != 0) {
            verifyBackend(report, args[2], Duration.ofSeconds(90));
            report = new Report(report.traceId(), report.traceparent(), report.spans(), report.logs(),
                    report.metrics(), report.sampledSpans(), report.checks(), "VERIFIED_IN_SKYWALKING");
        }
        System.out.println(JsonMapper.builder().build().writerWithDefaultPrettyPrinter().writeValueAsString(report));
    }

    public static Report run(String exportEndpoint) throws Exception {
        Capture capture = new Capture();
        Resource resource = Resource.getDefault().merge(Resource.create(Attributes.builder()
                .put("service.name", "kuma-lab-otel").put("service.version", "lesson-1").build()));
        var traceBuilder = SdkTracerProvider.builder().setResource(resource)
                .setSampler(Sampler.parentBased(Sampler.alwaysOn()))
                .addSpanProcessor(SimpleSpanProcessor.create(capture));
        if (exportEndpoint != null) {
            traceBuilder.addSpanProcessor(SimpleSpanProcessor.create(OtlpHttpSpanExporter.builder()
                    .setEndpoint(exportEndpoint).setTimeout(Duration.ofSeconds(5)).build()));
        }
        PeriodicMetricReader reader = PeriodicMetricReader.builder(capture.metricExporter())
                .setInterval(Duration.ofHours(1)).build();
        try (OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setTracerProvider(traceBuilder.build())
                .setMeterProvider(SdkMeterProvider.builder().setResource(resource).registerMetricReader(reader).build())
                .setLoggerProvider(SdkLoggerProvider.builder().setResource(resource)
                        .addLogRecordProcessor(SimpleLogRecordProcessor.create(capture.logExporter())).build())
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance())).build()) {
            var tracer = sdk.getTracer("kuma-observability-lesson");
            var meter = sdk.getMeter("kuma-observability-lesson");
            var requests = meter.counterBuilder("lab.checkout.requests").build();
            var latency = meter.histogramBuilder("lab.checkout.duration").setUnit("ms").build();
            var logger = sdk.getLogsBridge().get("kuma-observability-lesson");
            Map<String, String> headers = new LinkedHashMap<>();
            // 显式根上下文：即使从带有业务 trace 的 HTTP 请求进入，实验仍自成一条链路。
            Span root = tracer.spanBuilder("checkout").setParent(Context.root()).setSpanKind(SpanKind.SERVER)
                    .setAttribute("lab.synthetic", true).startSpan();
            long start = System.nanoTime();
            try (Scope rootScope = root.makeCurrent()) {
                root.addEvent("order.received");
                requests.add(1, Attributes.builder().put("outcome", "simulated-error").build());
                logger.logRecordBuilder().setContext(Context.current()).setSeverity(Severity.INFO)
                        .setBody("checkout started; synthetic order").emit();

                Span client = tracer.spanBuilder("inventory.call").setSpanKind(SpanKind.CLIENT).startSpan();
                try (Scope clientScope = client.makeCurrent()) {
                    sdk.getPropagators().getTextMapPropagator().inject(Context.current(), headers, Map::put);
                    Context remote = sdk.getPropagators().getTextMapPropagator()
                            .extract(Context.root(), headers, new TextMapGetter<Map<String, String>>() {
                                public Iterable<String> keys(Map<String, String> carrier) { return carrier.keySet(); }
                                public String get(Map<String, String> carrier, String key) { return carrier.get(key); }
                            });
                    // 载体为 Map，模拟 HTTP 接收端；没有实际远程网络请求。
                    Span server = tracer.spanBuilder("inventory.receive").setParent(remote)
                            .setSpanKind(SpanKind.SERVER).setAttribute("lab.propagation", "W3C traceparent").startSpan();
                    try (Scope remoteScope = server.makeCurrent()) {
                        Span slow = tracer.spanBuilder("inventory.lookup").setAttribute("lab.simulated.delay_ms", 40L).startSpan();
                        try { Thread.sleep(40); } finally { slow.end(); }
                    } finally { server.end(); }
                } finally { client.end(); }

                Context parent = Context.current();
                try (var executor = Executors.newSingleThreadExecutor()) {
                    executor.submit(() -> {
                        Span detached = tracer.spanBuilder("async.without-context").startSpan();
                        detached.end(); // Executor 不会自动继承当前线程的 OTel Context。
                    }).get(5, TimeUnit.SECONDS);
                    executor.submit(parent.wrap(() -> {
                        Span attached = tracer.spanBuilder("async.with-context").startSpan();
                        attached.end();
                    })).get(5, TimeUnit.SECONDS);
                }
                Span payment = tracer.spanBuilder("payment.failure").startSpan();
                try (Scope paymentScope = payment.makeCurrent()) {
                    IllegalStateException failure = new IllegalStateException("synthetic payment rejected");
                    payment.recordException(failure); // 异常事件与 ERROR 状态是两个独立操作。
                    payment.setStatus(StatusCode.ERROR, failure.getMessage());
                    root.setStatus(StatusCode.ERROR, "payment failed");
                    logger.logRecordBuilder().setContext(Context.current()).setSeverity(Severity.ERROR)
                            .setBody(failure.getMessage()).emit();
                } finally { payment.end(); }
            } finally {
                latency.record((System.nanoTime() - start) / 1_000_000.0);
                root.end();
            }
            requireSuccess(sdk.getSdkTracerProvider().forceFlush(), "trace flush");
            requireSuccess(sdk.getSdkLoggerProvider().forceFlush(), "log flush");
            requireSuccess(reader.forceFlush(), "metric collection");

            Map<String, Integer> sampled = new LinkedHashMap<>();
            sampled.put("alwaysOn", sample(Sampler.alwaysOn()));
            sampled.put("alwaysOff", sample(Sampler.alwaysOff()));
            sampled.put("parentBased-unsampled", sample(Sampler.parentBased(Sampler.alwaysOff())));
            Map<String, Boolean> checks = new LinkedHashMap<>();
            SpanData rootData = capture.named("checkout");
            checks.put("sevenSpansEnded", capture.spans.size() == 7);
            checks.put("remoteParentPreserved", capture.named("inventory.receive").getParentSpanId()
                    .equals(capture.named("inventory.call").getSpanId()));
            checks.put("remoteTracePreserved", capture.named("inventory.receive").getTraceId().equals(rootData.getTraceId()));
            checks.put("asyncContextLost", !capture.named("async.without-context").getTraceId().equals(rootData.getTraceId()));
            checks.put("asyncContextRestored", capture.named("async.with-context").getParentSpanId().equals(rootData.getSpanId()));
            checks.put("errorStatusAndException", capture.named("payment.failure").getStatus().getStatusCode() == StatusCode.ERROR
                    && capture.named("payment.failure").getEvents().stream().anyMatch(e -> e.getName().equals("exception")));
            checks.put("logsCorrelated", capture.logs.size() == 2 && capture.logs.stream()
                    .allMatch(log -> log.getSpanContext().getTraceId().equals(rootData.getTraceId())));
            checks.put("counterEqualsOne", capture.metrics.stream().filter(m -> m.getName().equals("lab.checkout.requests"))
                    .flatMap(m -> m.getLongSumData().getPoints().stream()).anyMatch(p -> p.getValue() == 1));
            checks.put("histogramRecorded", capture.metrics.stream().filter(m -> m.getName().equals("lab.checkout.duration"))
                    .flatMap(m -> m.getHistogramData().getPoints().stream()).anyMatch(p -> p.getCount() == 1));
            checks.put("samplingHonored", sampled.equals(Map.of("alwaysOn", 2, "alwaysOff", 0, "parentBased-unsampled", 0)));
            if (checks.containsValue(false)) throw new IllegalStateException("Lesson checks failed: " + checks);
            return new Report(rootData.getTraceId(), headers.get("traceparent"), capture.spans.stream().map(OtelLearningDemo::evidence).toList(),
                    capture.logs.stream().map(log -> new LogEvidence(log.getBodyValue().asString(), log.getSpanContext().getTraceId(),
                            log.getSpanContext().getSpanId(), log.getSeverity().name())).toList(),
                    capture.metrics.stream().map(OtelLearningDemo::metricEvidence).toList(), sampled, checks,
                    exportEndpoint == null ? "LOCAL_ONLY" : "EXPORT_ATTEMPTED_NOT_YET_QUERIED");
        }
    }

    private static int sample(Sampler sampler) {
        Capture capture = new Capture();
        try (var provider = SdkTracerProvider.builder().setSampler(sampler)
                .addSpanProcessor(SimpleSpanProcessor.create(capture)).build()) {
            var tracer = provider.get("sampling-lesson");
            Span root = tracer.spanBuilder("sample.root").setParent(Context.root()).startSpan();
            Span child = tracer.spanBuilder("sample.child").setParent(Context.root().with(root)).startSpan();
            child.end();
            root.end();
        }
        return capture.spans.size();
    }

    static SpanEvidence evidence(SpanData span) {
        Map<String, String> attributes = new LinkedHashMap<>();
        span.getAttributes().forEach((key, value) -> attributes.put(key.getKey(), value.toString()));
        return new SpanEvidence(span.getName(), span.getTraceId(), span.getSpanId(), span.getParentSpanId(),
                span.getKind().name(), span.getStatus().getStatusCode().name(),
                (span.getEndEpochNanos() - span.getStartEpochNanos()) / 1_000_000.0, attributes,
                span.getEvents().stream().map(e -> e.getName()).toList());
    }

    private static MetricEvidence metricEvidence(MetricData metric) {
        List<Double> values = switch (metric.getType()) {
            case LONG_SUM -> metric.getLongSumData().getPoints().stream().map(p -> (double) p.getValue()).toList();
            case HISTOGRAM -> metric.getHistogramData().getPoints().stream().map(p -> p.getSum()).toList();
            default -> List.of();
        };
        return new MetricEvidence(metric.getName(), metric.getType().name(), values);
    }

    private static void requireSuccess(CompletableResultCode result, String operation) {
        result.join(10, TimeUnit.SECONDS);
        if (!result.isSuccess()) throw new IllegalStateException(operation + " failed or timed out");
    }

    /** SDK 接收成功不等于存储成功：必须从 OAP 按 ID 查回本条 trace 的全部六个 span。 */
    public static void verifyBackend(Report report, String queryUrl, Duration timeout) throws Exception {
        List<String> expected = report.spans().stream().filter(s -> s.traceId().equals(report.traceId()))
                .map(SpanEvidence::spanId).toList();
        verifyTrace(report.traceId(), expected, queryUrl, timeout);
    }

    static void verifyTrace(String traceId, List<String> expected, String queryUrl, Duration timeout) throws Exception {
        if (expected.isEmpty()) throw new IllegalArgumentException("Expected span IDs must not be empty");
        URI uri = URI.create(queryUrl.replaceAll("/+$", "") + "/zipkin/api/v2/trace/" + traceId);
        long deadline = System.nanoTime() + timeout.toNanos();
        String last = "not queried";
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build()) {
            while (System.nanoTime() < deadline) {
                try {
                    var response = client.send(HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(5)).GET().build(),
                            HttpResponse.BodyHandlers.ofString());
                    last = "HTTP " + response.statusCode();
                    if (response.statusCode() == 200) {
                        var body = JsonMapper.builder().build().readTree(response.body());
                        List<String> actual = new ArrayList<>();
                        if (body.isArray()) for (var span : body) {
                            if (span.path("traceId").asString().equals(traceId)) actual.add(span.path("id").asString());
                        }
                        if (actual.containsAll(expected)) return;
                        last += "; stored spans=" + actual.size() + "/" + expected.size();
                    }
                } catch (java.io.IOException | RuntimeException error) { last = error.toString(); }
                Thread.sleep(1000);
            }
        }
        throw new IllegalStateException("SkyWalking query verification failed for " + traceId + ": " + last);
    }

    private static final class Capture implements SpanExporter {
        final List<SpanData> spans = new CopyOnWriteArrayList<>();
        final List<LogRecordData> logs = new CopyOnWriteArrayList<>();
        final List<MetricData> metrics = new CopyOnWriteArrayList<>();
        SpanData named(String name) { return spans.stream().filter(s -> s.getName().equals(name)).findFirst().orElseThrow(); }
        public CompletableResultCode export(Collection<SpanData> data) { spans.addAll(data); return CompletableResultCode.ofSuccess(); }
        public CompletableResultCode flush() { return CompletableResultCode.ofSuccess(); }
        public CompletableResultCode shutdown() { return CompletableResultCode.ofSuccess(); }
        LogRecordExporter logExporter() {
            return new LogRecordExporter() {
                public CompletableResultCode export(Collection<LogRecordData> data) { logs.addAll(data); return flush(); }
                public CompletableResultCode flush() { return CompletableResultCode.ofSuccess(); }
                public CompletableResultCode shutdown() { return flush(); }
            };
        }
        MetricExporter metricExporter() {
            return new MetricExporter() {
                public AggregationTemporality getAggregationTemporality(io.opentelemetry.sdk.metrics.InstrumentType type) {
                    return AggregationTemporality.CUMULATIVE;
                }
                public CompletableResultCode export(Collection<MetricData> data) { metrics.clear(); metrics.addAll(data); return flush(); }
                public CompletableResultCode flush() { return CompletableResultCode.ofSuccess(); }
                public CompletableResultCode shutdown() { return flush(); }
            };
        }
    }
}
