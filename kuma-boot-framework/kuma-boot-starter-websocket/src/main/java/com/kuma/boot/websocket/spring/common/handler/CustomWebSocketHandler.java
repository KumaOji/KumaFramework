package com.kuma.boot.websocket.spring.common.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.spring.common.exception.ErrorJsonMessageException;
import com.kuma.boot.websocket.spring.common.holder.JsonMessageHandlerHolder;
import com.kuma.boot.websocket.spring.common.message.JsonWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class CustomWebSocketHandler extends TextWebSocketHandler {
   private static final JsonMapper MAPPER;
   private PlanTextMessageHandler planTextMessageHandler;

   public CustomWebSocketHandler() {
   }

   public CustomWebSocketHandler(PlanTextMessageHandler planTextMessageHandler) {
      this.planTextMessageHandler = planTextMessageHandler;
   }

   public void handleTextMessage(WebSocketSession session, TextMessage message) {
      if (message.getPayloadLength() != 0) {
         String payload = (String)message.getPayload();

         try {
            this.handleWithJson(session, payload);
         } catch (ErrorJsonMessageException ex) {
            LogUtils.debug("消息载荷 [{}] 回退使用 PlanTextMessageHandler，原因：{}", new Object[]{payload, ex.getMessage()});
            if (this.planTextMessageHandler != null) {
               this.planTextMessageHandler.handle(session, payload);
            } else {
               LogUtils.error("[handleTextMessage] 普通文本消息（{}）没有对应的消息处理器", new Object[]{payload});
            }
         }

      }
   }

   private void handleWithJson(WebSocketSession session, String payload) {
      JsonNode jsonNode = null;

      try {
         jsonNode = MAPPER.readTree(payload);
      } catch (JacksonException var11) {
         throw new ErrorJsonMessageException("json 解析异常");
      }

      if (!jsonNode.isObject()) {
         throw new ErrorJsonMessageException("json 格式异常！非 object 类型！");
      } else {
         JsonNode typeNode = jsonNode.get("type");
         String messageType = typeNode.asString();
         if (messageType == null) {
            throw new ErrorJsonMessageException("json 无 type 属性");
         } else {
            JsonMessageHandler<JsonWebSocketMessage> jsonMessageHandler = JsonMessageHandlerHolder.getHandler(messageType);
            if (jsonMessageHandler == null) {
               LogUtils.error("[handleTextMessage] 消息类型（{}）不存在对应的消息处理器", new Object[]{messageType});
            } else {
               Class<? extends JsonWebSocketMessage> messageClass = jsonMessageHandler.getMessageClass();

               JsonWebSocketMessage websocketMessageJson;
               try {
                  websocketMessageJson = (JsonWebSocketMessage)MAPPER.treeToValue(jsonNode, messageClass);
               } catch (JacksonException var10) {
                  throw new ErrorJsonMessageException("消息序列化异常，class " + String.valueOf(messageClass));
               }

               jsonMessageHandler.handle(session, websocketMessageJson);
            }
         }
      }
   }

   static {
      JsonMapper.Builder builder = JsonMapper.builder();
      builder.enable(new JsonReadFeature[]{JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS});
      MAPPER = builder.build();
   }
}
