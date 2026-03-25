package com.kuma.boot.encrypt.encrypt2.handler;

import java.lang.annotation.Annotation;

public interface SecurityHandler {
   Annotation acquire(Annotation[] annotations);

   String handleEncrypt(String source, Annotation annotation);

   String handleDecrypt(String source, Annotation annotation);
}
