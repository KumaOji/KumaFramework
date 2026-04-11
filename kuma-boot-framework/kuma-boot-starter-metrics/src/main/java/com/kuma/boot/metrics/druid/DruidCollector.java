package com.taotao.boot.metrics.druid;

import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class DruidCollector {
   private static final String LABEL_NAME = "pool";
   private final List<DruidDataSource> dataSources;
   private final MeterRegistry registry;

   public DruidCollector(List<DruidDataSource> dataSources, MeterRegistry registry) {
      this.registry = registry;
      this.dataSources = dataSources;
   }

   public void register() {
      this.dataSources.forEach((druidDataSource) -> {
         this.createGauge(druidDataSource, "druid_initial_size", "Initial size", (datasource) -> (double)druidDataSource.getInitialSize());
         this.createGauge(druidDataSource, "druid_min_idle", "Min idle", (datasource) -> (double)druidDataSource.getMinIdle());
         this.createGauge(druidDataSource, "druid_max_active", "Max active", (datasource) -> (double)druidDataSource.getMaxActive());
         this.createGauge(druidDataSource, "druid_active_count", "Active count", (datasource) -> (double)druidDataSource.getActiveCount());
         this.createGauge(druidDataSource, "druid_active_peak", "Active peak", (datasource) -> (double)druidDataSource.getActivePeak());
         this.createGauge(druidDataSource, "druid_pooling_peak", "Pooling peak", (datasource) -> (double)druidDataSource.getPoolingPeak());
         this.createGauge(druidDataSource, "druid_pooling_count", "Pooling count", (datasource) -> (double)druidDataSource.getPoolingCount());
         this.createGauge(druidDataSource, "druid_wait_thread_count", "Wait thread count", (datasource) -> (double)druidDataSource.getWaitThreadCount());
         this.createGauge(druidDataSource, "druid_not_empty_wait_count", "Not empty wait count", (datasource) -> (double)druidDataSource.getNotEmptyWaitCount());
         this.createGauge(druidDataSource, "druid_not_empty_wait_millis", "Not empty wait millis", (datasource) -> (double)druidDataSource.getNotEmptyWaitMillis());
         this.createGauge(druidDataSource, "druid_not_empty_thread_count", "Not empty thread count", (datasource) -> (double)druidDataSource.getNotEmptyWaitThreadCount());
         this.createGauge(druidDataSource, "druid_logic_connect_count", "Logic connect count", (datasource) -> (double)druidDataSource.getConnectCount());
         this.createGauge(druidDataSource, "druid_logic_close_count", "Logic close count", (datasource) -> (double)druidDataSource.getCloseCount());
         this.createGauge(druidDataSource, "druid_logic_connect_error_count", "Logic connect error count", (datasource) -> (double)druidDataSource.getConnectErrorCount());
         this.createGauge(druidDataSource, "druid_physical_connect_count", "Physical connect count", (datasource) -> (double)druidDataSource.getCreateCount());
         this.createGauge(druidDataSource, "druid_physical_close_count", "Physical close count", (datasource) -> (double)druidDataSource.getDestroyCount());
         this.createGauge(druidDataSource, "druid_physical_connect_error_count", "Physical connect error count", (datasource) -> (double)druidDataSource.getCreateErrorCount());
         this.createGauge(druidDataSource, "druid_error_count", "Error count", (datasource) -> (double)druidDataSource.getErrorCount());
         this.createGauge(druidDataSource, "druid_execute_count", "Execute count", (datasource) -> (double)druidDataSource.getExecuteCount());
         this.createGauge(druidDataSource, "druid_start_transaction_count", "Start transaction count", (datasource) -> (double)druidDataSource.getStartTransactionCount());
         this.createGauge(druidDataSource, "druid_commit_count", "Commit count", (datasource) -> (double)druidDataSource.getCommitCount());
         this.createGauge(druidDataSource, "druid_rollback_count", "Rollback count", (datasource) -> (double)druidDataSource.getRollbackCount());
         this.createGauge(druidDataSource, "druid_prepared_statement_open_count", "Prepared statement open count", (datasource) -> (double)druidDataSource.getPreparedStatementCount());
         this.createGauge(druidDataSource, "druid_prepared_statement_closed_count", "Prepared statement closed count", (datasource) -> (double)druidDataSource.getClosedPreparedStatementCount());
         this.createGauge(druidDataSource, "druid_ps_cache_access_count", "PS cache access count", (datasource) -> (double)druidDataSource.getCachedPreparedStatementAccessCount());
         this.createGauge(druidDataSource, "druid_ps_cache_hit_count", "PS cache hit count", (datasource) -> (double)druidDataSource.getCachedPreparedStatementHitCount());
         this.createGauge(druidDataSource, "druid_ps_cache_miss_count", "PS cache miss count", (datasource) -> (double)druidDataSource.getCachedPreparedStatementMissCount());
         this.createGauge(druidDataSource, "druid_execute_query_count", "Execute query count", (datasource) -> (double)druidDataSource.getExecuteQueryCount());
         this.createGauge(druidDataSource, "druid_execute_update_count", "Execute update count", (datasource) -> (double)druidDataSource.getExecuteUpdateCount());
         this.createGauge(druidDataSource, "druid_execute_batch_count", "Execute batch count", (datasource) -> (double)druidDataSource.getExecuteBatchCount());
         this.createGauge(druidDataSource, "druid_max_wait", "Max wait", (datasource) -> (double)druidDataSource.getMaxWait());
         this.createGauge(druidDataSource, "druid_max_wait_thread_count", "Max wait thread count", (datasource) -> (double)druidDataSource.getMaxWaitThreadCount());
         this.createGauge(druidDataSource, "druid_login_timeout", "Login timeout", (datasource) -> (double)druidDataSource.getLoginTimeout());
         this.createGauge(druidDataSource, "druid_query_timeout", "Query timeout", (datasource) -> (double)druidDataSource.getQueryTimeout());
         this.createGauge(druidDataSource, "druid_transaction_query_timeout", "Transaction query timeout", (datasource) -> (double)druidDataSource.getTransactionQueryTimeout());
      });
   }

   private void createGauge(DruidDataSource weakRef, String metric, String help, ToDoubleFunction<DruidDataSource> measure) {
      Gauge.builder(metric, weakRef, measure).description(help).tag("pool", weakRef.getName()).register(this.registry);
   }
}
