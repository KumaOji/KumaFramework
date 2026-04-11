package com.kuma.boot.translation.translationjackson.config;

import com.kuma.boot.translation.translationjackson.core.DefaultDictTranslation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.json.JsonMapper;

@AutoConfiguration
public class TranslationConfig implements InitializingBean {
   @Autowired
   private JsonMapper jsonMapper;

   public TranslationConfig() {
   }

   public void afterPropertiesSet() {
   }

   @Bean
   @ConditionalOnMissingBean
   public DefaultDictTranslation defaultDictTranslation() {
      return new DefaultDictTranslation();
   }
}
