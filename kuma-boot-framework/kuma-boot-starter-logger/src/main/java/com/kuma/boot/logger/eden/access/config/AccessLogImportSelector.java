package com.kuma.boot.logger.eden.access.config;

import com.kuma.boot.logger.eden.EnableAccessLog;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

public class AccessLogImportSelector extends AdviceModeImportSelector<EnableAccessLog> {
   public AccessLogImportSelector() {
   }

   protected String[] selectImports(AdviceMode adviceMode) {
      switch (adviceMode) {
         case PROXY -> {
            return new String[]{AutoProxyRegistrar.class.getName(), AccessLogConfiguration.class.getName()};
         }
         case ASPECTJ -> {
            return new String[]{AccessLogConfiguration.class.getName()};
         }
         default -> {
            return new String[0];
         }
      }
   }
}
