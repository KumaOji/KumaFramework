package com.kuma.boot.sms.common.loadbalancer;

import java.util.Objects;

public class TargetWrapper {
   private boolean active;
   private Object target;

   private TargetWrapper() {
   }

   public static TargetWrapper of(Object target) {
      if (target == null) {
         throw new NullPointerException("entity is null");
      } else {
         TargetWrapper<T> wrapper = new TargetWrapper();
         wrapper.target = target;
         return wrapper;
      }
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public Object getTarget() {
      return this.target;
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         TargetWrapper<?> that = (TargetWrapper)o;
         return Objects.equals(this.target, that.target);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.target.hashCode();
   }
}
