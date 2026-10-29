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
   title = "通知类别"
)
@JsonFormat(
   shape = Shape.OBJECT
)
public enum NotificationCategory {
   ANNOUNCEMENT(0, "系统公告"),
   DIALOGUE(1, "私信");

   @Schema(
      title = "枚举值"
   )
   private final Integer value;
   @Schema(
      title = "说明"
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
