package com.kuma.boot.test.junitperf.support.exception;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.MAINTAINED
)
public class JunitPerfRuntimeException extends RuntimeException {
   private static final long serialVersionUID = 7067503448534895751L;

   public JunitPerfRuntimeException() {
   }

   public JunitPerfRuntimeException(String message) {
      super(message);
   }

   public JunitPerfRuntimeException(String message, Throwable cause) {
      super(message, cause);
   }

   public JunitPerfRuntimeException(Throwable cause) {
      super(cause);
   }

   public JunitPerfRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}
