package com.kuma.boot.logger.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.kuma.boot.common.utils.log.LogUtils;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

public class HttpLoggerAppender extends UnsynchronizedAppenderBase<LoggingEvent> {
   public HttpLoggerAppender() {
   }

   protected void append(LoggingEvent le) {
      try {
         String logUrl = this.context.getProperty("logUrl") + "?endPoint={endPoint}&title={title}&exMsg={exMsg}&level={level}";
         String title = this.context.getProperty("logTitle");
         String port = this.context.getProperty("logPort");
         String content = le.getFormattedMessage();
         Map<String, Object> log = new HashMap();
         log.put("level", le.getLevel().levelStr);
         String var10002 = le.getLevel().levelStr;
         log.put("exMsg", var10002 + "-" + le.getLoggerName() + ":" + content);
         log.put("title", title);
         var10002 = InetAddress.getLocalHost().getHostAddress();
         log.put("endPoint", var10002 + ":" + port);
         RestTemplate restTemplate = new RestTemplate();

         try {
            restTemplate.getForObject(logUrl, String.class, log);
         } catch (Exception e) {
            LogUtils.error(e);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }

   }
}
