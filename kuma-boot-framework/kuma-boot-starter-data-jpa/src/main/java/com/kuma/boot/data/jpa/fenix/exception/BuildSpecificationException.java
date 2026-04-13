package com.kuma.boot.data.jpa.fenix.exception;

public class BuildSpecificationException extends RuntimeException {
   private static final long serialVersionUID = -7791731344371081795L;

   public BuildSpecificationException(String msg) {
      super(msg);
   }

   public BuildSpecificationException(String message, Throwable e) {
      super(message, e);
   }
}
