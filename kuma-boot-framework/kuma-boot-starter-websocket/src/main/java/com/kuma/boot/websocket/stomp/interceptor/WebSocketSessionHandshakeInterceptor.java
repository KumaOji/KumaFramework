package com.kuma.boot.websocket.stomp.interceptor;

import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.websocket.stomp.core.BearerTokenResolver;
import com.kuma.boot.websocket.stomp.core.PrincipalDetails;
import com.kuma.boot.websocket.stomp.utils.WebSocketUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class WebSocketSessionHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
   private static final Logger log = LoggerFactory.getLogger(WebSocketSessionHandshakeInterceptor.class);
   private static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
   private final BearerTokenResolver bearerTokenResolver;

   public WebSocketSessionHandshakeInterceptor(BearerTokenResolver bearerTokenResolver) {
      this.bearerTokenResolver = bearerTokenResolver;
   }

   public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
      HttpServletRequest httpServletRequest = WebSocketUtils.getHttpServletRequest(request);
      if (ObjectUtils.isNotEmpty(httpServletRequest)) {
         String protocol = httpServletRequest.getHeader("Sec-WebSocket-Protocol");
         String token = this.determineToken(protocol);
         if (StringUtils.isNotBlank(token)) {
            log.debug("[Websocket] |- WebSocket fetch the token is [{}].", token);
            PrincipalDetails details = this.bearerTokenResolver.resolve(token);
            if (!ObjectUtils.isNotEmpty(details)) {
               response.setStatusCode(HttpStatus.UNAUTHORIZED);
               log.info("[Websocket] |- Token is invalid for WebSocket, stop handshake.");
               return false;
            }

            attributes.put("principal", details);
            attributes.put("HTTP.SESSION.ID", httpServletRequest.getSession(false));
         }
      }

      return super.beforeHandshake(request, response, wsHandler, attributes);
   }

   private String determineToken(String protocol) {
      if (StringUtils.contains(protocol, SymbolConstants.COMMA)) {
         String[] protocols = StringUtils.split(protocol, SymbolConstants.COMMA);

         for(String item : protocols) {
            if (!StringUtils.endWith(item, ".stomp")) {
               return item;
            }
         }
      }

      return null;
   }

   public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
      HttpServletRequest httpServletRequest = WebSocketUtils.getHttpServletRequest(request);
      HttpServletResponse httpServletResponse = WebSocketUtils.getHttpServletResponse(response);
      if (ObjectUtils.isNotEmpty(httpServletRequest) && ObjectUtils.isNotEmpty(httpServletResponse)) {
         httpServletResponse.setHeader("Sec-WebSocket-Protocol", "v10.stomp");
      }

      log.info("[Websocket] |- WebSocket handshake success!");
   }
}
