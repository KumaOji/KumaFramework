package com.kuma.boot.websocket.original;

import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@ServerEndpoint("/web/ws/{projectId}/{userId}")
@Component
public class WebsocketEndpointImpl implements WebsocketEndpoint {
   private static Logger log = LoggerFactory.getLogger(WebsocketEndpointImpl.class);
   private static final int MAX_ERROR_NUM = 3;
   private static Map<String, Map<String, WebSocketBean>> webSocketInfo = new ConcurrentHashMap();

   public WebsocketEndpointImpl() {
   }

   @OnOpen
   public void onOpen(Session session, EndpointConfig config, @PathParam("userId") String userId, @PathParam("projectId") String projectId) {
      WebSocketBean bean = new WebSocketBean();
      bean.setSession(session);
      Map<String, WebSocketBean> concurrentHashMap = new ConcurrentHashMap();
      concurrentHashMap.put(userId, bean);
      webSocketInfo.put(projectId, concurrentHashMap);
      log.info("ws\u9879\u76ee:" + projectId + ",\u5ba2\u6237\u7aef\u8fde\u63a5\u670d\u52a1\u5668userId :" + userId + "\u5f53\u524d\u8fde\u63a5\u6570\uff1a" + this.countUser(projectId));
   }

   @OnClose
   public void onClose(Session session, @PathParam("userId") String userId, @PathParam("projectId") String projectId) {
      Map<String, WebSocketBean> concurrentHashMap = (Map)webSocketInfo.get(projectId);
      if (concurrentHashMap != null) {
         concurrentHashMap.remove(userId);
      }

      log.info("ws\u9879\u76ee:" + projectId + ",\u5ba2\u6237\u7aef\u65ad\u5f00\u8fde\u63a5\uff0c\u5f53\u524d\u8fde\u63a5\u6570\uff1a" + this.countUser(projectId));
   }

   @OnMessage
   public void onMessage(Session session, String message, @PathParam("userId") String userId, @PathParam("projectId") String projectId) {
      log.info("ws\u9879\u76ee:" + projectId + ",\u5ba2\u6237\u7aef userId: " + userId + "\uff0c\u6d88\u606f:" + message);
   }

   @OnError
   public void onError(Session session, Throwable throwable) {
   }

   public void sendMessage(Session session, String message, String projectId, String userId) {
      log.info("ws\u9879\u76ee:" + projectId + ",\u8fde\u63a5\u6570:" + this.countUser(projectId) + ",\u53d1\u9001\u6d88\u606f " + String.valueOf(session));

      try {
         synchronized(session) {
            if (session.isOpen()) {
               session.getBasicRemote().sendText(message);
            }
         }

         this.cleanErrorNum(projectId, userId);
      } catch (Exception e) {
         log.error("ws\u9879\u76ee:" + projectId + ",\u7528\u6237:" + userId + "\uff0c\u53d1\u9001\u6d88\u606f\u5931\u8d25" + e.getMessage(), e);
         int errorNum = this.getErroerLinkCount(projectId, userId);
         if (errorNum <= 3) {
            this.sendMessage(session, message, projectId, userId);
         } else {
            log.error("ws\u53d1\u9001\u6d88\u606f\u5931\u8d25\u8d85\u8fc7\u6700\u5927\u6b21\u6570");
            this.cleanErrorNum(projectId, userId);
         }
      }

   }

   public void batchSendMessage(String projectId, String message) {
      Map<String, WebSocketBean> concurrentHashMap = (Map)webSocketInfo.get(projectId);
      if (concurrentHashMap != null) {
         for(Map.Entry<String, WebSocketBean> map : concurrentHashMap.entrySet()) {
            this.sendMessage(((WebSocketBean)map.getValue()).getSession(), message, projectId, (String)map.getKey());
         }
      }

   }

   public void sendMessageById(String projectId, String userId, String message) {
      Map<String, WebSocketBean> concurrentHashMap = (Map)webSocketInfo.get(projectId);
      if (concurrentHashMap != null) {
         WebSocketBean webSocketBean = (WebSocketBean)concurrentHashMap.get(userId);
         if (webSocketBean != null) {
            this.sendMessage(webSocketBean.getSession(), message, projectId, userId);
         }
      }

   }

   private void cleanErrorNum(String projectId, String userId) {
      Map<String, WebSocketBean> concurrentHashMap = (Map)webSocketInfo.get(projectId);
      if (concurrentHashMap != null) {
         WebSocketBean webSocketBean = (WebSocketBean)concurrentHashMap.get(userId);
         if (webSocketBean != null) {
            webSocketBean.cleanErrorNum();
         }
      }

   }

   private int getErroerLinkCount(String projectId, String userId) {
      int errorNum = 0;
      Map<String, WebSocketBean> concurrentHashMap = (Map)webSocketInfo.get(projectId);
      if (concurrentHashMap != null) {
         WebSocketBean webSocketBean = (WebSocketBean)concurrentHashMap.get(userId);
         if (webSocketBean != null) {
            errorNum = webSocketBean.getErroerLinkCount();
         }
      }

      return errorNum;
   }

   private Integer countUser(String projectId) {
      int size = 0;
      Map<String, WebSocketBean> concurrentHashMap = (Map)webSocketInfo.get(projectId);
      if (concurrentHashMap != null) {
         size = concurrentHashMap.size();
      }

      return size;
   }
}
