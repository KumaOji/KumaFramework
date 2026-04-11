package com.kuma.boot.websocket.stomp.core;

public interface BearerTokenResolver {
   PrincipalDetails resolve(String token);
}
