package com.kuma.cloud.reference.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import com.kuma.boot.core.autoconfigure.ClockAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class StarterAutoDiscoveryTest {
    @Test
    void frameworkFeaturesRemainExplicitlyImportedByApplications() {
        assertThat(ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader())).doesNotContain(
                ClockAutoConfiguration.class.getName(),
                "com.kuma.boot.core.autoconfigure.AsyncThreadPoolAutoConfiguration",
                "com.kuma.boot.logger.autoconfigure.LoggerAutoConfiguration",
                "com.kuma.boot.data.datasource.autoconfigure.TransactionSupportAutoConfiguration",
                "com.kuma.boot.data.mybatis.autoconfigure.MybatisPlusAutoConfiguration",
                "com.kuma.boot.web.autoconfigure.WebMvcAutoConfiguration",
                "com.kuma.boot.actuator.health.autoconfigure.KmcHealthAutoConfiguration");
    }

    @Test
    void applicationCanReplaceTheSharedClockForDeterministicBusinessTests() {
        var fixed = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ClockAutoConfiguration.class))
                .withBean(Clock.class, () -> fixed).run(context -> {
                    assertThat(context).hasSingleBean(Clock.class);
                    assertThat(context.getBean(Clock.class)).isSameAs(fixed);
                });
    }
}
