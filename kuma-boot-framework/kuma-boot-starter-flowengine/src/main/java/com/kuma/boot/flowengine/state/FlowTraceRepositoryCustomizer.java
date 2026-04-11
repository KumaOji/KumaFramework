package com.kuma.boot.flowengine.state;

import org.springframework.jdbc.core.JdbcTemplate;

public interface FlowTraceRepositoryCustomizer {
   FlowTraceRepository customize(String dialect, JdbcTemplate jdbcTemplate);
}
