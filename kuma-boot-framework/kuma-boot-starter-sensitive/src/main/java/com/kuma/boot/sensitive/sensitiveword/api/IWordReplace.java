package com.kuma.boot.sensitive.sensitiveword.api;

public interface IWordReplace {
   void replace(final StringBuilder stringBuilder, final char[] rawChars, final IWordResult wordResult, final IWordContext wordContext);
}
