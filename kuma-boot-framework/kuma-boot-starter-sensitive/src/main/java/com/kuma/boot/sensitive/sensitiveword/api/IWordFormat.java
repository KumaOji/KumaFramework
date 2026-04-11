package com.kuma.boot.sensitive.sensitiveword.api;

public interface IWordFormat {
   char format(final char original, final IWordContext context);
}
