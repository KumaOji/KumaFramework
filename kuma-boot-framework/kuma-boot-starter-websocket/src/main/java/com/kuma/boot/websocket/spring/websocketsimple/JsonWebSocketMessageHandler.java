package com.kuma.boot.websocket.spring.websocketsimple;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.TypeUtil;
import com.kuma.boot.core.utils.io.FileUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class JsonWebSocketMessageHandler extends TextWebSocketHandler {
   private final Map<String, WebSocketMessageListener<Object>> listeners = new HashMap<>();

   @SuppressWarnings("unchecked")
   public JsonWebSocketMessageHandler(List<? extends WebSocketMessageListener> listenersList) {
      listenersList.forEach((listener) -> this.listeners.put(listener.getType(), (WebSocketMessageListener<Object>) listener));
   }

   protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
      if (message.getPayloadLength() != 0) {
         if (message.getPayloadLength() == 4 && Objects.equals(message.getPayload(), "ping")) {
            session.sendMessage(new TextMessage("pong"));
         } else {
            try {
               JsonWebSocketMessage jsonMessage = (JsonWebSocketMessage)BeanUtil.toBean(message.getPayload(), JsonWebSocketMessage.class);
               if (jsonMessage == null) {
                  LogUtils.error("[handleTextMessage][session({}) message({}) 解析为空]", new Object[]{session.getId(), message.getPayload()});
                  return;
               }

               if (FileUtils.isEmpty(jsonMessage.getType())) {
                  LogUtils.error("[handleTextMessage][session({}) message({}) 类型为空]", new Object[]{session.getId(), message.getPayload()});
                  return;
               }

               WebSocketMessageListener<Object> messageListener = this.listeners.get(jsonMessage.getType());
               if (messageListener == null) {
                  LogUtils.error("[handleTextMessage][session({}) message({}) 监听器为空]", new Object[]{session.getId(), message.getPayload()});
                  return;
               }

               Type type = TypeUtil.getTypeArgument(messageListener.getClass(), 0);
               Object messageObj = BeanUtil.toBean(jsonMessage.getContent(), type.getClass());
               messageListener.onMessage(session, messageObj);
            } catch (Throwable var7) {
               LogUtils.error("[handleTextMessage][session({}) message({}) 处理异常]", new Object[]{session.getId(), message.getPayload()});
            }

         }
      }
   }
}
