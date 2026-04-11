package com.kuma.boot.websocket.stomp.interceptor;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.websocket.stomp.core.PrincipalDetails;
import com.kuma.boot.websocket.stomp.domain.WebSocketPrincipal;
import com.kuma.boot.websocket.stomp.utils.WebSocketUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class WebSocketPrincipalHandshakeHandler extends DefaultHandshakeHandler {
   private static final Logger log = LoggerFactory.getLogger(WebSocketPrincipalHandshakeHandler.class);

   public WebSocketPrincipalHandshakeHandler() {
   }

   protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
      Principal principal = request.getPrincipal();
      if (ObjectUtils.isNotEmpty(principal)) {
         log.debug("[Websocket] |- Determine user from request, value is  [{}].", principal.getName());
         return principal;
      } else {
         HttpServletRequest httpServletRequest = WebSocketUtils.getHttpServletRequest(request);
         if (ObjectUtils.isNotEmpty(httpServletRequest)) {
            Object object = attributes.get("principal");
            if (ObjectUtils.isNotEmpty(object) && object instanceof PrincipalDetails) {
               PrincipalDetails details = (PrincipalDetails)object;
               WebSocketPrincipal webSocketPrincipal = new WebSocketPrincipal(details);
               log.debug("[Websocket] |- Determine user by request parameter, userId is  [{}].", webSocketPrincipal.getUserId());
               return webSocketPrincipal;
            }

            String userId = httpServletRequest.getParameter("openid");
            if (StringUtils.isNotBlank(userId)) {
               WebSocketPrincipal webSocketPrincipal = new WebSocketPrincipal(userId);
               log.debug("[Websocket] |- Determine user by request parameter, userId is  [{}].", userId);
               return webSocketPrincipal;
            }
         }

         log.warn("[Websocket] |- Can not determine user from request.");
         return null;
      }
   }
}
