package com.kuma.boot.websocket.original;

import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServerImpl implements WebsocketService {
   private final RedisTemplate<String, String> redisTemplate;

   public WebSocketServerImpl(RedisTemplate<String, String> redisTemplate) {
      this.redisTemplate = redisTemplate;
   }

   public void sendMessageAll(String projectId, String message) {
      SendMsgAll sendMsgAll = new SendMsgAll();
      sendMsgAll.setProjectId(projectId);
      sendMsgAll.setMsg(message);
      this.redisTemplate.convertAndSend("mh-topic", JSON.toJSONString(sendMsgAll));
   }

   public void sendMessageById(String projectId, String userId, String message) {
      SendMsg sendMsg = new SendMsg();
      sendMsg.setProjectId(projectId);
      sendMsg.setUserId(userId);
      sendMsg.setMsg(message);
      this.redisTemplate.convertAndSend("ptp-topic", JSON.toJSONString(sendMsg));
   }
}
