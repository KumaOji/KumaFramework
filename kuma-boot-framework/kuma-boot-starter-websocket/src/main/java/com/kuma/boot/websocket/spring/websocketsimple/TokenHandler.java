package com.kuma.boot.websocket.spring.websocketsimple;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenHandler {
   LoginInfo getLoginInfo(HttpServletRequest httpServletRequest);
}
