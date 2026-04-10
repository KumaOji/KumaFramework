package com.kuma.boot.test.junitperf.support.exception;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.MAINTAINED
)
public class JunitPerfException extends Exception {
   private static final long serialVersionUID = 8241804257890026060L;

   public JunitPerfException() {
   }

   public JunitPerfException(String message) {
      super(message);
   }

   public JunitPerfException(String message, Throwable cause) {
      super(message, cause);
   }

   public JunitPerfException(Throwable cause) {
      super(cause);
   }

   public JunitPerfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}
