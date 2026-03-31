package com.kuma.boot.monitor.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum WarnLevelEnum {
   HIGN(3, "\u6781\u5176\u4e25\u91cd"),
   MIDDLE(2, "\u4e25\u91cd"),
   LOW(1, "\u4e00\u822c");

   private int level = 1;
   private String description;
   private static final Map<Integer, WarnLevelEnum> valueLookup = new ConcurrentHashMap(values().length);

   public String getDescription() {
      return this.description;
   }

   public int getLevel() {
      return this.level;
   }

   private WarnLevelEnum(int level, String description) {
      this.description = description;
      this.level = level;
   }

   public static WarnLevelEnum resolve(Integer code) {
      return code != null ? (WarnLevelEnum)valueLookup.get(code) : null;
   }

   public static String resolveName(Integer code) {
      WarnLevelEnum mode = resolve(code);
      return mode == null ? "" : mode.getDescription();
   }

   // $FF: synthetic method
   private static WarnLevelEnum[] $values() {
      return new WarnLevelEnum[]{HIGN, MIDDLE, LOW};
   }

   static {
      for(WarnLevelEnum type : EnumSet.allOf(WarnLevelEnum.class)) {
         valueLookup.put(type.level, type);
      }

   }
}
