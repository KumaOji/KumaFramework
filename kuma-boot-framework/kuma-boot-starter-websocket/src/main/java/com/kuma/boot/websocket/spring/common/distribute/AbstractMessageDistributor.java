package com.kuma.boot.websocket.spring.common.distribute;

import cn.hutool.core.collection.CollUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.spring.common.WebSocketMessageSender;
import com.kuma.boot.websocket.spring.common.session.WebSocketSessionStore;
import java.util.Collection;
import org.springframework.web.socket.WebSocketSession;

public abstract class AbstractMessageDistributor implements MessageDistributor {
   private final WebSocketSessionStore webSocketSessionStore;

   protected AbstractMessageDistributor(WebSocketSessionStore webSocketSessionStore) {
      this.webSocketSessionStore = webSocketSessionStore;
   }

   protected void doSend(MessageDO messageDO) {
      Boolean needBroadcast = messageDO.getNeedBroadcast();
      Collection<Object> sessionKeys;
      if (needBroadcast != null && needBroadcast) {
         sessionKeys = this.webSocketSessionStore.getSessionKeys();
      } else {
         sessionKeys = messageDO.getSessionKeys();
      }

      if (CollUtil.isEmpty(sessionKeys)) {
         LogUtils.warn("\u53d1\u9001 websocket \u6d88\u606f\uff0c\u5374\u6ca1\u6709\u627e\u5230\u5bf9\u5e94 sessionKeys, messageDo: {}", new Object[]{messageDO});
      } else {
         String messageText = messageDO.getMessageText();
         Boolean onlyOneClientInSameKey = messageDO.getOnlyOneClientInSameKey();

         for(Object sessionKey : sessionKeys) {
            Collection<WebSocketSession> sessions = this.webSocketSessionStore.getSessions(sessionKey);
            if (CollUtil.isNotEmpty(sessions)) {
               if (onlyOneClientInSameKey != null && onlyOneClientInSameKey) {
                  WebSocketSession wsSession = (WebSocketSession)CollUtil.get(sessions, 0);
                  WebSocketMessageSender.send(wsSession, messageText);
               } else {
                  for(WebSocketSession wsSession : sessions) {
                     WebSocketMessageSender.send(wsSession, messageText);
                  }
               }
            }
         }

      }
   }
}
