package com.kuma.boot.data.jpa.fenix.exception;

public class XmlParseException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public XmlParseException(String msg, Throwable t) {
      super(msg, t);
   }
}
