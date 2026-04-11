package com.kuma.boot.sensitive.desensitize.enums;

import java.util.function.Function;

public enum SensitiveStrategy {
   USERNAME((s) -> s.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),
   ID_CARD((s) -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2")),
   PHONE((s) -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),
   EMAIL((s) -> s.replaceAll("(^\\w)[^@]*(@.*$)", "$1****$2")),
   NAME((s) -> s.replaceAll("^(.{3}).+(.{3})$", "$1*****$2")),
   ADDRESS((s) -> s.replaceAll("(\\S{3})\\S{2}(\\S*)\\S{2}", "$1****$2****"));

   private final Function<String, String> desensitizer;

   private SensitiveStrategy(Function<String, String> desensitizer) {
      this.desensitizer = desensitizer;
   }

   public Function<String, String> desensitizer() {
      return this.desensitizer;
   }

   // $FF: synthetic method
   private static SensitiveStrategy[] $values() {
      return new SensitiveStrategy[]{USERNAME, ID_CARD, PHONE, EMAIL, NAME, ADDRESS};
   }
}
