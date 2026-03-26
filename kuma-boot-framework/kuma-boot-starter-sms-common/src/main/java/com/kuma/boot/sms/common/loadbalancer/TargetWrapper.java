package com.kuma.boot.sms.common.loadbalancer;

import java.util.Objects;

public class TargetWrapper<T> {
   private boolean active;
   private T target;

   private TargetWrapper() {
   }

   public static <T> TargetWrapper<T> of(T target) {
      if (target == null) {
         throw new NullPointerException("entity is null");
      } else {
         TargetWrapper<T> wrapper = new TargetWrapper<>();
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

   public T getTarget() {
      return this.target;
   }

   @Override
   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         TargetWrapper<?> that = (TargetWrapper<?>) o;
         return Objects.equals(this.target, that.target);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.target.hashCode();
   }
}
