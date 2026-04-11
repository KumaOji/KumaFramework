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
         LogUtils.error("[send] session \u4e3a null", new Object[0]);
         return false;
      } else if (!session.isOpen()) {
         LogUtils.error("[send] session \u5df2\u7ecf\u5173\u95ed", new Object[0]);
         return false;
      } else {
         try {
            session.sendMessage(new TextMessage(message));
            return true;
         } catch (IOException e) {
            LogUtils.error("[send] session({}) \u53d1\u9001\u6d88\u606f({}) \u5f02\u5e38", new Object[]{session, message, e});
            return false;
         }
      }
   }
}
