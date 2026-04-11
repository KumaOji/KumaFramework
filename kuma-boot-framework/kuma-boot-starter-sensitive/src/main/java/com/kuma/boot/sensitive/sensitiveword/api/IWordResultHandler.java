package com.kuma.boot.sensitive.sensitiveword.api;

public interface IWordResultHandler<R> {
   R handle(final IWordResult wordResult, final IWordContext wordContext, final String originalText);
}
