package com.kuma.cloud.lab.binlog.config;

import java.net.URI;

/**
 * Host and logical database parsed from a standard MySQL JDBC URL.
 */
public record MysqlJdbcEndpoint(String hostname, int port, String database) {

    public static MysqlJdbcEndpoint parse(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:mysql://")) {
            throw new IllegalArgumentException("mysql-binlog.jdbc-url must be a jdbc:mysql:// URL");
        }

        URI uri = URI.create(jdbcUrl.substring("jdbc:".length()));
        String path = uri.getPath();
        String database = path == null || path.length() <= 1 ? null : path.substring(1);
        if (uri.getHost() == null || database == null || database.isBlank()) {
            throw new IllegalArgumentException("mysql-binlog.jdbc-url must contain a host and database");
        }
        return new MysqlJdbcEndpoint(uri.getHost(), uri.getPort() < 0 ? 3306 : uri.getPort(), database);
    }
}
