package com.kuma.boot.websocket.stomp.service;

import com.kuma.boot.websocket.stomp.utils.WebSocketUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WebSocketDisplayService {
   public WebSocketDisplayService() {
   }

   public Map<String, Object> findAllStat() {
      Map<String, Object> data = new HashMap();
      data.put("onlineCount", WebSocketUtils.getOnlineCount());
      return data;
   }
}
