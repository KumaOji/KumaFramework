package com.kuma.boot.sms.common.loadbalancer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public abstract class AbstractLoadBalancer<T> implements ILoadBalancer<T> {
   protected final List<TargetWrapper<T>> targetList;

   public AbstractLoadBalancer() {
      this(new CopyOnWriteArrayList<>());
   }

   public AbstractLoadBalancer(List<TargetWrapper<T>> targetList) {
      this.targetList = targetList;
   }

   public void addTargetWrapper(TargetWrapper<T> wrapper) {
      if (wrapper != null) {
         if (!this.targetList.contains(wrapper)) {
            this.targetList.add(wrapper);
            this.afterAdd(wrapper);
         }

      }
   }

   protected void afterAdd(TargetWrapper<T> wrapper) {
   }

   public void removeTargetWrapper(TargetWrapper<T> wrapper) {
      if (wrapper != null) {
         this.targetList.remove(wrapper);
         this.afterRemove(wrapper);
      }
   }

   protected void afterRemove(TargetWrapper<T> wrapper) {
   }

   public void clear() {
      this.targetList.clear();
   }

   public void setWeight(T target, int weight) {
   }

   public T choose(Predicate<T> predicate, Object chooseReferenceObject) {
      List<TargetWrapper<T>> activeTargetList;
      if (predicate == null) {
         activeTargetList = this.targetList.stream().filter(TargetWrapper::isActive).toList();
      } else {
         activeTargetList = this.targetList.stream().filter(TargetWrapper::isActive).filter((wrapper) -> predicate.test(wrapper.getTarget())).toList();
      }

      return activeTargetList.isEmpty() ? null : this.choose0(activeTargetList, chooseReferenceObject);
   }

   protected abstract T choose0(List<TargetWrapper<T>> activeTargetList, Object chooseReferenceObject);

   public void markReachable(TargetWrapper<T> wrapper) {
      if (wrapper != null) {
         this.targetList.stream()
            .filter(wrapper::equals)
            .forEach((item) -> item.setActive(true));
      }
   }

   public void markDown(TargetWrapper<T> wrapper) {
      if (wrapper != null) {
         this.targetList.stream()
            .filter(wrapper::equals)
            .forEach((item) -> item.setActive(false));
      }
   }

   public List<TargetWrapper<T>> getTargetWrappers(Boolean active) {
      List<TargetWrapper<T>> wrappers;
      if (active == null) {
         wrappers = this.targetList;
      } else {
         wrappers = this.targetList.stream().filter((wrapper) -> wrapper.isActive() == active).toList();
      }

      return Collections.unmodifiableList(wrappers);
   }

   public List<T> getTargets(Boolean active) {
      List<T> targets;
      if (active == null) {
         targets = this.targetList.stream().map(TargetWrapper::getTarget).toList();
      } else {
         targets = this.targetList.stream().filter((wrapper) -> wrapper.isActive() == active).map(TargetWrapper::getTarget).toList();
      }

      return Collections.unmodifiableList(targets);
   }
}
