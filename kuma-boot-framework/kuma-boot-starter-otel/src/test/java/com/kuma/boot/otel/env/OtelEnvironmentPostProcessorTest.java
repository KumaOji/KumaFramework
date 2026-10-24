package com.kuma.boot.otel.env;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.micrometer.tracing.opentelemetry.autoconfigure.otlp.OtlpTracingProperties;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class OtelEnvironmentPostProcessorTest {
    private final OtelEnvironmentPostProcessor processor = new OtelEnvironmentPostProcessor();
    private static final String OTLP = "management.opentelemetry.tracing.export.otlp";

    @Test void mapsKumaOptionsToBoot4BindableProperties() {
        var environment = new MockEnvironment()
                .withProperty("kuma.boot.otel.enabled", "true")
                .withProperty("kuma.boot.otel.endpoint", "http://localhost:4318/v1/traces")
                .withProperty("kuma.boot.otel.timeout", "3s")
                .withProperty("kuma.boot.otel.compression", "gzip")
                .withProperty("kuma.boot.otel.headers.X-Kuma-Test", "example")
                .withProperty("kuma.boot.otel.sampling.probability", "0.75");
        processor.postProcessEnvironment(environment, new SpringApplication());

        var properties = Binder.get(environment).bind(OTLP, OtlpTracingProperties.class).orElseThrow(IllegalStateException::new);
        assertThat(properties.getEndpoint()).isEqualTo("http://localhost:4318/v1/traces");
        assertThat(properties.getTimeout()).isEqualTo(Duration.ofSeconds(3));
        assertThat(properties.getCompression()).isEqualTo(OtlpTracingProperties.Compression.GZIP);
        assertThat(properties.getHeaders()).containsEntry("X-Kuma-Test", "example");
        assertThat(environment.getProperty("management.tracing.enabled", Boolean.class)).isTrue();
        assertThat(environment.getProperty("management.tracing.sampling.probability", Double.class)).isEqualTo(0.75);
        assertThat(environment.getProperty("management.otlp.tracing.endpoint")).isNull();
    }

    @Test void explicitNativePropertiesOverrideKumaDefaults() {
        var environment = new MockEnvironment()
                .withProperty("kuma.boot.otel.enabled", "true")
                .withProperty("kuma.boot.otel.endpoint", "http://localhost:4318/v1/traces")
                .withProperty("kuma.boot.otel.timeout", "3s")
                .withProperty("kuma.boot.otel.headers.X-Kuma-Test", "kuma")
                .withProperty(OTLP + ".endpoint", "http://localhost:54318/v1/traces")
                .withProperty(OTLP + ".timeout", "7s")
                .withProperty(OTLP + ".headers.X-Kuma-Test", "native")
                .withProperty("management.tracing.enabled", "false")
                .withProperty("management.tracing.sampling.probability", "0.25");
        processor.postProcessEnvironment(environment, new SpringApplication());

        var properties = Binder.get(environment).bind(OTLP, OtlpTracingProperties.class).orElseThrow(IllegalStateException::new);
        assertThat(properties.getEndpoint()).isEqualTo("http://localhost:54318/v1/traces");
        assertThat(properties.getTimeout()).isEqualTo(Duration.ofSeconds(7));
        assertThat(properties.getHeaders()).containsEntry("X-Kuma-Test", "native");
        assertThat(environment.getProperty("management.tracing.enabled", Boolean.class)).isFalse();
        assertThat(environment.getProperty("management.tracing.sampling.probability", Double.class)).isEqualTo(0.25);
    }

    @Test void absentOrDisabledKumaMappingLeavesNativeConfigurationAlone() {
        for (String enabled : new String[]{"false", ""}) {
            var environment = new MockEnvironment().withProperty(OTLP + ".endpoint", "http://localhost:54318/v1/traces");
            if (!enabled.isEmpty()) environment.setProperty("kuma.boot.otel.enabled", enabled);
            processor.postProcessEnvironment(environment, new SpringApplication());
            assertThat(environment.getPropertySources().contains("kumaBootOtel")).isFalse();
            assertThat(environment.getProperty(OTLP + ".endpoint")).isEqualTo("http://localhost:54318/v1/traces");
        }
    }

    @Test void repeatedProcessingAddsOnlyOneFallbackSource() {
        var environment = new MockEnvironment().withProperty("kuma.boot.otel.enabled", "true");
        processor.postProcessEnvironment(environment, new SpringApplication());
        var original = environment.getPropertySources().get("kumaBootOtel");
        processor.postProcessEnvironment(environment, new SpringApplication());
        assertThat(environment.getPropertySources().get("kumaBootOtel")).isSameAs(original);
        assertThat(environment.getProperty(OTLP + ".endpoint")).isEqualTo("http://localhost:4318/v1/traces");
    }
}
