package com.kuma.boot.webagg.aop;

import com.kuma.boot.ddd.model.types.MarkerCheck;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({MarkerCheck.class})
public class RecordValidationAutoConfiguration {
   public RecordValidationAutoConfiguration() {
   }

   @Bean
   static RecordValidationAutoProxyCreator recordValidationAutoProxyCreator() {
      return new RecordValidationAutoProxyCreator();
   }
}
