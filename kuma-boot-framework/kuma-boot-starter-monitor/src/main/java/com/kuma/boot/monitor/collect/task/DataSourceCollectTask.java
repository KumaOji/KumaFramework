package com.kuma.boot.monitor.collect.task;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.reflect.ClassUtils;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.monitor.annotation.FieldReport;
import com.kuma.boot.monitor.autoconfigure.properties.CollectTaskProperties;
import com.kuma.boot.monitor.collect.AbstractCollectTask;
import com.kuma.boot.monitor.collect.CollectInfo;
import java.lang.reflect.Field;
import javax.sql.DataSource;

public class DataSourceCollectTask extends AbstractCollectTask {
   private static final String TASK_NAME = "kmc.monitor.collect.datasource";
   private final CollectTaskProperties properties;
   private final boolean classExist;

   public DataSourceCollectTask(CollectTaskProperties properties) {
      this.properties = properties;
      this.classExist = ClassUtils.isExist("javax.sql.DataSource");
   }

   public int getTimeSpan() {
      return this.properties.getDatasourcTimeSpan();
   }

   public String getDesc() {
      return this.getClass().getName();
   }

   public String getName() {
      return "kmc.monitor.collect.datasource";
   }

   public boolean getEnabled() {
      return this.properties.isDatasourceEnabled() && this.classExist;
   }

   protected CollectInfo getData() {
      try {
         DataSourceInfo info = new DataSourceInfo();
         String[] names = ContextUtils.getApplicationContext().getBeanNamesForType(DataSource.class);
         int index = 0;

         for(String name : names) {
            if (!"tracingDataSource".equals(name)) {
               DataSource dataSource = (DataSource)ContextUtils.getApplicationContext().getBean(name, DataSource.class);
               Class<?> druidCls = ReflectionUtils.tryClassForName("com.alibaba.druid.pool.DruidDataSource");
               if (druidCls != null && druidCls.isAssignableFrom(dataSource.getClass())) {
                  Class var10000 = info.getClass();
                  int var10001 = index++;
                  Field field = ReflectionUtils.findField(var10000, "druid" + var10001);
                  if (field != null) {
                     DruidDataSourceInfo druid = new DruidDataSourceInfo();
                     druid.active = (Integer)ReflectionUtils.callMethod(dataSource, "getActiveCount", (Object[])null);
                     druid.connect = (Long)ReflectionUtils.callMethod(dataSource, "getConnectCount", (Object[])null);
                     druid.poolingCount = (Integer)ReflectionUtils.callMethod(dataSource, "getPoolingCount", (Object[])null);
                     druid.lockQueueLength = (Integer)ReflectionUtils.callMethod(dataSource, "getLockQueueLength", (Object[])null);
                     druid.waitThreadCount = (Integer)ReflectionUtils.callMethod(dataSource, "getWaitThreadCount", (Object[])null);
                     druid.initialSize = (Integer)ReflectionUtils.callMethod(dataSource, "getInitialSize", (Object[])null);
                     druid.maxActive = (Integer)ReflectionUtils.callMethod(dataSource, "getMaxActive", (Object[])null);
                     druid.minIdle = (Integer)ReflectionUtils.callMethod(dataSource, "getMinIdle", (Object[])null);
                     druid.connectErrorCount = (Long)ReflectionUtils.callMethod(dataSource, "getConnectErrorCount", (Object[])null);
                     druid.createTimeSpan = (Long)ReflectionUtils.callMethod(dataSource, "getCreateTimespanMillis", (Object[])null);
                     druid.closeCount = (Long)ReflectionUtils.callMethod(dataSource, "getCloseCount", (Object[])null);
                     druid.createCount = (Long)ReflectionUtils.callMethod(dataSource, "getCreateCount", (Object[])null);
                     druid.destroyCount = (Long)ReflectionUtils.callMethod(dataSource, "getDestroyCount", (Object[])null);
                     druid.isSharePreparedStatements = ReflectionUtils.callMethod(dataSource, "isSharePreparedStatements", (Object[])null).toString();
                     druid.isRemoveAbandoned = ReflectionUtils.callMethod(dataSource, "isRemoveAbandoned", (Object[])null).toString();
                     druid.removeAbandonedTimeout = (Integer)ReflectionUtils.callMethod(dataSource, "getRemoveAbandonedTimeout", (Object[])null);
                     druid.removeAbandonedCount = (Long)ReflectionUtils.callMethod(dataSource, "getRemoveAbandonedCount", (Object[])null);
                     druid.rollbackCount = (Long)ReflectionUtils.callMethod(dataSource, "getRollbackCount", (Object[])null);
                     druid.commitCount = (Long)ReflectionUtils.callMethod(dataSource, "getCommitCount", (Object[])null);
                     druid.startTransactionCount = (Long)ReflectionUtils.callMethod(dataSource, "getStartTransactionCount", (Object[])null);
                     field.setAccessible(true);
                     ReflectionUtils.setFieldValue(field, info, druid);
                  }
               }

               Class<?> hikariCls = ReflectionUtils.tryClassForName("com.zaxxer.hikari.HikariDataSource");
               if (hikariCls != null && hikariCls.isAssignableFrom(dataSource.getClass())) {
                  Class var18 = info.getClass();
                  int var19 = index++;
                  Field field = ReflectionUtils.findField(var18, "hikari" + var19);
                  if (field != null) {
                     HikariDataSourceInfo hikari = new HikariDataSourceInfo();
                     Object hikariPoolMXBean = ReflectionUtils.callMethod(dataSource, "getHikariPoolMXBean", (Object[])null);
                     Object hikariConfigMXBean = ReflectionUtils.callMethod(dataSource, "getHikariConfigMXBean", (Object[])null);
                     if (ObjectUtils.isNotNull(hikariPoolMXBean)) {
                        hikari.activeConnections = (Integer)ReflectionUtils.callMethod(hikariPoolMXBean, "getActiveConnections", (Object[])null);
                        hikari.idleConnections = (Integer)ReflectionUtils.callMethod(hikariPoolMXBean, "getIdleConnections", (Object[])null);
                        hikari.threadsAwaitingConnection = (Integer)ReflectionUtils.callMethod(hikariPoolMXBean, "getThreadsAwaitingConnection", (Object[])null);
                        hikari.totalConnections = (Integer)ReflectionUtils.callMethod(hikariPoolMXBean, "getTotalConnections", (Object[])null);
                     }

                     hikari.catalog = (String)ReflectionUtils.callMethod(hikariConfigMXBean, "getCatalog", (Object[])null);
                     hikari.connectionTimeout = (Long)ReflectionUtils.callMethod(hikariConfigMXBean, "getConnectionTimeout", (Object[])null);
                     hikari.idleTimeout = (Long)ReflectionUtils.callMethod(hikariConfigMXBean, "getIdleTimeout", (Object[])null);
                     hikari.maxLifetime = (Long)ReflectionUtils.callMethod(hikariConfigMXBean, "getMaxLifetime", (Object[])null);
                     hikari.validationTimeout = (Long)ReflectionUtils.callMethod(hikariConfigMXBean, "getValidationTimeout", (Object[])null);
                     hikari.leakDetectionThreshold = (Long)ReflectionUtils.callMethod(hikariConfigMXBean, "getLeakDetectionThreshold", (Object[])null);
                     hikari.maximumPoolSize = (Integer)ReflectionUtils.callMethod(hikariConfigMXBean, "getMaximumPoolSize", (Object[])null);
                     hikari.minimumIdle = (Integer)ReflectionUtils.callMethod(hikariConfigMXBean, "getMinimumIdle", (Object[])null);
                     hikari.poolName = (String)ReflectionUtils.callMethod(hikariConfigMXBean, "getPoolName", (Object[])null);
                     field.setAccessible(true);
                     ReflectionUtils.setFieldValue(field, info, hikari);
                  }
               }
            }
         }

         return info;
      } catch (Exception e) {
         if (LogUtils.isErrorEnabled()) {
            LogUtils.error(e);
         }

         return null;
      }
   }

   private static class HikariDataSourceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.active.connections",
         desc = "hikari\u5f53\u524d\u6d3b\u52a8\u8fde\u63a5\u7684\u6570\u91cf\u3002"
      )
      private Integer activeConnections = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.idle.connections",
         desc = "hikari\u5f53\u524d\u7a7a\u95f2\u7684\u8fde\u63a5\u6570\u3002"
      )
      private Integer idleConnections = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.threads.awaiting.connection",
         desc = "hikari\u7b49\u5f85\u8fde\u63a5\u7684\u7ebf\u7a0b\u6570"
      )
      private Integer threadsAwaitingConnection = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.total.connections",
         desc = "hikari\u8fde\u63a5\u6c60\u4e2d\u7684\u8fde\u63a5\u603b\u6570"
      )
      private Integer totalConnections = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.catalog",
         desc = "hikari\u8bbe\u7f6e\u7684\u9ed8\u8ba4\u76ee\u5f55\u540d\u79f0"
      )
      private String catalog = "";
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.connection.timeout",
         desc = "hikari\u7b49\u5f85\u6c60\u4e2d\u8fde\u63a5\u7684\u6700\u5927\u6beb\u79d2\u6570"
      )
      private Long connectionTimeout = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.idle.timeout",
         desc = "hikari\u5141\u8bb8\u8fde\u63a5\u505c\u7559\u7684\u6700\u957f\u65f6\u95f4\uff08\u4ee5\u6beb\u79d2\u4e3a\u5355\u4f4d)"
      )
      private Long idleTimeout = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.max.lifetime",
         desc = "hikari\u8fde\u63a5\u6c60\u4e2d\u8fde\u63a5\u7684\u6700\u957f\u751f\u547d\u5468\u671f"
      )
      private Long maxLifetime = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.validation.timeout",
         desc = "hikari\u7b49\u5f85\u8fde\u63a5\u88ab\u9a8c\u8bc1\u4e3a\u7684\u6700\u5927\u6beb\u79d2\u6570"
      )
      private Long validationTimeout = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.leak.detection.threshold",
         desc = "hikari\u6d88\u606f\u53d1\u9001\u4e4b\u524d\u8fde\u63a5\u53ef\u4ee5\u79bb\u5f00\u6c60\u7684\u65f6\u95f4\u91cf.\u8bb0\u5f55\u8868\u660e\u53ef\u80fd\u7684\u8fde\u63a5\u6cc4\u6f0f"
      )
      private Long leakDetectionThreshold = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.maximum.pool.size",
         desc = "hikari HikariCP \u5c06\u4fdd\u7559\u5728\u6c60\u4e2d\u7684\u6700\u5927\u8fde\u63a5\u6570\uff0c\u5305\u62ec\u7a7a\u95f2\u548c\u4f7f\u7528\u4e2d\u7684\u8fde\u63a5\u3002"
      )
      private Integer maximumPoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.minimum.idle",
         desc = "hikari\u5c1d\u8bd5\u5728\u6c60\u4e2d\u7ef4\u62a4\u7684\u6700\u5c0f\u7a7a\u95f2\u8fde\u63a5\u6570\u3002\u5305\u62ec\u7a7a\u95f2\u548c\u4f7f\u7528\u4e2d\u7684\u8fde\u63a5"
      )
      private Integer minimumIdle = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.name",
         desc = "hikari\u7ebf\u7a0b\u6c60\u540d\u79f0"
      )
      private String poolName = "";

      private HikariDataSourceInfo() {
      }
   }

   private static class DataSourceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid0.info",
         desc = "druid0\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid1.info",
         desc = "druid1\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid1;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid2.info",
         desc = "druid2\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid2;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid3.info",
         desc = "druid3\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid3;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid4.info",
         desc = "druid4\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid4;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid5.info",
         desc = "druid5\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid5;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid6.info",
         desc = "druid6\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid6;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid7.info",
         desc = "druid7\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid7;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid8.info",
         desc = "druid8\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid8;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid9.info",
         desc = "druid9\u4fe1\u606f"
      )
      private DruidDataSourceInfo druid9;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari0\u4fe1\u606f"
      )
      private HikariDataSourceInfo hikari0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari1\u4fe1\u606f"
      )
      private HikariDataSourceInfo hikari1;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari2\u4fe1\u606f"
      )
      private HikariDataSourceInfo hikari2;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari3\u4fe1\u606f"
      )
      private HikariDataSourceInfo hikari3;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari4\u4fe1\u606f"
      )
      private HikariDataSourceInfo hikari4;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari5\u4fe1\u606f"
      )
      private HikariDataSourceInfo hikari5;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari6\u4fe1\u606f"
      )
      private HikariDataSourceInfo hikari6;

      private DataSourceInfo() {
      }
   }

   private static class DruidDataSourceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.startTransaction.count",
         desc = "druid sql \u5f00\u542f\u4e8b\u52a1\u6b21\u6570"
      )
      private Long startTransactionCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.commit.count",
         desc = "druid sql commit\u6b21\u6570"
      )
      private Long commitCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.rollback.count",
         desc = "druid sql\u56de\u6eda\u6b21\u6570"
      )
      private Long rollbackCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.removeAbandoned.count",
         desc = "druid \u8fde\u63a5\u8d85\u65f6\u56de\u6536\u6b21\u6570"
      )
      private Long removeAbandonedCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.removeAbandoned.timeout",
         desc = "druid \u8fde\u63a5\u8d85\u65f6\u56de\u6536\u5468\u671f\uff08\u79d2\uff09"
      )
      private Integer removeAbandonedTimeout = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.isRemoveAbandoned",
         desc = "druid \u662f\u5426\u5f00\u542f\u8fde\u63a5\u8d85\u65f6\u56de\u6536"
      )
      private String isRemoveAbandoned = "";
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.isSharePreparedStatements",
         desc = "druid preparedStatement\u662f\u5426\u7f13\u5b58"
      )
      private String isSharePreparedStatements = "";
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.destroy.count",
         desc = "druid\u9500\u6bc1\u8fde\u63a5\u6b21\u6570"
      )
      private Long destroyCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.create.count",
         desc = "druid\u521b\u5efa\u8fde\u63a5\u6b21\u6570"
      )
      private Long createCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.close.count",
         desc = "druid\u5173\u95ed\u8fde\u63a5\u6b21\u6570"
      )
      private Long closeCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.create.timeSpan",
         desc = "druid\u7269\u7406\u8fde\u63a5\u521b\u5efa\u8017\u65f6(\u6beb\u79d2)"
      )
      private Long createTimeSpan = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.connect.errorCount",
         desc = "druid\u7269\u7406\u8fde\u63a5\u9519\u8bef\u6570"
      )
      private Long connectErrorCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.idle.min",
         desc = "druid\u8fde\u63a5\u6c60\u6700\u5c0f\u503c"
      )
      private Integer minIdle = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.active.max",
         desc = "druid\u8fde\u63a5\u6c60\u6700\u5927\u503c"
      )
      private Integer maxActive = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.initial.size",
         desc = "druid\u8fde\u63a5\u6c60\u521d\u59cb\u5316\u957f\u5ea6"
      )
      private Integer initialSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.waitThread.count",
         desc = "druid\u83b7\u53d6\u8fde\u63a5\u65f6\u7b49\u5f85\u7ebf\u7a0b\u6570"
      )
      private Integer waitThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.lockQueue.length",
         desc = "druid\u83b7\u53d6\u8fde\u63a5\u7b49\u5f85\u961f\u5217\u957f\u5ea6"
      )
      private Integer lockQueueLength = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.active",
         desc = "druid\u6b63\u5728\u6253\u5f00\u7684\u8fde\u63a5\u6570"
      )
      private Integer active = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.connect",
         desc = "druid\u7533\u8bf7\u8fde\u63a5\u7684\u6b21\u6570"
      )
      private Long connect = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.poolingCount",
         desc = "druid\u8fde\u63a5\u6c60\u7a7a\u95f2\u8fde\u63a5\u6570"
      )
      private Integer poolingCount = 0;

      private DruidDataSourceInfo() {
      }
   }
}
