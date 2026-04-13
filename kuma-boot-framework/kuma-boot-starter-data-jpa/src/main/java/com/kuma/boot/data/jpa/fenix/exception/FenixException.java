package com.kuma.boot.data.jpa.fenix.exception;

import com.kuma.boot.data.jpa.fenix.helper.StringHelper;

public class FenixException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public FenixException(String msg) {
      super(msg);
   }

   public FenixException(String msg, Throwable t) {
      super(msg, t);
   }

   public FenixException(Throwable t, String msg, Object... args) {
      super(StringHelper.format(msg, args), t);
   }
}
