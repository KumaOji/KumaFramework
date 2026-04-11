package com.kuma.boot.websocket.spring.common.handler;

import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator.OverflowStrategy;

public class ConcurrentWebSocketSessionOptions {
   private boolean enable = false;
   private int sendTimeLimit = 5000;
   private int bufferSizeLimit = 102400;
   ConcurrentWebSocketSessionDecorator.OverflowStrategy overflowStrategy;

   public ConcurrentWebSocketSessionOptions() {
      this.overflowStrategy = OverflowStrategy.TERMINATE;
   }

   public boolean isEnable() {
      return this.enable;
   }

   public void setEnable(boolean enable) {
      this.enable = enable;
   }

   public int getSendTimeLimit() {
      return this.sendTimeLimit;
   }

   public void setSendTimeLimit(int sendTimeLimit) {
      this.sendTimeLimit = sendTimeLimit;
   }

   public int getBufferSizeLimit() {
      return this.bufferSizeLimit;
   }

   public void setBufferSizeLimit(int bufferSizeLimit) {
      this.bufferSizeLimit = bufferSizeLimit;
   }

   public ConcurrentWebSocketSessionDecorator.OverflowStrategy getOverflowStrategy() {
      return this.overflowStrategy;
   }

   public void setOverflowStrategy(ConcurrentWebSocketSessionDecorator.OverflowStrategy overflowStrategy) {
      this.overflowStrategy = overflowStrategy;
   }
}
