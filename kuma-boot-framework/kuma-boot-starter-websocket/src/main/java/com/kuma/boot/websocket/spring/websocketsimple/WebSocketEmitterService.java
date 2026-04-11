package com.kuma.boot.websocket.spring.websocketsimple;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketEmitterService {
   public static final String ATTRIBUTE_LOGIN_USER = "LOGIN_USER";
   private final ConcurrentMap<String, WebSocketSession> idSessions = new ConcurrentHashMap();
   private final ConcurrentMap<String, ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>>> userSessions = new ConcurrentHashMap();

   public WebSocketEmitterService() {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> this.getSessionList().forEach((session) -> {
            try {
               session.close();
            } catch (Exception var2) {
            }

         })));
   }

   void addSession(WebSocketSession session) {
      this.idSessions.put(session.getId(), session);
      LoginInfo loginInfo = (LoginInfo)session.getAttributes().get("LOGIN_USER");
      ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = (ConcurrentMap)this.userSessions.get(loginInfo.getType());
      if (userSessionsMap == null) {
         userSessionsMap = new ConcurrentHashMap();
         if (this.userSessions.putIfAbsent(loginInfo.getType(), userSessionsMap) != null) {
            userSessionsMap = (ConcurrentMap)this.userSessions.get(loginInfo.getType());
         }
      }

      CopyOnWriteArrayList<WebSocketSession> sessions = (CopyOnWriteArrayList)userSessionsMap.get(loginInfo.getId());
      if (sessions == null) {
         sessions = new CopyOnWriteArrayList();
         if (userSessionsMap.putIfAbsent(loginInfo.getId(), sessions) != null) {
            sessions = (CopyOnWriteArrayList)userSessionsMap.get(loginInfo.getId());
         }
      }

      sessions.add(session);
   }

   void removeSession(WebSocketSession session) {
      this.idSessions.remove(session.getId());
      LoginInfo loginInfo = (LoginInfo)session.getAttributes().get("LOGIN_USER");
      if (loginInfo != null) {
         ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = (ConcurrentMap)this.userSessions.get(loginInfo.getType());
         if (userSessionsMap != null) {
            CopyOnWriteArrayList<WebSocketSession> sessions = (CopyOnWriteArrayList)userSessionsMap.get(loginInfo.getId());
            sessions.removeIf((session0) -> session0.getId().equals(session.getId()));
            if (CollUtil.isEmpty(sessions)) {
               userSessionsMap.remove(loginInfo.getId(), sessions);
            }

         }
      }
   }

   WebSocketSession getSession(String id) {
      return (WebSocketSession)this.idSessions.get(id);
   }

   Collection<WebSocketSession> getSessionList(String type) {
      ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = (ConcurrentMap)this.userSessions.get(type);
      if (CollUtil.isEmpty(userSessionsMap)) {
         return new ArrayList();
      } else {
         LinkedList<WebSocketSession> result = new LinkedList();

         for(List<WebSocketSession> sessions : userSessionsMap.values()) {
            if (!CollUtil.isEmpty(sessions)) {
               result.addAll(sessions);
            }
         }

         return result;
      }
   }

   Collection<WebSocketSession> getSessionList() {
      Collection<ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>>> concurrentMapCollection = this.userSessions.values();
      if (CollUtil.isEmpty(concurrentMapCollection)) {
         return new ArrayList();
      } else {
         LinkedList<WebSocketSession> result = new LinkedList();
         Iterator<ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>>> iterator = concurrentMapCollection.iterator();

         while(iterator.hasNext()) {
            for(List<WebSocketSession> sessions : ((ConcurrentMap)iterator.next()).values()) {
               if (!CollUtil.isEmpty(sessions)) {
                  result.addAll(sessions);
               }
            }
         }

         return result;
      }
   }

   Collection<WebSocketSession> getSessionList(String type, String id) {
      ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = (ConcurrentMap)this.userSessions.get(type);
      if (CollUtil.isEmpty(userSessionsMap)) {
         return new ArrayList();
      } else {
         CopyOnWriteArrayList<WebSocketSession> sessions = (CopyOnWriteArrayList)userSessionsMap.get(id);
         return CollUtil.isNotEmpty(sessions) ? new ArrayList(sessions) : new ArrayList();
      }
   }

   public void send(String type, String id, String messageType, String messageContent) {
      this.send((String)null, type, id, messageType, messageContent);
   }

   public void send(String type, String messageType, String messageContent) {
      this.send((String)null, type, (String)null, messageType, messageContent);
   }

   public void sendAll(String messageType, String messageContent) {
      this.send((String)null, (String)null, (String)null, messageType, messageContent);
   }

   private void send(String sessionId, String type, String id, String messageType, String messageContent) {
      List<WebSocketSession> sessions = Collections.emptyList();
      if (CharSequenceUtil.isNotEmpty(sessionId)) {
         WebSocketSession session = this.getSession(sessionId);
         if (session != null) {
            sessions = Collections.singletonList(session);
         }
      } else if (type != null && id != null) {
         sessions = (List)this.getSessionList(type, id);
      } else if (type != null) {
         sessions = (List)this.getSessionList(type);
      } else {
         sessions = (List)this.getSessionList();
      }

      if (CollUtil.isEmpty(sessions)) {
         LogUtils.info("[send][sessionId({}) userType({}) userId({}) messageType({}) messageContent({}) \u672a\u5339\u914d\u5230\u4f1a\u8bdd]", new Object[]{sessionId, type, id, messageType, messageContent});
      }

      this.doSend(sessions, messageType, messageContent);
   }

   private void doSend(Collection<WebSocketSession> sessions, String messageType, String messageContent) {
      JsonWebSocketMessage message = new JsonWebSocketMessage();
      message.setType(messageType);
      message.setContent(messageContent);
      String payload = JSONUtil.toJsonStr(message);
      sessions.forEach((session) -> {
         if (session == null) {
            LogUtils.error("[doSend][session \u4e3a\u7a7a, message({})]", new Object[]{message});
         } else if (!session.isOpen()) {
            LogUtils.error("[doSend][session({}) \u5df2\u5173\u95ed, message({})]", new Object[]{session.getId(), message});
         } else {
            try {
               session.sendMessage(new TextMessage(payload));
               LogUtils.info("[doSend][session({}) \u53d1\u9001\u6d88\u606f\u6210\u529f\uff0cmessage({})]", new Object[]{session.getId(), message});
            } catch (Exception ex) {
               LogUtils.error("[doSend][session({}) \u53d1\u9001\u6d88\u606f\u5931\u8d25\uff0cmessage({})]", new Object[]{session.getId(), message, ex});
            }

         }
      });
   }
}
