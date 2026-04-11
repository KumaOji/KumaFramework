package com.kuma.boot.flowengine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;

public class RetryExceptionRegistry {
   private Set<Class<? extends Throwable>> retryExceptions;

   public RetryExceptionRegistry(Set<Class<? extends Throwable>> retryExceptions) {
      this.retryExceptions = retryExceptions;
   }

   public void register(Class<? extends Throwable>... throwables) {
      if (ArrayUtils.isNotEmpty(throwables)) {
         this.retryExceptions.addAll((Collection)Arrays.stream(throwables).collect(Collectors.toList()));
      }

   }
}
