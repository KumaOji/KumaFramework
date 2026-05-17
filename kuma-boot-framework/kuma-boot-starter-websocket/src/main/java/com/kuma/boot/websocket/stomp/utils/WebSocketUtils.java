package com.kuma.boot.websocket.stomp.utils;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.core.utils.context.ContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

public class WebSocketUtils {
   public WebSocketUtils() {
   }

   public static HttpServletRequest getHttpServletRequest(ServerHttpRequest serverHttpRequest) {
      if (serverHttpRequest instanceof ServletServerHttpRequest request) {
         return request.getServletRequest();
      } else {
         return null;
      }
   }

   public static HttpServletResponse getHttpServletResponse(ServerHttpResponse serverHttpResponse) {
      if (serverHttpResponse instanceof ServletServerHttpResponse response) {
         return response.getServletResponse();
      } else {
         return null;
      }
   }

   public static int getOnlineCount() {
      RedisRepository redisRepository = (RedisRepository)ContextUtils.getBean(RedisRepository.class, true);
      Long count = redisRepository.bitCount("data:msg:online:user");
      return count.intValue();
   }
}
