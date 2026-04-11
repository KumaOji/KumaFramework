package com.kuma.boot.websocket.spring.websocketsimple;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class LoginInfoHandshakeInterceptor implements HandshakeInterceptor {
   private TokenHandler tokenHandler;

   public LoginInfoHandshakeInterceptor(TokenHandler tokenHandler) {
      this.tokenHandler = tokenHandler;
   }

   public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
      if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
         LoginInfo loginInfo = this.tokenHandler.getLoginInfo(servletServerHttpRequest.getServletRequest());
         if (loginInfo == null) {
            return false;
         }
      }

      return true;
   }

   public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
   }
}
