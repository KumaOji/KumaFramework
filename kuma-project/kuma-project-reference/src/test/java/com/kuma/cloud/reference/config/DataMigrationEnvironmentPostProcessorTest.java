package com.kuma.cloud.reference.config;

import com.kuma.boot.data.migration.env.DataMigrationEnvironmentPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.bootstrap.DefaultBootstrapContext;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class DataMigrationEnvironmentPostProcessorTest {

    @Test
    void flywayModeDisablesLiquibaseBeforeAutoConfiguration() {
        var environment = new MockEnvironment()
                .withProperty("kuma.boot.data.migration.type", "FLYWAY");
        var event = new ApplicationEnvironmentPreparedEvent(
                new DefaultBootstrapContext(), new SpringApplication(), new String[0], environment);

        new DataMigrationEnvironmentPostProcessor().onApplicationEvent(event);

        assertThat(environment.getProperty("spring.liquibase.enabled")).isEqualTo("false");
    }
}
