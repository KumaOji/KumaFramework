package com.kuma.boot.sms.common.loadbalancer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface ILoadBalancer {
   int MIN_WEIGHT = 1;

   void addTargetWrapper(TargetWrapper wrapper);

   default void addTarget(Object target, Boolean initActive) {
      if (target != null) {
         TargetWrapper<T> wrapper = TargetWrapper.of(target);
         if (initActive != null) {
            wrapper.setActive(initActive);
         }

         this.addTargetWrapper(wrapper);
      }
   }

   default void addTarget(Object target) {
      this.addTarget(target, (Boolean)null);
   }

   default void addTargetWrappers(Collection wrappers) {
      if (wrappers != null && !wrappers.isEmpty()) {
         wrappers.forEach(this::addTargetWrapper);
      }
   }

   default void addTargets(Collection targets, Boolean initActive) {
      if (targets != null && !targets.isEmpty()) {
         Collection<TargetWrapper<T>> wrappers = new ArrayList(targets.size());

         for(Object target : targets) {
            if (target != null) {
               TargetWrapper<T> wrapper = TargetWrapper.of(target);
               if (initActive != null) {
                  wrapper.setActive(initActive);
               }

               wrappers.add(wrapper);
            }
         }

         this.addTargetWrappers(wrappers);
      }
   }

   default void addTargets(Collection targets) {
      this.addTargets(targets, (Boolean)null);
   }

   void removeTargetWrapper(TargetWrapper wrapper);

   default void removeTarget(Object target) {
      if (target != null) {
         this.removeTargetWrapper(TargetWrapper.of(target));
      }
   }

   default void removeTargetWrappers(Collection wrappers) {
      if (wrappers != null && !wrappers.isEmpty()) {
         wrappers.forEach(this::removeTargetWrapper);
      }
   }

   default void removeTargets(Collection targets) {
      if (targets != null && !targets.isEmpty()) {
         Collection<TargetWrapper<T>> wrappers = targets.stream().filter(Objects::nonNull).map(TargetWrapper::of).toList();
         this.removeTargetWrappers(wrappers);
      }
   }

   void clear();

   void setWeight(Object target, int weight);

   Object choose(Predicate predicate, Object chooseReferenceObject);

   default Object choose(Object chooseReferenceObject) {
      return this.choose((Predicate)null, chooseReferenceObject);
   }

   default Object choose() {
      return this.choose((Predicate)null, (Object)null);
   }

   void markReachable(TargetWrapper wrapper);

   default void markReachable(Object target) {
      if (target != null) {
         this.markReachable(TargetWrapper.of(target));
      }
   }

   void markDown(TargetWrapper wrapper);

   default void markDown(Object target) {
      if (target != null) {
         this.markDown(TargetWrapper.of(target));
      }
   }

   List getTargetWrappers(Boolean active);

   List getTargets(Boolean active);
}
