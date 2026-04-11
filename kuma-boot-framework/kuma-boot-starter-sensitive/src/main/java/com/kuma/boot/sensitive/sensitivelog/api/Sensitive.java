package com.kuma.boot.sensitive.sensitivelog.api;

public interface Sensitive<T> {
   T desCopy(final T object, final SensitiveConfig config);

   String desJson(final T object, final SensitiveConfig config);
}
