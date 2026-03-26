package com.kuma.boot.sms.common.loadbalancer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface ILoadBalancer<T> {
   int MIN_WEIGHT = 1;

   void addTargetWrapper(TargetWrapper<T> wrapper);

   default void addTarget(T target, Boolean initActive) {
      if (target != null) {
         TargetWrapper<T> wrapper = TargetWrapper.of(target);
         if (initActive != null) {
            wrapper.setActive(initActive);
         }

         this.addTargetWrapper(wrapper);
      }
   }

   default void addTarget(T target) {
      this.addTarget(target, (Boolean) null);
   }

   default void addTargetWrappers(Collection<TargetWrapper<T>> wrappers) {
      if (wrappers != null && !wrappers.isEmpty()) {
         wrappers.forEach(this::addTargetWrapper);
      }
   }

   default void addTargets(Collection<T> targets, Boolean initActive) {
      if (targets != null && !targets.isEmpty()) {
         Collection<TargetWrapper<T>> wrappers = new ArrayList<>(targets.size());

         for (T target : targets) {
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

   default void addTargets(Collection<T> targets) {
      this.addTargets(targets, (Boolean) null);
   }

   void removeTargetWrapper(TargetWrapper<T> wrapper);

   default void removeTarget(T target) {
      if (target != null) {
         this.removeTargetWrapper(TargetWrapper.of(target));
      }
   }

   default void removeTargetWrappers(Collection<TargetWrapper<T>> wrappers) {
      if (wrappers != null && !wrappers.isEmpty()) {
         wrappers.forEach(this::removeTargetWrapper);
      }
   }

   default void removeTargets(Collection<T> targets) {
      if (targets != null && !targets.isEmpty()) {
         Collection<TargetWrapper<T>> wrappers = targets.stream().filter(Objects::nonNull).map(TargetWrapper::of).toList();
         this.removeTargetWrappers(wrappers);
      }
   }

   void clear();

   void setWeight(T target, int weight);

   T choose(Predicate<T> predicate, Object chooseReferenceObject);

   default T choose(Object chooseReferenceObject) {
      return this.choose((Predicate<T>) null, chooseReferenceObject);
   }

   default T choose() {
      return this.choose((Predicate<T>) null, (Object) null);
   }

   void markReachable(TargetWrapper<T> wrapper);

   default void markReachable(T target) {
      if (target != null) {
         this.markReachable(TargetWrapper.of(target));
      }
   }

   void markDown(TargetWrapper<T> wrapper);

   default void markDown(T target) {
      if (target != null) {
         this.markDown(TargetWrapper.of(target));
      }
   }

   List<TargetWrapper<T>> getTargetWrappers(Boolean active);

   List<T> getTargets(Boolean active);
}
