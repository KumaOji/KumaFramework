package com.kuma.boot.websocket.spring.common;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.spring.common.message.JsonWebSocketMessage;
import java.io.IOException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public final class WebSocketMessageSender {
   private WebSocketMessageSender() {
   }

   public static void send(WebSocketSession session, JsonWebSocketMessage message) {
      send(session, JacksonUtils.toJson(message));
   }

   public static boolean send(WebSocketSession session, String message) {
      if (session == null) {
         LogUtils.error("[send] session 为 null", new Object[0]);
         return false;
      } else if (!session.isOpen()) {
         LogUtils.error("[send] session 已经关闭", new Object[0]);
         return false;
      } else {
         try {
            session.sendMessage(new TextMessage(message));
            return true;
         } catch (IOException e) {
            LogUtils.error("[send] session({}) 发送消息({}) 异常", new Object[]{session, message, e});
            return false;
         }
      }
   }
}
