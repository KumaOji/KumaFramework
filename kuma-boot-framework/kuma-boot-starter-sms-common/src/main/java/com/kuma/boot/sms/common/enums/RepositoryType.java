package com.kuma.boot.sms.common.enums;

public enum RepositoryType {
   MEMORY,
   REDIS;

   // $FF: synthetic method
   private static RepositoryType[] $values() {
      return new RepositoryType[]{MEMORY, REDIS};
   }
}
