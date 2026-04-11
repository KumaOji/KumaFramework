package com.kuma.boot.xss.xsssupport;

public interface XssException {
   default String getName() {
      return null;
   }

   String getInput();

   String getMessage();
}
