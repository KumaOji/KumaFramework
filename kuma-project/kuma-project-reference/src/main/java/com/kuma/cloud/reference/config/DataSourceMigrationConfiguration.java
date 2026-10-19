package com.kuma.cloud.reference.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.kuma.cloud.reference.domain.enums.DataScope;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DataSourceMigrationConfiguration {
    /** Migrate every physical datasource instead of only the routing datasource's default. */
    @Bean
    SmartInitializingSingleton migrateReferenceDataSources(DynamicRoutingDataSource dataSource) {
        return () -> {
            for (DataScope scope : DataScope.values()) {
                Flyway.configure().dataSource(dataSource.getDataSource(scope.dataSource()))
                        .defaultSchema(scope.schema()).schemas(scope.schema()).createSchemas(false)
                        .locations("classpath:db/migration").cleanDisabled(true).validateOnMigrate(true)
                        .load().migrate();
            }
        };
    }
}
