package com.kuma.boot.flowengine.module;

import com.google.common.collect.Lists;
import com.kuma.boot.flowengine.exception.FlowException;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ExceptionMapping {
   @NotEmpty
   public List<Class<? extends Throwable>> throwables = Lists.newArrayList();

   public List<Class<? extends Throwable>> getThrowables() {
      return throwables;
   }

   public void addThrowable(String throwable) {
      Class<? extends Throwable> throwableClass;
      try {
         throwableClass = (Class<? extends Throwable>) Class.forName(throwable);
      } catch (ClassNotFoundException e) {
         throw new FlowException(String.format("解析ExceptionMonitor过程出现错误,throwable=%s初始化失败!",throwable),e);
      }

      throwables.add(throwableClass);
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder("ExceptionMapping{");
      sb.append("throwables=").append(throwables);
      sb.append('}');
      return sb.toString();
   }
}

