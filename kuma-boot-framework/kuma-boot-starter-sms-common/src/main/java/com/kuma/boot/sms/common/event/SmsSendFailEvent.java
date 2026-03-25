package com.kuma.boot.sms.common.event;

import com.kuma.boot.sms.common.handler.SendHandler;
import java.util.Collection;
import java.util.Map;
import org.springframework.context.ApplicationEvent;

public class SmsSendFailEvent extends ApplicationEvent {
   private final String sendChannel;
   private final Collection phones;
   private final String type;
   private final Map params;
   private final Throwable cause;
   private final Object response;

   public SmsSendFailEvent(SendHandler source, String sendChannel, Collection phones, String type, Map params, Throwable cause, Object response) {
      super(source);
      this.sendChannel = sendChannel;
      this.phones = phones;
      this.type = type;
      this.params = params;
      this.cause = cause;
      this.response = response;
   }

   public String getSendChannel() {
      return this.sendChannel;
   }

   public Collection getPhones() {
      return this.phones;
   }

   public String getType() {
      return this.type;
   }

   public Map getParams() {
      return this.params;
   }

   public Throwable getCause() {
      return this.cause;
   }

   public Object getResponse() {
      return this.response;
   }
}
