package com.kuma.boot.flowengine.module;

import com.google.common.collect.Lists;
import com.kuma.boot.flowengine.exception.FlowException;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class ExceptionMapping {
   public @NotEmpty List<Class<? extends Throwable>> throwables = Lists.newArrayList();

   public ExceptionMapping() {
   }

   public List<Class<? extends Throwable>> getThrowables() {
      return this.throwables;
   }

   public void addThrowable(String throwable) {
      Class<? extends Throwable> throwableClass;
      try {
         throwableClass = Class.forName(throwable);
      } catch (ClassNotFoundException e) {
         throw new FlowException(String.format("\u89e3\u6790ExceptionMonitor\u8fc7\u7a0b\u51fa\u73b0\u9519\u8bef,throwable=%s\u521d\u59cb\u5316\u5931\u8d25!", throwable), e);
      }

      this.throwables.add(throwableClass);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("ExceptionMapping{");
      sb.append("throwables=").append(this.throwables);
      sb.append('}');
      return sb.toString();
   }
}
