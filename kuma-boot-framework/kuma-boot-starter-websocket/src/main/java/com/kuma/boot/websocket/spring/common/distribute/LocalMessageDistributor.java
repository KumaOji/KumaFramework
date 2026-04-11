package com.kuma.boot.websocket.spring.common.distribute;

import com.kuma.boot.websocket.spring.common.session.WebSocketSessionStore;

public class LocalMessageDistributor extends AbstractMessageDistributor {
   public LocalMessageDistributor(WebSocketSessionStore webSocketSessionStore) {
      super(webSocketSessionStore);
   }

   public void distribute(MessageDO messageDO) {
      this.doSend(messageDO);
   }
}
