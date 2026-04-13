package com.kuma.boot.data.jpa.fenix.exception;

public class FieldEmptyException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public FieldEmptyException(String msg) {
      super(msg);
   }
}
