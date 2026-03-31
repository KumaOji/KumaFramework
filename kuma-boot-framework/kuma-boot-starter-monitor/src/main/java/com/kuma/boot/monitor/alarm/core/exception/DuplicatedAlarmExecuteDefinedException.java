package com.kuma.boot.monitor.alarm.core.exception;

public class DuplicatedAlarmExecuteDefinedException extends RuntimeException {
   public DuplicatedAlarmExecuteDefinedException(String message) {
      super(message);
   }

   public synchronized Throwable fillInStackTrace() {
      return this;
   }
}
