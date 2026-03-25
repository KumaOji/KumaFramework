package com.kuma.boot.encrypt.encrypt2.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.annotation.Sensitive;
import com.kuma.boot.encrypt.encrypt2.format.SensitiveProcessor;
import java.lang.annotation.Annotation;
import org.springframework.util.Assert;

public class CommonSensitiveHandler implements SensitiveHandler {
   private SensitiveProcessor sensitiveProcessor;

   public CommonSensitiveHandler(SensitiveProcessor sensitiveProcessor) {
      Assert.notNull(sensitiveProcessor, "sensitiveProcessor could not be null");
      this.sensitiveProcessor = sensitiveProcessor;
   }

   public Sensitive acquire(Annotation[] annotations) {
      Sensitive sensitive = null;

      for(int i = 0; i < annotations.length; ++i) {
         if (annotations[i] instanceof Sensitive) {
            sensitive = (Sensitive)annotations[i];
            if (sensitive.required()) {
               return sensitive;
            }
         }
      }

      return null;
   }

   @Override
   public String format(String source, Annotation ann) {
      Sensitive annotation = (Sensitive) ann;
      if (annotation.required()) {
         try {
            return this.sensitiveProcessor.format(source, annotation.type());
         } catch (Exception e) {
            LogUtils.error("sensitive fail: {}, source field value: {}", new Object[]{e.getMessage(), source});
         }
      }

      return source;
   }
}
