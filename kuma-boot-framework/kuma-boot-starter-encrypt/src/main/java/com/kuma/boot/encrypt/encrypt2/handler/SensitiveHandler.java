package com.kuma.boot.encrypt.encrypt2.handler;

import java.lang.annotation.Annotation;

public interface SensitiveHandler {
   Annotation acquire(Annotation[] annotations);

   String format(String source, Annotation annotation);
}
