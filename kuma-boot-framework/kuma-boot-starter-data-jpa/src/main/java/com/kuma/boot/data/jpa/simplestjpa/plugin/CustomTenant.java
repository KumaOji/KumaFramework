package com.kuma.boot.data.jpa.simplestjpa.plugin;

public abstract class CustomTenant implements TenantFactory {
   public static Boolean withoutTenantCondition;

   public CustomTenant() {
   }

   static {
      withoutTenantCondition = Boolean.FALSE;
   }
}
