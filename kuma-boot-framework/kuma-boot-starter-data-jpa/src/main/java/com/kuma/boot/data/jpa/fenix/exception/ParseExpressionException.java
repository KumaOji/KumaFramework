package com.kuma.boot.data.jpa.fenix.exception;

public class ParseExpressionException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public ParseExpressionException(String msg, Throwable t) {
      super(msg, t);
   }
}
