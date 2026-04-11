package com.kuma.boot.sensitive.sensitivelog.api;

public interface Strategy {
   Object des(final Object original, final Context context);
}
