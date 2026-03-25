package com.kuma.boot.sms.common.event;

import com.kuma.boot.sms.common.handler.SendHandler;
import java.util.Collection;
import java.util.Map;
import org.springframework.context.ApplicationEvent;

public class SmsSendSuccessEvent extends ApplicationEvent {
   private final String sendChannel;
   private final Collection phones;
   private final String type;
   private final Map params;
   private final Object response;

   public SmsSendSuccessEvent(SendHandler source, String sendChannel, Collection phones, String type, Map params, Object response) {
      super(source);
      this.sendChannel = sendChannel;
      this.phones = phones;
      this.type = type;
      this.params = params;
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

   public Object getResponse() {
      return this.response;
   }
}
