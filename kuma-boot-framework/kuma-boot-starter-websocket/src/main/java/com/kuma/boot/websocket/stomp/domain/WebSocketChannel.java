package com.kuma.boot.websocket.stomp.domain;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum WebSocketChannel {
   NOTICE("/notice", "\u4e2a\u4eba\u901a\u77e5");

   @Schema(
      title = "\u6d88\u606f\u7aef\u70b9"
   )
   private final String destination;
   @Schema(
      title = "\u8bf4\u660e"
   )
   private final String description;
   private static final Map<String, WebSocketChannel> INDEX_MAP = new HashMap<>();
   private static final List<Map<String, Object>> JSON_STRUCT = new ArrayList<>();

   private WebSocketChannel(String destination, String description) {
      this.destination = destination;
      this.description = description;
   }

   public String getDestination() {
      return this.destination;
   }

   public String getDescription() {
      return this.description;
   }

   public static WebSocketChannel getWebSocketChannel(String code) {
      return (WebSocketChannel)INDEX_MAP.get(code);
   }

   public static List<Map<String, Object>> getToJsonStruct() {
      return JSON_STRUCT;
   }

   // $FF: synthetic method
   private static WebSocketChannel[] $values() {
      return new WebSocketChannel[]{NOTICE};
   }

   static {
      for(WebSocketChannel webSocketChannel : values()) {
         INDEX_MAP.put(webSocketChannel.name(), webSocketChannel);
         JSON_STRUCT.add(webSocketChannel.ordinal(), ImmutableMap.<String, Object>builder().put("value", webSocketChannel.ordinal()).put("key", webSocketChannel.name()).put("text", webSocketChannel.getDescription()).build());
      }

   }
}
