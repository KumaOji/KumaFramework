package com.kuma.boot.data.jpa.tenant;

public enum MultiTenantApproach {
   DISCRIMINATOR,
   SCHEMA,
   DATABASE;

   private MultiTenantApproach() {
   }

   // $FF: synthetic method
   private static MultiTenantApproach[] $values() {
      return new MultiTenantApproach[]{DISCRIMINATOR, SCHEMA, DATABASE};
   }
}
