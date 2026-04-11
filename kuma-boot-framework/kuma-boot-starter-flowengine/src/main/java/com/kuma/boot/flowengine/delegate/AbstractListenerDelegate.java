package com.kuma.boot.flowengine.delegate;

public abstract class AbstractListenerDelegate implements ListenerDelegate, Comparable {
   private int priority;
   private Object target;

   public AbstractListenerDelegate(Object target, int priority) {
      this.target = target;
      this.priority = priority;
   }

   public Object getTarget() {
      return this.target;
   }

   public int compareTo(Object o) {
      AbstractListenerDelegate other = (AbstractListenerDelegate)o;
      return this.priority == other.priority ? 0 : (this.priority > other.priority ? 1 : -1);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AbstractListenerDelegate that = (AbstractListenerDelegate)o;
         if (this.target != null) {
            if (!this.target.equals(that.target)) {
               return false;
            }
         } else if (that.target != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.target != null ? this.target.hashCode() : 0;
   }
}
