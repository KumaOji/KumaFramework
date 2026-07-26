package com.kuma.cloud.lab.binlog.service;

import com.kuma.cloud.lab.binlog.config.MysqlBinlogProperties;
import com.kuma.cloud.lab.binlog.domain.MysqlBinlogChangeEvent;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "kuma.lab.mysql-binlog", name = "enabled", havingValue = "true")
public class MysqlBinlogMonitor implements SmartLifecycle {

    private final MysqlBinlogProperties properties;
    private final DebeziumEventMapper eventMapper;
    private final MysqlBinlogEventStore eventStore;
    private final ApplicationEventPublisher eventPublisher;
    private volatile DebeziumEngine<ChangeEvent<String, String>> engine;
    private volatile ExecutorService executor;

    public MysqlBinlogMonitor(
            MysqlBinlogProperties properties,
            DebeziumEventMapper eventMapper,
            MysqlBinlogEventStore eventStore,
            ApplicationEventPublisher eventPublisher
    ) {
        this.properties = properties;
        this.eventMapper = eventMapper;
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public synchronized void start() {
        if (isRunning()) {
            return;
        }
        try {
            MysqlJdbcEndpoint endpoint = MysqlJdbcEndpoint.parse(properties.jdbcUrl());
            createParentDirectory(properties.offsetFile());
            createParentDirectory(properties.schemaHistoryFile());

            engine = DebeziumEngine.create(Json.class)
                    .using(configuration(endpoint))
                    .notifying(this::accept)
                    .using((success, message, error) -> {
                        eventStore.markRunning(false);
                        if (!success) {
                            eventStore.markFailed(error);
                            log.error("MySQL binlog monitor stopped: {}", message, error);
                        }
                    })
                    .build();
            executor = Executors.newSingleThreadExecutor(
                    Thread.ofPlatform().name("lab-mysql-binlog-monitor").factory()
            );
            eventStore.markRunning(true);
            executor.execute(engine);
            log.info("MySQL binlog monitor started for database {}", endpoint.database());
        } catch (Exception error) {
            eventStore.markFailed(error);
            throw new IllegalStateException("Failed to start MySQL binlog monitor", error);
        }
    }

    private Properties configuration(MysqlJdbcEndpoint endpoint) {
        Properties config = new Properties();
        config.setProperty("name", properties.topicPrefix());
        config.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");
        config.setProperty("database.hostname", endpoint.hostname());
        config.setProperty("database.port", Integer.toString(endpoint.port()));
        config.setProperty("database.user", properties.username());
        config.setProperty("database.password", properties.password());
        config.setProperty("database.server.id", Long.toString(properties.serverId()));
        config.setProperty("topic.prefix", properties.topicPrefix());
        config.setProperty("database.include.list", endpoint.database());
        if (properties.tableIncludeList() != null && !properties.tableIncludeList().isBlank()) {
            config.setProperty("table.include.list", properties.tableIncludeList());
        }
        config.setProperty("snapshot.mode", properties.snapshotMode());
        config.setProperty("include.schema.changes", "false");
        config.setProperty("provide.transaction.metadata", "true");
        config.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        config.setProperty("offset.storage.file.filename", absolute(properties.offsetFile()));
        config.setProperty("offset.flush.interval.ms", "1000");
        config.setProperty("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory");
        config.setProperty("schema.history.internal.file.filename", absolute(properties.schemaHistoryFile()));
        config.setProperty("key.converter.schemas.enable", "false");
        config.setProperty("value.converter.schemas.enable", "false");
        return config;
    }

    private void accept(ChangeEvent<String, String> record) {
        try {
            eventMapper.map(record.value()).ifPresent(event -> {
                eventStore.append(event);
                eventPublisher.publishEvent(event);
                log.info("MySQL binlog {} {}.{} at {}:{}",
                        event.operation(), event.database(), event.table(),
                        event.binlogFile(), event.binlogPosition());
            });
        } catch (RuntimeException error) {
            log.error("Ignoring malformed MySQL binlog event from {}", record.destination(), error);
        }
    }

    @Override
    public synchronized void stop() {
        DebeziumEngine<ChangeEvent<String, String>> currentEngine = engine;
        engine = null;
        if (currentEngine != null) {
            try {
                currentEngine.close();
            } catch (Exception error) {
                log.warn("Failed to close MySQL binlog engine cleanly", error);
            }
        }
        ExecutorService currentExecutor = executor;
        executor = null;
        if (currentExecutor != null) {
            currentExecutor.shutdown();
            try {
                if (!currentExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    currentExecutor.shutdownNow();
                }
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                currentExecutor.shutdownNow();
            }
        }
        eventStore.markRunning(false);
    }

    @Override
    public boolean isRunning() {
        ExecutorService current = executor;
        return current != null && !current.isShutdown();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private static void createParentDirectory(String filename) throws Exception {
        Path parent = Path.of(filename).toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    private static String absolute(String filename) {
        return Path.of(filename).toAbsolutePath().normalize().toString();
    }
}
