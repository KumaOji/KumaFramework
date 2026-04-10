package com.kuma.boot.data.p6spy.logger;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import java.util.Objects;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaLogger extends FormattedLogger {
   private final KafkaTemplate<String, String> kafkaTemplate;
   private final String applicationName;

   public KafkaLogger() throws ClassNotFoundException {
      try {
         KafkaTemplate<String, String> kafkaTemplate = (KafkaTemplate)ContextUtils.getBean(KafkaTemplate.class);
         String applicationName = PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY);
         this.kafkaTemplate = kafkaTemplate;
         this.applicationName = applicationName;
      } catch (Exception e) {
         throw new ClassNotFoundException("KafkaTemplate\u4e0d\u5b58\u5728\uff0c\u8bf7\u6dfb\u52a0org.springframework.kafka:spring-kafka", e);
      }
   }

   public void logException(Exception e) {
      if (Objects.nonNull(this.kafkaTemplate)) {
         this.kafkaTemplate.send("sys-sql", e.getMessage());
      }

   }

   public void logText(String text) {
      if (Objects.nonNull(this.kafkaTemplate)) {
         this.kafkaTemplate.send("sys-sql", text);
      }

   }

   public boolean isCategoryEnabled(Category category) {
      return true;
   }
}
