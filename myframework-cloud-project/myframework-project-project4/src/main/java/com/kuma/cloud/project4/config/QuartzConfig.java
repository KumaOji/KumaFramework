package com.kuma.cloud.project4.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz 调度器配置
 * 优化 Quartz 性能配置
 *
 * 配置优先级说明：
 * 1. Java Bean 配置（本类中的配置）优先级最高，会覆盖配置文件中的设置
 * 2. application.yml 中的配置作为默认值和参考，但最终以 Java 代码为准
 * 3. 如果修改配置，建议同时更新 Java 代码和配置文件，保持一致
 *
 * 注意：factory.setStartupDelay()、factory.setOverwriteExistingJobs() 等方法
 * 会覆盖 application.yml 中对应的配置（spring.quartz.startup-delay 等）
 */
@Configuration
@Lazy
@ConditionalOnProperty(name = "spring.quartz.job-store-type", havingValue = "jdbc", matchIfMissing = false)
public class QuartzConfig {

    /**
     * 配置 Quartz SchedulerFactoryBean
     * 优化线程池和性能参数
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);

        Properties properties = new Properties();

        // 调度器配置
        properties.put("org.quartz.scheduler.instanceName", "BlogScheduler");
        properties.put("org.quartz.scheduler.instanceId", "AUTO");

        // 线程池配置（性能优化关键）
        properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.put("org.quartz.threadPool.threadCount", "10"); // 线程数，根据服务器CPU核心数调整
        properties.put("org.quartz.threadPool.threadPriority", "5"); // 线程优先级
        properties.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        // JobStore 配置（使用 JDBC 存储）
        properties.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.put("org.quartz.jobStore.useProperties", "false");
        properties.put("org.quartz.jobStore.dataSource", "quartzDataSource");
        properties.put("org.quartz.jobStore.isClustered", "false"); // 单机模式，集群模式设为 true
        properties.put("org.quartz.jobStore.clusterCheckinInterval", "20000"); // 集群检查间隔（毫秒）

        // 性能优化配置
        properties.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1"); // 每次处理的最大失火数
        properties.put("org.quartz.jobStore.misfireThreshold", "60000"); // 失火阈值（毫秒）
        properties.put("org.quartz.jobStore.txIsolationLevelSerializable", "false"); // 不使用串行化隔离级别，提高性能

        // 数据源配置
        properties.put("org.quartz.dataSource.quartzDataSource.driver", "com.mysql.cj.jdbc.Driver");
        properties.put("org.quartz.dataSource.quartzDataSource.URL", "${spring.datasource.url}");
        properties.put("org.quartz.dataSource.quartzDataSource.user", "${spring.datasource.username}");
        properties.put("org.quartz.dataSource.quartzDataSource.password", "${spring.datasource.password}");
        properties.put("org.quartz.dataSource.quartzDataSource.maxConnections", "10"); // 连接池大小
        properties.put("org.quartz.dataSource.quartzDataSource.validationQuery", "SELECT 1");

        factory.setQuartzProperties(properties);
        factory.setSchedulerName("BlogScheduler");
        factory.setStartupDelay(0); // 启动延迟
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setOverwriteExistingJobs(true); // 覆盖已存在的任务
        factory.setWaitForJobsToCompleteOnShutdown(false); // 关闭时不等待任务完成

        return factory;
    }

    /**
     * 内存模式配置（当前使用）
     * 优化线程池配置
     *
     * 配置优先级：Java Bean 配置 > application.yml 配置
     * - factory.setStartupDelay(5) 会覆盖 spring.quartz.startup-delay
     * - properties.put() 设置的 Quartz 原生属性会覆盖 spring.quartz.properties.*
     */
    @Bean
    @ConditionalOnProperty(name = "spring.quartz.job-store-type", havingValue = "memory", matchIfMissing = true)
    public SchedulerFactoryBean memorySchedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        Properties properties = new Properties();

        // 调度器配置
        properties.put("org.quartz.scheduler.instanceName", "BlogScheduler");
        properties.put("org.quartz.scheduler.instanceId", "AUTO");

        // 线程池配置（性能优化关键）
        properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.put("org.quartz.threadPool.threadCount", "10"); // 线程数，根据服务器CPU核心数调整
        properties.put("org.quartz.threadPool.threadPriority", "5"); // 线程优先级
        properties.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        // JobStore 配置（内存模式）
        properties.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");

        // 性能优化配置
        properties.put("org.quartz.jobStore.misfireThreshold", "60000"); // 失火阈值（毫秒）

        factory.setQuartzProperties(properties);
        factory.setSchedulerName("BlogScheduler");
        factory.setStartupDelay(5); // 启动延迟5秒，让应用先完成初始化，优化启动速度
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setOverwriteExistingJobs(true); // 覆盖已存在的任务
        factory.setWaitForJobsToCompleteOnShutdown(false); // 关闭时不等待任务完成

        return factory;
    }
}

