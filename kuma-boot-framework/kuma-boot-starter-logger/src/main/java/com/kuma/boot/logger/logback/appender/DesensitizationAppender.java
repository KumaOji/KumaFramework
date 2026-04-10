package com.kuma.boot.logger.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.logger.utils.DesensitizationUtils;
import java.lang.reflect.Field;

public class DesensitizationAppender {
   private static final String MESSAGE = "message";
   private static final String FORMATTED_MESSAGE = "formattedMessage";

   public DesensitizationAppender() {
   }

   public void operation(LoggingEvent event) {
      if (event.getArgumentArray() != null) {
         String eventFormattedMessage = event.getFormattedMessage();
         DesensitizationUtils util = new DesensitizationUtils();
         String changeMessage = util.customChange(eventFormattedMessage);
         if (null != changeMessage && !"".equals(changeMessage)) {
            try {
               Class<? extends LoggingEvent> eventClass = event.getClass();
               Field message = eventClass.getDeclaredField("message");
               message.setAccessible(true);
               message.set(event, changeMessage);
               Field formattedMessage = eventClass.getDeclaredField("formattedMessage");
               formattedMessage.setAccessible(true);
               formattedMessage.set(event, changeMessage);
            } catch (NoSuchFieldException | IllegalAccessException e) {
               LogUtils.error(e);
            }
         }
      }

   }
}
