package com.kuma.boot.canal.canalquick.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.google.common.collect.Lists;
import com.kuma.boot.canal.canalquick.event.TableEvent;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@AutoConfiguration
@ConditionalOnClass({CanalConnectors.class})
@ConditionalOnProperty(prefix = "kuma.boot.canal.quick", name = "enabled", havingValue = "true")
public class CanalConnectorConfiguration implements ApplicationContextAware {
   private final CanalConfigProperties canalConfigProperties;
   protected ApplicationContext applicationContext;

   public CanalConnectorConfiguration(CanalConfigProperties canalConfigProperties) {
      this.canalConfigProperties = canalConfigProperties;
   }

   @Bean
   public CanalConnector initConnector() {
      CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(this.canalConfigProperties.getHost(), this.canalConfigProperties.getPort()), this.canalConfigProperties.getDestination(), this.canalConfigProperties.getUserName(), this.canalConfigProperties.getPassword());
      connector.connect();
      connector.subscribe(String.join(",", this.mergeFilters(this.getConfigFilters(), this.getTableEventFilters())));
      connector.rollback();
      return connector;
   }

   private List<String> mergeFilters(List<String> configFilters, List<String> annotationFilters) {
      List<String> result = new ArrayList();
      if (!CollectionUtils.isEmpty(configFilters)) {
         result.addAll(configFilters);
      }

      if (!CollectionUtils.isEmpty(annotationFilters)) {
         result.addAll(annotationFilters);
      }

      Assert.notEmpty(result, "\u76ee\u6807\u8fc7\u6ee4\u8868\u4e0d\u80fd\u4e3a\u7a7a");
      return result.stream().distinct().toList();
   }

   private List<String> getConfigFilters() {
      return (List<String>)(StrUtil.isBlank(this.canalConfigProperties.getFilter()) ? Lists.newLinkedList() : Lists.newArrayList(this.canalConfigProperties.getFilter().split(",")));
   }

   private List<String> getTableEventFilters() {
      List<TableEvent> tableEventList = new ArrayList();
      Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(TableEvent.class);

      for(Object bean : beansWithAnnotation.values()) {
         TableEvent tableEvent = (TableEvent)bean.getClass().getAnnotation(TableEvent.class);
         if (tableEvent != null) {
            tableEventList.add(tableEvent);
         }
      }

      if (CollectionUtils.isEmpty(tableEventList)) {
         return null;
      } else {
         return tableEventList.stream().map((it) -> {
            String var10000 = it.schemaName();
            return var10000 + "." + it.tableName();
         }).distinct().toList();
      }
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }
}
