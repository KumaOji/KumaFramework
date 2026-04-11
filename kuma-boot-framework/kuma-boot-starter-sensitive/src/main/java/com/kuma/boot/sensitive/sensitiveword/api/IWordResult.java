package com.kuma.boot.sensitive.sensitiveword.api;

public interface IWordResult {
   int startIndex();

   int endIndex();

   String type();

   String word();
}
