package com.kuma.boot.apollo.Listener;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Set;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;

public class ApolloConfigChangeListenerRefresher {
   private final ApplicationEventPublisher eventPublisher;
   private final RefreshScope refreshScope;

   public ApolloConfigChangeListenerRefresher(ApplicationEventPublisher eventPublisher, RefreshScope refreshScope) {
      this.eventPublisher = eventPublisher;
      this.refreshScope = refreshScope;
   }

   @ApolloConfigChangeListener
   public void onChange(ConfigChangeEvent changeEvent) {
      Set<String> changedKeys = changeEvent.changedKeys();
      LogUtils.info("Apollo Refreshing properties changedKeys:{}!", new Object[]{changedKeys});
      this.eventPublisher.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
      this.refreshScope.refreshAll();
      LogUtils.info("Apollo Refreshing properties refreshed!", new Object[0]);
   }
}
