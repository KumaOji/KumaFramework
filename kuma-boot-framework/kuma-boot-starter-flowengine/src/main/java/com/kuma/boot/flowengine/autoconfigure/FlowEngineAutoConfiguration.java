package com.kuma.boot.flowengine.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.datasource.autoconfigure.TransactionAutoConfiguration;
import com.kuma.boot.data.datasource.utils.Jdbcs;
import com.kuma.boot.flowengine.FlowContext;
import com.kuma.boot.flowengine.FlowEngineSqlScripter;
import com.kuma.boot.flowengine.StandardFlowContext;
import com.kuma.boot.flowengine.autoconfigure.properties.FlowEngineProperties;
import com.kuma.boot.flowengine.state.FlowHistoryTraceRepository;
import com.kuma.boot.flowengine.state.FlowTraceRepository;
import com.kuma.boot.flowengine.state.FlowTraceRepositoryCustomizer;
import com.kuma.boot.flowengine.state.retry.RetryFlowProvider;
import com.kuma.boot.flowengine.state.retry.RetryTransitionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

@AutoConfiguration(
   after = {TransactionAutoConfiguration.class}
)
@EnableConfigurationProperties({FlowEngineProperties.class})
@ConditionalOnClass({JdbcTemplate.class, StandardFlowContext.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.flowengine",
   name = {"enabled"},
   havingValue = "true"
)
@EnableTransactionManagement(
   proxyTargetClass = true
)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, PlatformTransactionManager.class})
public class FlowEngineAutoConfiguration implements InitializingBean {
   public FlowEngineAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(FlowEngineAutoConfiguration.class, "kuma-boot-starter-flowengine", new String[0]);
   }

   @Bean
   public FlowContext standardFlowContext(FlowEngineProperties properties) {
      Assert.hasText(properties.getFlowConfLocation(), "\u6d41\u7a0b\u63cf\u8ff0\u914d\u7f6e\u6587\u4ef6\u8def\u5f84\u4e0d\u80fd\u4e3a\u7a7a");
      return new StandardFlowContext(properties.getFlowConfLocation(), properties.getRetryable());
   }

   @Bean
   public RetryTransitionListener retryTransitionlistener() {
      return new RetryTransitionListener();
   }

   @Bean
   public FlowTraceRepository flowTraceRepository(JdbcTemplate jdbcTemplate, DataSourceProperties dataSourceProperties, ObjectProvider<FlowTraceRepositoryCustomizer> custom) {
      FlowTraceRepositoryCustomizer customizer = (FlowTraceRepositoryCustomizer)custom.getIfUnique();
      return customizer == null ? new FlowTraceRepository(Jdbcs.getDbType(dataSourceProperties.getUrl()).getDb(), jdbcTemplate) : customizer.customize(Jdbcs.getDbType(dataSourceProperties.getUrl()).getDb(), jdbcTemplate);
   }

   @Bean
   public FlowEngineSqlScripter flowEngineSqlScripter() {
      return new FlowEngineSqlScripter();
   }

   @Bean
   public FlowHistoryTraceRepository flowHistoryTraceRepository(JdbcTemplate jdbcTemplate, DataSourceProperties dataSourceProperties) {
      return new FlowHistoryTraceRepository(Jdbcs.getDbType(dataSourceProperties.getUrl()).getDb(), jdbcTemplate);
   }

   @Bean
   @ConditionalOnProperty(
      prefix = "kuma.boot.flowengine",
      name = {"retryable"},
      matchIfMissing = false
   )
   public RetryFlowProvider retryFlowProvider(FlowTraceRepository flowTraceRepository, FlowContext flowContext, ThreadPoolTaskExecutor commonTaskExecutor) {
      return new RetryFlowProvider(flowTraceRepository, flowContext, commonTaskExecutor);
   }
}
