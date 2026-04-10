package com.kuma.boot.logger.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.alibaba.fastjson2.JSON;
import java.util.HashMap;
import java.util.Map;

public class LogBackHttpFeiShuAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
   private String url = "";
   protected Encoder<ILoggingEvent> encoder;
   private String title = "demo-project";

   public LogBackHttpFeiShuAppender() {
   }

   protected void append(ILoggingEvent eventObject) {
      String content = new String(this.encoder.encode(eventObject));
      this.sendFeiShu(content);
   }

   public void sendFeiShu(String content) {
      try {
         Thread.sleep(500L);
         Map<String, String> map = new HashMap(2);
         map.put("title", this.title);
         map.put("content", content);
         String var3 = JSON.toJSONString(map);
      } catch (Exception var4) {
      }

   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public Encoder<ILoggingEvent> getEncoder() {
      return this.encoder;
   }

   public void setEncoder(Encoder<ILoggingEvent> encoder) {
      this.encoder = encoder;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }
}
