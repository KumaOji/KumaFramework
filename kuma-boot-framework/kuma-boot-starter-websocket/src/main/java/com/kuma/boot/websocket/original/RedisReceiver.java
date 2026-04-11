package com.kuma.boot.websocket.original;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class RedisReceiver {
   @Resource
   private WebsocketEndpoint websocketEndpoint;

   public RedisReceiver() {
   }

   public void sendMsg(String message) {
      SendMsg msg = (SendMsg)JSONObject.parseObject(message, SendMsg.class);
      this.websocketEndpoint.sendMessageById(msg.getProjectId(), msg.getUserId(), msg.getMsg());
   }

   public void sendAllMsg(String message) {
      SendMsgAll msg = (SendMsgAll)JSONObject.parseObject(message, SendMsgAll.class);
      this.websocketEndpoint.batchSendMessage(msg.getProjectId(), msg.getMsg());
   }
}
