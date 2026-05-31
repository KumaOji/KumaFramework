package com.kuma.boot.websocket.spring.common.session;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.WebSocketSession;

public class DefaultWebSocketSessionStore implements WebSocketSessionStore {
   private final SessionKeyGenerator sessionKeyGenerator;
   private final ConcurrentHashMap<Object, Map<String, WebSocketSession>> sessionKeyToWsSessions = new ConcurrentHashMap<>();

   public DefaultWebSocketSessionStore(SessionKeyGenerator sessionKeyGenerator) {
      this.sessionKeyGenerator = sessionKeyGenerator;
   }

   public void addSession(WebSocketSession wsSession) {
      Object sessionKey = this.sessionKeyGenerator.sessionKey(wsSession);
      Map<String, WebSocketSession> sessions = this.sessionKeyToWsSessions.get(sessionKey);
      if (sessions == null) {
         Map<String, WebSocketSession> var4 = new ConcurrentHashMap<>();
         this.sessionKeyToWsSessions.putIfAbsent(sessionKey, var4);
         sessions = this.sessionKeyToWsSessions.get(sessionKey);
      }

      sessions.put(wsSession.getId(), wsSession);
   }

   public void removeSession(WebSocketSession session) {
      Object sessionKey = this.sessionKeyGenerator.sessionKey(session);
      String wsSessionId = session.getId();
      Map<String, WebSocketSession> sessions = this.sessionKeyToWsSessions.get(sessionKey);
      if (sessions != null) {
         boolean result = sessions.remove(wsSessionId) != null;
         if (LogUtils.isDebugEnabled()) {
            LogUtils.debug("Removal of " + wsSessionId + " was " + result, new Object[0]);
         }

         if (sessions.isEmpty()) {
            this.sessionKeyToWsSessions.remove(sessionKey);
            if (LogUtils.isDebugEnabled()) {
               LogUtils.debug("Removed the corresponding HTTP Session for " + wsSessionId + " since it contained no WebSocket mappings", new Object[0]);
            }
         }
      }

   }

   public Collection<WebSocketSession> getSessions() {
      return this.sessionKeyToWsSessions.values().stream().flatMap((x) -> x.values().stream()).toList();
   }

   public Collection<WebSocketSession> getSessions(Object sessionKey) {
      Map<String, WebSocketSession> sessions = this.sessionKeyToWsSessions.get(sessionKey);
      if (sessions == null) {
         LogUtils.warn("\u6839\u636e\u6307\u5b9a\u7684sessionKey: {} \u83b7\u53d6\u5bf9\u5e94\u7684wsSessions\u4e3a\u7a7a!", new Object[]{sessionKey});
         return Collections.emptyList();
      } else {
         return sessions.values();
      }
   }

   public Collection<Object> getSessionKeys() {
      return this.sessionKeyToWsSessions.keySet();
   }
}
