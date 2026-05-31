package com.kuma.boot.websocket.stomp.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(
   title = "\u901a\u77e5\u7c7b\u522b"
)
@JsonFormat(
   shape = Shape.OBJECT
)
public enum NotificationCategory {
   ANNOUNCEMENT(0, "\u7cfb\u7edf\u516c\u544a"),
   DIALOGUE(1, "\u79c1\u4fe1");

   @Schema(
      title = "\u679a\u4e3e\u503c"
   )
   private final Integer value;
   @Schema(
      title = "\u8bf4\u660e"
   )
   private final String description;
   private static final Map<Integer, NotificationCategory> INDEX_MAP = new HashMap<>();
   private static final List<Map<String, Object>> JSON_STRUCTURE = new ArrayList<>();

   private NotificationCategory(Integer value, String description) {
      this.value = value;
      this.description = description;
   }

   @JsonValue
   public Integer getValue() {
      return this.value;
   }

   public String getDescription() {
      return this.description;
   }

   public static NotificationCategory get(Integer index) {
      return INDEX_MAP.getOrDefault(index, null);
   }

   public static List<Map<String, Object>> getPreprocessedJsonStructure() {
      return JSON_STRUCTURE;
   }

   // $FF: synthetic method
   private static NotificationCategory[] $values() {
      return new NotificationCategory[]{ANNOUNCEMENT, DIALOGUE};
   }

   static {
      for(NotificationCategory notificationCategory : values()) {
         INDEX_MAP.put(notificationCategory.getValue(), notificationCategory);
         JSON_STRUCTURE.add(notificationCategory.getValue(), ImmutableMap.<String, Object>builder().put("value", notificationCategory.getValue()).put("key", notificationCategory.name()).put("text", notificationCategory.getDescription()).put("index", notificationCategory.getValue()).build());
      }

   }
}
