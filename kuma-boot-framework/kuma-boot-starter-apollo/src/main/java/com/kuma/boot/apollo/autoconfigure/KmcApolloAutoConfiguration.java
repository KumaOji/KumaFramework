package com.kuma.boot.apollo.autoconfigure;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.kuma.boot.apollo.Listener.ApolloConfigChangeListenerRefresher;
import com.kuma.boot.apollo.Listener.SpringCloudConfigChangeRefresher;
import com.kuma.boot.apollo.namespace.ApolloConfigInterceptRegistry;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Objects;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@EnableApolloConfig({"application.yml"})
@ConditionalOnProperty({"apollo.bootstrap.enabled"})
public class KmcApolloAutoConfiguration implements InitializingBean {
   @ApolloConfig("application.yml")
   private Config config;

   public KmcApolloAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(KmcApolloAutoConfiguration.class, "kuma-boot-starter-apollo", new String[0]);
   }

   @Bean
   public void kmcApolloConfigChangeListener() {
      this.config.addChangeListener(new ConfigChangeListener() {
         {
            Objects.requireNonNull(KmcApolloAutoConfiguration.this);
         }

         public void onChange(ConfigChangeEvent configChangeEvent) {
            System.out.println("Changes for namespace " + configChangeEvent.getNamespace());

            for(String key : configChangeEvent.changedKeys()) {
               ConfigChange change = configChangeEvent.getChange(key);
               System.out.println(String.format("Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType()));
            }

         }
      });
   }

   @Bean
   public ApolloConfigInterceptRegistry apolloConfigInterceptRegistry() {
      return new ApolloConfigInterceptRegistry();
   }

   @Configuration
   @ConditionalOnClass(
      name = {"org.springframework.cloud.bootstrap.BootstrapImportSelectorConfiguration", "org.springframework.cloud.context.refresh.ContextRefresher"}
   )
   public static class SpringCloudConfigChangeRefresherConfiguration {
      public SpringCloudConfigChangeRefresherConfiguration() {
      }

      @Bean
      public SpringCloudConfigChangeRefresher springCloudEnvironmentRefresher(ContextRefresher contextRefresher) {
         return new SpringCloudConfigChangeRefresher(contextRefresher);
      }

      public ApolloConfigChangeListenerRefresher apolloConfigChangeListenerRefresher(ApplicationEventPublisher applicationEventPublisher, RefreshScope refreshScope) {
         return new ApolloConfigChangeListenerRefresher(applicationEventPublisher, refreshScope);
      }
   }
}
