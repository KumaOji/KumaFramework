package com.kuma.boot.sms.common.loadbalancer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractLoadBalancer implements ILoadBalancer {
   protected final List targetList;

   public AbstractLoadBalancer() {
      this(new CopyOnWriteArrayList());
   }

   public AbstractLoadBalancer(List targetList) {
      this.targetList = targetList;
   }

   public void addTargetWrapper(TargetWrapper wrapper) {
      if (wrapper != null) {
         if (!this.targetList.contains(wrapper)) {
            this.targetList.add(wrapper);
            this.afterAdd(wrapper);
         }

      }
   }

   protected void afterAdd(TargetWrapper wrapper) {
   }

   public void removeTargetWrapper(TargetWrapper wrapper) {
      if (wrapper != null) {
         this.targetList.remove(wrapper);
         this.afterRemove(wrapper);
      }
   }

   protected void afterRemove(TargetWrapper wrapper) {
   }

   public void clear() {
      this.targetList.clear();
   }

   public void setWeight(Object target, int weight) {
   }

   public Object choose(Predicate predicate, Object chooseReferenceObject) {
      List<TargetWrapper<T>> activeTargetList;
      if (predicate == null) {
         activeTargetList = this.targetList.stream().filter(TargetWrapper::isActive).toList();
      } else {
         activeTargetList = this.targetList.stream().filter(TargetWrapper::isActive).filter((wrapper) -> predicate.test(wrapper.getTarget())).toList();
      }

      return activeTargetList.isEmpty() ? null : this.choose0(activeTargetList, chooseReferenceObject);
   }

   protected abstract Object choose0(List activeTargetList, Object chooseReferenceObject);

   public void markReachable(TargetWrapper wrapper) {
      if (wrapper != null) {
         Stream var10000 = this.targetList.stream();
         Objects.requireNonNull(wrapper);
         var10000.filter(wrapper::equals).forEach((item) -> item.setActive(true));
      }
   }

   public void markDown(TargetWrapper wrapper) {
      if (wrapper != null) {
         Stream var10000 = this.targetList.stream();
         Objects.requireNonNull(wrapper);
         var10000.filter(wrapper::equals).forEach((item) -> item.setActive(false));
      }
   }

   public List getTargetWrappers(Boolean active) {
      List<TargetWrapper<T>> wrappers;
      if (active == null) {
         wrappers = this.targetList;
      } else {
         wrappers = this.targetList.stream().filter((wrapper) -> wrapper.isActive() == active).toList();
      }

      return Collections.unmodifiableList(wrappers);
   }

   public List getTargets(Boolean active) {
      List<T> targets;
      if (active == null) {
         targets = this.targetList.stream().map(TargetWrapper::getTarget).toList();
      } else {
         targets = this.targetList.stream().filter((wrapper) -> wrapper.isActive() == active).map(TargetWrapper::getTarget).toList();
      }

      return Collections.unmodifiableList(targets);
   }
}
