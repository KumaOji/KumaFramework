package com.taotao.boot.metrics.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcConnectionStat;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.Collections;
import java.util.Map;

public class DruidMetrics implements MeterBinder {
   public static final String DRUID_METRIC_NAME_PREFIX = "druid";
   private static final String METRIC_CATEGORY = "name";
   private static final String METRIC_NAME_CONNECT_MAX_TIME = "druid.connections.connect.max.time";
   private static final String METRIC_NAME_ALIVE_MAX_TIME = "druid.connections.alive.max.time";
   private static final String METRIC_NAME_ALIVE_MIN_TIME = "druid.connections.alive.min.time";
   private static final String METRIC_NAME_CONNECT_COUNT = "druid.connections.connect.count";
   private static final String METRIC_NAME_ACTIVE_COUNT = "druid.connections.active.count";
   private static final String METRIC_NAME_CLOSE_COUNT = "druid.connections.close.count";
   private static final String METRIC_NAME_ERROR_COUNT = "druid.connections.error.count";
   private static final String METRIC_NAME_CONNECT_ERROR_COUNT = "druid.connections.connect.error.count";
   private static final String METRIC_NAME_COMMIT_COUNT = "druid.connections.commit.count";
   private static final String METRIC_NAME_ROLLBACK_COUNT = "druid.connections.rollback.count";
   private final Map<String, DruidDataSource> druidDataSourceMap;
   private final Iterable<Tag> tags;

   public DruidMetrics(Map<String, DruidDataSource> druidDataSourceMap) {
      this(druidDataSourceMap, Collections.emptyList());
   }

   public DruidMetrics(Map<String, DruidDataSource> druidDataSourceMap, Iterable<Tag> tags) {
      this.druidDataSourceMap = druidDataSourceMap;
      this.tags = tags;
   }

   public void bindTo(MeterRegistry meterRegistry) {
      this.druidDataSourceMap.forEach((name, dataSource) -> {
         JdbcDataSourceStat dsStats = dataSource.getDataSourceStat();
         JdbcConnectionStat connectionStat = dsStats.getConnectionStat();
         Gauge.builder("druid.connections.connect.max.time", connectionStat, JdbcConnectionStat::getConnectMillisMax).description("Connection connect max time").tags(this.tags).tag("name", name).baseUnit("ms").register(meterRegistry);
         Gauge.builder("druid.connections.alive.max.time", connectionStat, JdbcConnectionStat::getAliveMillisMax).description("Connection alive max time").tags(this.tags).tag("name", name).baseUnit("ms").register(meterRegistry);
         Gauge.builder("druid.connections.alive.min.time", connectionStat, JdbcConnectionStat::getAliveMillisMin).description("Connection alive min time").tags(this.tags).tag("name", name).baseUnit("ms").register(meterRegistry);
         Gauge.builder("druid.connections.active.count", connectionStat, JdbcConnectionStat::getActiveCount).description("Connection active count").tags(this.tags).tag("name", name).register(meterRegistry);
         Gauge.builder("druid.connections.connect.count", connectionStat, JdbcConnectionStat::getConnectCount).description("Connection connect count").tags(this.tags).tag("name", name).register(meterRegistry);
         Gauge.builder("druid.connections.close.count", connectionStat, JdbcConnectionStat::getCloseCount).description("Connection close count").tags(this.tags).tag("name", name).register(meterRegistry);
         Gauge.builder("druid.connections.error.count", connectionStat, JdbcConnectionStat::getErrorCount).description("Connection error count").tags(this.tags).tag("name", name).register(meterRegistry);
         Gauge.builder("druid.connections.connect.error.count", connectionStat, JdbcConnectionStat::getConnectErrorCount).description("Connection connect error count").tags(this.tags).tag("name", name).register(meterRegistry);
         Gauge.builder("druid.connections.commit.count", connectionStat, JdbcConnectionStat::getCommitCount).description("Connecting commit count").tags(this.tags).tag("name", name).register(meterRegistry);
         Gauge.builder("druid.connections.rollback.count", connectionStat, JdbcConnectionStat::getRollbackCount).description("Connection rollback count").tags(this.tags).tag("name", name).register(meterRegistry);
      });
   }
}
