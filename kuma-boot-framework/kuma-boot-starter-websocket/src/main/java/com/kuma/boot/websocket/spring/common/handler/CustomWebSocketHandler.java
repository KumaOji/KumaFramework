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
            LogUtils.debug("\u6d88\u606f\u8f7d\u8377 [{}] \u56de\u9000\u4f7f\u7528 PlanTextMessageHandler\uff0c\u539f\u56e0\uff1a{}", new Object[]{payload, ex.getMessage()});
            if (this.planTextMessageHandler != null) {
               this.planTextMessageHandler.handle(session, payload);
            } else {
               LogUtils.error("[handleTextMessage] \u666e\u901a\u6587\u672c\u6d88\u606f\uff08{}\uff09\u6ca1\u6709\u5bf9\u5e94\u7684\u6d88\u606f\u5904\u7406\u5668", new Object[]{payload});
            }
         }

      }
   }

   private void handleWithJson(WebSocketSession session, String payload) {
      JsonNode jsonNode = null;

      try {
         jsonNode = MAPPER.readTree(payload);
      } catch (JacksonException var11) {
         throw new ErrorJsonMessageException("json \u89e3\u6790\u5f02\u5e38");
      }

      if (!jsonNode.isObject()) {
         throw new ErrorJsonMessageException("json \u683c\u5f0f\u5f02\u5e38\uff01\u975e object \u7c7b\u578b\uff01");
      } else {
         JsonNode typeNode = jsonNode.get("type");
         String messageType = typeNode.asString();
         if (messageType == null) {
            throw new ErrorJsonMessageException("json \u65e0 type \u5c5e\u6027");
         } else {
            JsonMessageHandler<JsonWebSocketMessage> jsonMessageHandler = JsonMessageHandlerHolder.getHandler(messageType);
            if (jsonMessageHandler == null) {
               LogUtils.error("[handleTextMessage] \u6d88\u606f\u7c7b\u578b\uff08{}\uff09\u4e0d\u5b58\u5728\u5bf9\u5e94\u7684\u6d88\u606f\u5904\u7406\u5668", new Object[]{messageType});
            } else {
               Class<? extends JsonWebSocketMessage> messageClass = jsonMessageHandler.getMessageClass();

               JsonWebSocketMessage websocketMessageJson;
               try {
                  websocketMessageJson = (JsonWebSocketMessage)MAPPER.treeToValue(jsonNode, messageClass);
               } catch (JacksonException var10) {
                  throw new ErrorJsonMessageException("\u6d88\u606f\u5e8f\u5217\u5316\u5f02\u5e38\uff0cclass " + String.valueOf(messageClass));
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
