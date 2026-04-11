package com.kuma.boot.websocket.original;

public interface WebsocketService {
   void sendMessageAll(String projectId, String message);

   void sendMessageById(String projectId, String userId, String message);
}
