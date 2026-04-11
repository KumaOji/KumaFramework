package com.kuma.boot.websocket.spring.autoconfigure;

import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;

public interface SockJsServiceConfigurer {
   void config(SockJsServiceRegistration sockJsServiceRegistration);
}
