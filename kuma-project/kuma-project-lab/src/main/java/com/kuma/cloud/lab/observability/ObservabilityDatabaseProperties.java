package com.kuma.cloud.lab.observability;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kuma.lab.observability.database")
public class ObservabilityDatabaseProperties {
    /** Empty uses the existing Lab DataSource; explicit URL can select PostgreSQL instead. */
    private String jdbcUrl;
    private String user;
    private String password;
    /** Empty keeps OTel traces local; example http://localhost:4318/v1/traces. */
    private String exportEndpoint;
}
