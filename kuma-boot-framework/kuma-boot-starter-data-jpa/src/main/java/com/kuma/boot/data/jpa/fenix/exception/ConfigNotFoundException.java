package com.kuma.boot.data.jpa.fenix.exception;

public class ConfigNotFoundException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public ConfigNotFoundException(String msg) {
      super(msg);
   }
}
