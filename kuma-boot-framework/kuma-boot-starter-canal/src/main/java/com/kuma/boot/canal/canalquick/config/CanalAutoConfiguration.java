package com.kuma.boot.canal.canalquick.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.kuma.boot.canal.canalquick.QuickCanalClientListener;
import com.kuma.boot.canal.canalquick.event.EventHandlerFactory;
import com.kuma.boot.canal.canalquick.parser.DefaultRowDtaCustomParser;
import com.kuma.boot.canal.canalquick.parser.RowDataCustomParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnProperty(
   prefix = "kuma.boot.canal.quick",
   name = {"enabled"},
   havingValue = "true"
)
@EnableConfigurationProperties({CanalConfigProperties.class})
@Import({CanalConnectorConfiguration.class})
public class CanalAutoConfiguration {
   public CanalAutoConfiguration() {
   }

   @Bean
   @ConditionalOnMissingBean({RowDataCustomParser.class})
   public RowDataCustomParser rowDataCustomParser() {
      return new DefaultRowDtaCustomParser();
   }

   @Bean
   public EventHandlerFactory eventHandlerFactory() {
      return new EventHandlerFactory();
   }

   @Bean
   public QuickCanalClientListener quickCanalClientListener(CanalConnector canalConnector, CanalConfigProperties canalConfig, EventHandlerFactory eventHandlerFactory) {
      return new QuickCanalClientListener(canalConnector, canalConfig, eventHandlerFactory);
   }
}
