package com.kuma.boot.websocket.spring.common.distribute;

import java.util.List;

public class MessageDO {
   private Boolean needBroadcast;
   private Boolean onlyOneClientInSameKey;
   private List<Object> sessionKeys;
   private String messageText;

   public MessageDO() {
   }

   public Boolean getNeedBroadcast() {
      return this.needBroadcast;
   }

   public void setNeedBroadcast(Boolean needBroadcast) {
      this.needBroadcast = needBroadcast;
   }

   public Boolean getOnlyOneClientInSameKey() {
      return this.onlyOneClientInSameKey;
   }

   public void setOnlyOneClientInSameKey(Boolean onlyOneClientInSameKey) {
      this.onlyOneClientInSameKey = onlyOneClientInSameKey;
   }

   public List<Object> getSessionKeys() {
      return this.sessionKeys;
   }

   public void setSessionKeys(List<Object> sessionKeys) {
      this.sessionKeys = sessionKeys;
   }

   public String getMessageText() {
      return this.messageText;
   }

   public void setMessageText(String messageText) {
      this.messageText = messageText;
   }
}
