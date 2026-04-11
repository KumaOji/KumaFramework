package com.kuma.boot.threadpool.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.threadpool.configuration.properties.ThreadPoolProperties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.dromara.dynamictp.common.em.QueueTypeEnum;
import org.dromara.dynamictp.core.executor.DtpExecutor;
import org.dromara.dynamictp.core.support.DynamicTp;
import org.dromara.dynamictp.core.support.ThreadPoolBuilder;
import org.dromara.dynamictp.core.support.ThreadPoolCreator;
import org.dromara.dynamictp.spring.annotation.EnableDynamicTp;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableDynamicTp
@ConditionalOnProperty(
   prefix = "kuma.boot.threadpool",
   name = {"enabled"},
   havingValue = "true"
)
@EnableConfigurationProperties({ThreadPoolProperties.class})
public class DynamicTpAutoConfiguration implements InitializingBean {
   public DynamicTpAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(DynamicTpAutoConfiguration.class, "kuma-boot-starter-threadpool", new String[0]);
   }

   @DynamicTp("commonExecutor")
   @Bean
   public ThreadPoolExecutor commonExecutor() {
      return (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
   }

   @Bean
   public DtpExecutor dtpExecutor1() {
      return ThreadPoolCreator.createDynamicFast("dtpExecutor1");
   }

   @Bean
   public DtpExecutor ioIntensiveExecutor() {
      return ThreadPoolBuilder.newBuilder().threadPoolName("ioIntensiveExecutor").corePoolSize(20).maximumPoolSize(50).queueCapacity(2048).buildDynamic();
   }

   @Bean
   public ThreadPoolExecutor dtpExecutor2() {
      return ThreadPoolBuilder.newBuilder().threadPoolName("dtpExecutor2").corePoolSize(10).maximumPoolSize(15).keepAliveTime(50L).timeUnit(TimeUnit.MILLISECONDS).workQueue(QueueTypeEnum.SYNCHRONOUS_QUEUE.getName(), (Integer)null, false).waitForTasksToCompleteOnShutdown(true).awaitTerminationSeconds(5).buildDynamic();
   }
}
