package com.kuma.cloud.lab.binlog.service;

import com.kuma.cloud.lab.binlog.domain.MysqlBinlogChangeEvent;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
class DebeziumEventMapper {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() { };

    private final ObjectMapper objectMapper;

    DebeziumEventMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Optional<MysqlBinlogChangeEvent> map(String json) {
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode payload = root.has("payload") ? root.path("payload") : root;
            JsonNode source = payload.path("source");
            String table = text(source, "table");
            MysqlBinlogChangeEvent.Operation operation = operation(text(payload, "op"));
            if (table == null || operation == null) {
                return Optional.empty();
            }

            String file = text(source, "file");
            Long position = number(source, "pos");
            Long row = number(source, "row");
            String id = (file == null ? "snapshot" : file)
                    + ':' + (position == null ? 0 : position)
                    + ':' + (row == null ? 0 : row);
            long timestamp = payload.path("ts_ms").asLong(System.currentTimeMillis());

            return Optional.of(new MysqlBinlogChangeEvent(
                    id,
                    operation,
                    text(source, "db"),
                    table,
                    map(payload.get("before")),
                    map(payload.get("after")),
                    text(payload.path("transaction"), "id"),
                    Instant.ofEpochMilli(timestamp),
                    file,
                    position,
                    !source.path("snapshot").isMissingNode()
                            && !source.path("snapshot").asText("false").equals("false")
            ));
        } catch (Exception error) {
            throw new IllegalArgumentException("Cannot parse Debezium change event", error);
        }
    }

    private Map<String, Object> map(JsonNode node) {
        return node == null || node.isNull() ? null : objectMapper.convertValue(node, MAP_TYPE);
    }

    private static MysqlBinlogChangeEvent.Operation operation(String code) {
        return switch (code == null ? "" : code) {
            case "c" -> MysqlBinlogChangeEvent.Operation.CREATE;
            case "u" -> MysqlBinlogChangeEvent.Operation.UPDATE;
            case "d" -> MysqlBinlogChangeEvent.Operation.DELETE;
            case "r" -> MysqlBinlogChangeEvent.Operation.READ;
            default -> null;
        };
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private static Long number(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNumber() ? value.longValue() : null;
    }
}
