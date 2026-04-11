package com.kuma.boot.websocket.stomp.processor;

import com.kuma.boot.websocket.stomp.core.BearerTokenResolver;
import com.kuma.boot.websocket.stomp.core.PrincipalDetails;

public class WebSocketBearerTokenResolver implements BearerTokenResolver {
   public WebSocketBearerTokenResolver() {
   }

   public PrincipalDetails resolve(String token) {
      PrincipalDetails details = new PrincipalDetails();
      details.setOpenId(token);
      return details;
   }
}
