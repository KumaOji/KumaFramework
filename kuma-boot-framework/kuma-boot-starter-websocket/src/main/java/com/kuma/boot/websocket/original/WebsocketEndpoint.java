package com.kuma.boot.websocket.original;

public interface WebsocketEndpoint {
   void batchSendMessage(String projectId, String message);

   void sendMessageById(String projectId, String userId, String message);
}
