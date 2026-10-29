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
         desc = "hikari当前活动连接的数量。"
      )
      private Integer activeConnections = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.idle.connections",
         desc = "hikari当前空闲的连接数。"
      )
      private Integer idleConnections = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.threads.awaiting.connection",
         desc = "hikari等待连接的线程数"
      )
      private Integer threadsAwaitingConnection = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.total.connections",
         desc = "hikari连接池中的连接总数"
      )
      private Integer totalConnections = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.catalog",
         desc = "hikari设置的默认目录名称"
      )
      private String catalog = "";
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.connection.timeout",
         desc = "hikari等待池中连接的最大毫秒数"
      )
      private Long connectionTimeout = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.idle.timeout",
         desc = "hikari允许连接停留的最长时间（以毫秒为单位)"
      )
      private Long idleTimeout = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.max.lifetime",
         desc = "hikari连接池中连接的最长生命周期"
      )
      private Long maxLifetime = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.validation.timeout",
         desc = "hikari等待连接被验证为的最大毫秒数"
      )
      private Long validationTimeout = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.leak.detection.threshold",
         desc = "hikari消息发送之前连接可以离开池的时间量.记录表明可能的连接泄漏"
      )
      private Long leakDetectionThreshold = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.maximum.pool.size",
         desc = "hikari HikariCP 将保留在池中的最大连接数，包括空闲和使用中的连接。"
      )
      private Integer maximumPoolSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.minimum.idle",
         desc = "hikari尝试在池中维护的最小空闲连接数。包括空闲和使用中的连接"
      )
      private Integer minimumIdle = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.pool.name",
         desc = "hikari线程池名称"
      )
      private String poolName = "";

      private HikariDataSourceInfo() {
      }
   }

   private static class DataSourceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid0.info",
         desc = "druid0信息"
      )
      private DruidDataSourceInfo druid0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid1.info",
         desc = "druid1信息"
      )
      private DruidDataSourceInfo druid1;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid2.info",
         desc = "druid2信息"
      )
      private DruidDataSourceInfo druid2;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid3.info",
         desc = "druid3信息"
      )
      private DruidDataSourceInfo druid3;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid4.info",
         desc = "druid4信息"
      )
      private DruidDataSourceInfo druid4;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid5.info",
         desc = "druid5信息"
      )
      private DruidDataSourceInfo druid5;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid6.info",
         desc = "druid6信息"
      )
      private DruidDataSourceInfo druid6;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid7.info",
         desc = "druid7信息"
      )
      private DruidDataSourceInfo druid7;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid8.info",
         desc = "druid8信息"
      )
      private DruidDataSourceInfo druid8;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid9.info",
         desc = "druid9信息"
      )
      private DruidDataSourceInfo druid9;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari0信息"
      )
      private HikariDataSourceInfo hikari0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari1信息"
      )
      private HikariDataSourceInfo hikari1;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari2信息"
      )
      private HikariDataSourceInfo hikari2;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari3信息"
      )
      private HikariDataSourceInfo hikari3;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari4信息"
      )
      private HikariDataSourceInfo hikari4;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari5信息"
      )
      private HikariDataSourceInfo hikari5;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.hikari.info",
         desc = "hikari6信息"
      )
      private HikariDataSourceInfo hikari6;

      private DataSourceInfo() {
      }
   }

   private static class DruidDataSourceInfo implements CollectInfo {
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.startTransaction.count",
         desc = "druid sql 开启事务次数"
      )
      private Long startTransactionCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.commit.count",
         desc = "druid sql commit次数"
      )
      private Long commitCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.rollback.count",
         desc = "druid sql回滚次数"
      )
      private Long rollbackCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.removeAbandoned.count",
         desc = "druid 连接超时回收次数"
      )
      private Long removeAbandonedCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.removeAbandoned.timeout",
         desc = "druid 连接超时回收周期（秒）"
      )
      private Integer removeAbandonedTimeout = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.isRemoveAbandoned",
         desc = "druid 是否开启连接超时回收"
      )
      private String isRemoveAbandoned = "";
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.isSharePreparedStatements",
         desc = "druid preparedStatement是否缓存"
      )
      private String isSharePreparedStatements = "";
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.destroy.count",
         desc = "druid销毁连接次数"
      )
      private Long destroyCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.create.count",
         desc = "druid创建连接次数"
      )
      private Long createCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.close.count",
         desc = "druid关闭连接次数"
      )
      private Long closeCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.create.timeSpan",
         desc = "druid物理连接创建耗时(毫秒)"
      )
      private Long createTimeSpan = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.connect.errorCount",
         desc = "druid物理连接错误数"
      )
      private Long connectErrorCount = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.idle.min",
         desc = "druid连接池最小值"
      )
      private Integer minIdle = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.active.max",
         desc = "druid连接池最大值"
      )
      private Integer maxActive = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.initial.size",
         desc = "druid连接池初始化长度"
      )
      private Integer initialSize = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.waitThread.count",
         desc = "druid获取连接时等待线程数"
      )
      private Integer waitThreadCount = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.lockQueue.length",
         desc = "druid获取连接等待队列长度"
      )
      private Integer lockQueueLength = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.active",
         desc = "druid正在打开的连接数"
      )
      private Integer active = 0;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.connect",
         desc = "druid申请连接的次数"
      )
      private Long connect = 0L;
      @FieldReport(
         name = "kmc.monitor.collect.datasource.druid.pool.poolingCount",
         desc = "druid连接池空闲连接数"
      )
      private Integer poolingCount = 0;

      private DruidDataSourceInfo() {
      }
   }
}
