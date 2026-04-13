package com.kuma.boot.data.jpa.fenix.consts;

public enum LikeTypeEnum {
   STARTS_WITH,
   ENDS_WITH;

   private LikeTypeEnum() {
   }

   // $FF: synthetic method
   private static LikeTypeEnum[] $values() {
      return new LikeTypeEnum[]{STARTS_WITH, ENDS_WITH};
   }
}
