package com.kuma.boot.data.jpa.fenix.exception;

public class NodeNotFoundException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public NodeNotFoundException(String msg) {
      super(msg);
   }
}
