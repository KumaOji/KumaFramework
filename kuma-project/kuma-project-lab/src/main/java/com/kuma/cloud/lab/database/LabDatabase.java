package com.kuma.cloud.lab.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

/** Dedicated PostgreSQL database, unique retained schema; H2 must be selected explicitly. */
public final class LabDatabase implements AutoCloseable {
    public static final String DATABASE = "kuma_lab";
    private final HikariDataSource source;
    private final boolean postgresql;

    private LabDatabase(HikariDataSource source, boolean postgresql) {
        this.source = source; this.postgresql = postgresql;
    }

    public static LabDatabase open(String kind, int maximumPoolSize, long connectionTimeout) throws SQLException {
        if (!kind.matches("[a-z][a-z_]{0,20}")) throw new IllegalArgumentException("Invalid experiment kind");
        String engine = System.getProperty("kuma.lab.database.engine", "postgresql");
        boolean postgres = engine.equals("postgresql");
        if (!postgres && !engine.equals("h2")) throw new IllegalArgumentException("Select postgresql or h2");
        String suffix = UUID.randomUUID().toString().replace("-", "");
        HikariConfig config = new HikariConfig();
        config.setPoolName("kuma-" + kind + "-" + suffix);
        config.setMaximumPoolSize(maximumPoolSize); config.setMinimumIdle(1);
        config.setConnectionTimeout(connectionTimeout); config.setValidationTimeout(250); config.setInitializationFailTimeout(3000);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        if (postgres) {
            String url = required("KUMA_LAB_DATABASE_JDBC_URL");
            if (!url.startsWith("jdbc:postgresql:")) throw new IllegalArgumentException("PostgreSQL JDBC URL required");
            String user = required("KUMA_LAB_DATABASE_JDBC_USER");
            String password = required("KUMA_LAB_DATABASE_JDBC_PASSWORD");
            String schema = "lab_" + kind + "_" + suffix;
            Properties settings = new Properties();
            settings.setProperty("user", user); settings.setProperty("password", password);
            settings.setProperty("connectTimeout", "5"); settings.setProperty("socketTimeout", "10");
            try (Connection setup = DriverManager.getConnection(url, settings)) {
                if (!"PostgreSQL".equals(setup.getMetaData().getDatabaseProductName()) || !DATABASE.equals(setup.getCatalog()))
                    throw new IllegalArgumentException("Experiments require the dedicated PostgreSQL database " + DATABASE);
                try (var statement = setup.createStatement()) {
                    statement.setQueryTimeout(5);
                    statement.executeUpdate("CREATE SCHEMA " + schema);
                    statement.executeUpdate("CREATE TABLE IF NOT EXISTS public.lab_runs (schema_name VARCHAR(63) PRIMARY KEY, experiment_kind VARCHAR(30) NOT NULL, lesson VARCHAR(60) NOT NULL, opened_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)");
                }
                try (var record = setup.prepareStatement("INSERT INTO public.lab_runs(schema_name,experiment_kind,lesson) VALUES(?,?,?)")) {
                    record.setQueryTimeout(5); record.setString(1, schema); record.setString(2, kind);
                    record.setString(3, System.getProperty("kuma.lab.database.lesson", "manual")); record.executeUpdate();
                }
                System.out.println("PostgreSQL database=" + DATABASE + ", schema=" + schema + ", version="
                        + setup.getMetaData().getDatabaseProductVersion() + "; results retained");
            }
            config.setJdbcUrl(url); config.setUsername(user); config.setPassword(password);
            config.setSchema(schema);
            config.setConnectionInitSql("SET lock_timeout = '500ms'");
            config.addDataSourceProperty("connectTimeout", "5"); config.addDataSourceProperty("socketTimeout", "10");
        } else {
            config.setJdbcUrl("jdbc:h2:mem:kuma_" + suffix + ";LOCK_TIMEOUT=500");
            config.setUsername("sa"); config.setPassword("");
            System.out.println("Explicit offline H2 mode; database is disposable");
        }
        return new LabDatabase(new HikariDataSource(config), postgres);
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Missing " + name
                + "; run scripts/run-database-labs-wsl.ps1, or explicitly select -PdatabaseEngine=h2 for offline mode");
        return value;
    }
    public HikariDataSource source() { return source; }
    public boolean postgresql() { return postgresql; }
    @Override public void close() { source.close(); }
}
