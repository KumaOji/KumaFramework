package com.kuma.boot.websocket.original.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.websocket.original.RedisMessageListenerConfig;
import com.kuma.boot.websocket.original.RedisReceiver;
import com.kuma.boot.websocket.original.WebSocketConfig;
import com.kuma.boot.websocket.original.WebSocketServerImpl;
import com.kuma.boot.websocket.original.WebsocketEndpointImpl;
import com.kuma.boot.websocket.original.autoconfigure.properties.WebSocketOriginalProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({RedisMessageListenerConfig.class, RedisReceiver.class, WebSocketConfig.class, WebsocketEndpointImpl.class, WebSocketServerImpl.class})
@EnableConfigurationProperties({WebSocketOriginalProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.websocket.original",
   name = {"enabled"},
   havingValue = "true"
)
public class WebSocketOriginalAutoConfiguration implements InitializingBean {
   public WebSocketOriginalAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(WebSocketOriginalAutoConfiguration.class, "kuma-boot-starter-websocket", new String[0]);
   }
}
