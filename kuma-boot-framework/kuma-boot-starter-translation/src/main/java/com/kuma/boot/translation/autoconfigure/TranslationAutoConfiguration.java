package com.kuma.boot.translation.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.translation.annotation.TranslationType;
import com.kuma.boot.translation.autoconfigure.properties.TranslationProperties;
import com.kuma.boot.translation.core.TranslationInterface;
import com.kuma.boot.translation.core.handler.TranslationHandler;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tools.jackson.databind.json.JsonMapper;

@EnableConfigurationProperties({TranslationProperties.class})
@AutoConfiguration
public class TranslationAutoConfiguration implements InitializingBean {
   private final List<TranslationInterface<?>> list;
   private final JsonMapper jsonMapper;

   public TranslationAutoConfiguration(List<TranslationInterface<?>> list, JsonMapper jsonMapper) {
      this.list = list;
      this.jsonMapper = jsonMapper;
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(TranslationAutoConfiguration.class, "kuma-boot-starter-translation", new String[0]);
   }

   @PostConstruct
   public void init() {
      Map<String, TranslationInterface<?>> map = new HashMap(this.list.size());

      for(TranslationInterface<?> trans : this.list) {
         if (trans.getClass().isAnnotationPresent(TranslationType.class)) {
            TranslationType annotation = (TranslationType)trans.getClass().getAnnotation(TranslationType.class);
            map.put(annotation.type(), trans);
         } else {
            LogUtils.warn(trans.getClass().getName() + " \u7ffb\u8bd1\u5b9e\u73b0\u7c7b\u672a\u6807\u6ce8 TranslationType \u6ce8\u89e3!", new Object[0]);
         }
      }

      TranslationHandler.TRANSLATION_MAPPER.putAll(map);
   }
}
