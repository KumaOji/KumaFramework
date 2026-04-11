package com.kuma.boot.flowengine;

@FunctionalInterface
public interface GlobalRetryExceptionsCustomizer {
   void customize(RetryExceptionRegistry registry);
}
