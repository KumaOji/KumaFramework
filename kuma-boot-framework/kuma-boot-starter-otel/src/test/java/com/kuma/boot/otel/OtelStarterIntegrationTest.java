package com.kuma.boot.otel;

import com.kuma.boot.otel.configuration.OtelAutoConfiguration;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

class OtelStarterIntegrationTest {
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void autoDiscoversStarterAndExportsSpanUsingKumaOrNativeProperties(boolean kumaConfiguration) throws Exception {
        var received = new CountDownLatch(1);
        var payload = new AtomicReference<byte[]>();
        var header = new AtomicReference<String>();
        var compression = new AtomicReference<String>();
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/traces", exchange -> {
            header.set(exchange.getRequestHeaders().getFirst("X-Kuma-Test"));
            compression.set(exchange.getRequestHeaders().getFirst("Content-Encoding"));
            try (var body = "gzip".equals(compression.get())
                    ? new GZIPInputStream(exchange.getRequestBody()) : exchange.getRequestBody()) {
                payload.set(body.readAllBytes());
            }
            exchange.getResponseHeaders().set("Content-Type", "application/x-protobuf");
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            received.countDown();
        });
        server.start();
        String endpoint = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/traces";
        var arguments = new ArrayList<>(List.of("--spring.main.banner-mode=off",
                "--management.otlp.metrics.export.enabled=false"));
        if (kumaConfiguration) {
            arguments.addAll(List.of("--spring.config.location=classpath:otel-test.yml", "--test.collector-url=" + endpoint));
        } else {
            arguments.addAll(List.of("--spring.application.name=kuma-otel-native-test",
                    "--management.tracing.enabled=true", "--management.tracing.sampling.probability=1.0",
                    "--management.opentelemetry.tracing.export.otlp.endpoint=" + endpoint,
                    "--management.opentelemetry.tracing.export.otlp.headers.X-Kuma-Test=native"));
        }
        var application = new SpringApplication(TestApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        try (var context = application.run(arguments.toArray(String[]::new))) {
            assertThat(context.getBeansOfType(Tracer.class)).hasSize(1);
            assertThat(context.getBeansOfType(SdkTracerProvider.class)).hasSize(1);
            assertThat(context.getBeansOfType(SpanExporter.class)).hasSize(1);
            assertThat(context.getBeansOfType(OtelAutoConfiguration.class)).hasSize(kumaConfiguration ? 1 : 0);
            var tracer = context.getBean(Tracer.class);
            var span = tracer.nextSpan().name("kuma-boot4-export").start();
            assertThat(span.context().sampled()).isTrue();
            span.end();
            assertThat(context.getBean(SdkTracerProvider.class).forceFlush().join(10, TimeUnit.SECONDS).isSuccess()).isTrue();
            assertThat(received.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(header.get()).isEqualTo(kumaConfiguration ? "otel-starter" : "native");
            assertThat(compression.get()).isEqualTo(kumaConfiguration ? "gzip" : null);
            assertThat(new String(payload.get(), StandardCharsets.ISO_8859_1)).contains("kuma-boot4-export");
        } finally {
            server.stop(0);
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication { }
}
