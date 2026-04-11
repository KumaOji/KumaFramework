package com.kuma.boot.apollo.Listener;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.kuma.boot.apollo.namespace.NamespaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.refresh.ContextRefresher;

public class SpringCloudConfigChangeRefresher implements ConfigChangeListener, InitializingBean {
   private static final Logger LOGGER = LoggerFactory.getLogger(SpringCloudConfigChangeRefresher.class);
   private final ContextRefresher contextRefresher;

   public SpringCloudConfigChangeRefresher(ContextRefresher contextRefresher) {
      this.contextRefresher = contextRefresher;
   }

   public void afterPropertiesSet() throws Exception {
      for(String namespace : NamespaceManager.get()) {
         Config config = ConfigService.getConfig(namespace);
         config.addChangeListener(this);
      }

   }

   public void onChange(ConfigChangeEvent changeEvent) {
      this.refreshEnvironment(changeEvent);
   }

   private void refreshEnvironment(ConfigChangeEvent changeEvent) {
      LOGGER.info("Refreshing environment! keys changed:{}", changeEvent.changedKeys().toString());
      this.contextRefresher.refresh();
      LOGGER.info("Refreshed environment!");
   }
}
