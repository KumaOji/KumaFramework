package com.kuma.boot.websocket.spring.websocketsimple;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhanghongbin
 */
public class WebSocketEmitterService {

   public static final String ATTRIBUTE_LOGIN_USER = "LOGIN_USER";

   public WebSocketEmitterService() {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         this.getSessionList().forEach(session -> {
            try {
               session.close();
            } catch (Exception e) {
            }
         });
      }));
   }

   /**
    * id 与 WebSocketSession 映射
    * <p>
    * key：Session 编号
    */
   private final ConcurrentMap<String, WebSocketSession> idSessions = new ConcurrentHashMap<>();

   /**
    * user 与 WebSocketSession 映射
    * <p>
    * key1：类型
    * key2：id
    */
   private final ConcurrentMap<String, ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>>> userSessions =
           new ConcurrentHashMap<>();

   void addSession(WebSocketSession session) {
      // 添加到 idSessions 中
      idSessions.put(session.getId(), session);
      // 添加到 userSessions 中
      LoginInfo loginInfo = (LoginInfo) session.getAttributes().get(ATTRIBUTE_LOGIN_USER);

      ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap =
              userSessions.get(loginInfo.getType());
      if (userSessionsMap == null) {
         userSessionsMap = new ConcurrentHashMap<>();
         if (userSessions.putIfAbsent(loginInfo.getType(), userSessionsMap) != null) {
            userSessionsMap = userSessions.get(loginInfo.getType());
         }
      }
      CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(loginInfo.getId());
      if (sessions == null) {
         sessions = new CopyOnWriteArrayList<>();
         if (userSessionsMap.putIfAbsent(loginInfo.getId(), sessions) != null) {
            sessions = userSessionsMap.get(loginInfo.getId());
         }
      }
      sessions.add(session);
   }

   void removeSession(WebSocketSession session) {
      // 移除从 idSessions 中
      idSessions.remove(session.getId());
      // 移除从 idSessions 中
      LoginInfo loginInfo = (LoginInfo) session.getAttributes().get(ATTRIBUTE_LOGIN_USER);
      if (loginInfo == null) {
         return;
      }
      ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap =
              userSessions.get(loginInfo.getType());
      if (userSessionsMap == null) {
         return;
      }
      CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(loginInfo.getId());
      sessions.removeIf(session0 -> session0.getId().equals(session.getId()));
      if (CollUtil.isEmpty(sessions)) {
         userSessionsMap.remove(loginInfo.getId(), sessions);
      }
   }

   WebSocketSession getSession(String id) {
      return idSessions.get(id);
   }

   Collection<WebSocketSession> getSessionList(String type) {
      ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(type);
      if (CollUtil.isEmpty(userSessionsMap)) {
         return new ArrayList<>();
      }
      LinkedList<WebSocketSession> result = new LinkedList<>(); // 避免扩容
      for (List<WebSocketSession> sessions : userSessionsMap.values()) {
         if (CollUtil.isEmpty(sessions)) {
            continue;
         }
         result.addAll(sessions);
      }
      return result;
   }

   Collection<WebSocketSession> getSessionList() {
      Collection<ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>>> concurrentMapCollection =
              userSessions.values();
      if (CollUtil.isEmpty(concurrentMapCollection)) {
         return new ArrayList<>();
      }
      LinkedList<WebSocketSession> result = new LinkedList<>();
      Iterator<ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>>> iterator =
              concurrentMapCollection.iterator();
      while (iterator.hasNext()) {
         for (List<WebSocketSession> sessions : iterator.next().values()) {
            if (CollUtil.isEmpty(sessions)) {
               continue;
            }
            result.addAll(sessions);
         }
      }
      return result;
   }

   Collection<WebSocketSession> getSessionList(String type, String id) {
      ConcurrentMap<String, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(type);
      if (CollUtil.isEmpty(userSessionsMap)) {
         return new ArrayList<>();
      }
      CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(id);
      return CollUtil.isNotEmpty(sessions) ? new ArrayList<>(sessions) : new ArrayList<>();
   }

   public void send(String type, String id, String messageType, String messageContent) {
      send(null, type, id, messageType, messageContent);
   }

   public void send(String type, String messageType, String messageContent) {
      send(null, type, null, messageType, messageContent);
   }

   public void sendAll(String messageType, String messageContent) {
      send(null, null, null, messageType, messageContent);
   }

   /**
    * 发送消息
    *
    * @param sessionId      Session 编号
    * @param type           登录类型
    * @param id             登录id
    * @param messageType    消息类型
    * @param messageContent 消息内容
    */
   private void send(String sessionId, String type, String id, String messageType, String messageContent) {
      // 1. 获得 Session 列表
      List<WebSocketSession> sessions = Collections.emptyList();
      if (CharSequenceUtil.isNotEmpty(sessionId)) {
         WebSocketSession session = this.getSession(sessionId);
         if (session != null) {
            sessions = Collections.singletonList(session);
         }
      } else if (type != null && id != null) {
         sessions = (List<WebSocketSession>) this.getSessionList(type, id);
      } else if (type != null) {
         sessions = (List<WebSocketSession>) this.getSessionList(type);
      } else {
         sessions = (List<WebSocketSession>) this.getSessionList();
      }
      if (CollUtil.isEmpty(sessions)) {
         LogUtils.info(
                 "[send][sessionId({}) userType({}) userId({}) messageType({}) messageContent({}) 未匹配到会话]",
                 sessionId,
                 type,
                 id,
                 messageType,
                 messageContent);
      }
      // 2. 执行发送
      doSend(sessions, messageType, messageContent);
   }

   /**
    * 发送消息的具体实现
    *
    * @param sessions       Session 列表
    * @param messageType    消息类型
    * @param messageContent 消息内容
    */
   private void doSend(Collection<WebSocketSession> sessions, String messageType, String messageContent) {
      JsonWebSocketMessage message = new JsonWebSocketMessage();
      message.setType(messageType);
      message.setContent(messageContent);
      String payload = JSONUtil.toJsonStr(message);
      sessions.forEach(session -> {
         // 1. 保证 Session 可以被发送
         if (session == null) {
            LogUtils.error("[doSend][session 为空, message({})]", message);
            return;
         }
         if (!session.isOpen()) {
            LogUtils.error("[doSend][session({}) 已关闭, message({})]", session.getId(), message);
            return;
         }
         // 2. 执行发送
         try {
            session.sendMessage(new TextMessage(payload));
            LogUtils.info("[doSend][session({}) 发送消息成功，message({})]", session.getId(), message);
         } catch (Exception ex) {
            LogUtils.error("[doSend][session({}) 发送消息失败，message({})]", session.getId(), message, ex);
         }
      });
   }
}
