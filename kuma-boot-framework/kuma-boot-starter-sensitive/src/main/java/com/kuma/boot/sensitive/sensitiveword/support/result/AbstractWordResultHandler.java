package com.kuma.boot.sensitive.sensitiveword.support.result;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultHandler;

public abstract class AbstractWordResultHandler<R> implements IWordResultHandler<R> {
   public AbstractWordResultHandler() {
   }

   protected abstract R doHandle(IWordResult wordResult, IWordContext wordContext, String originalText);

   public R handle(IWordResult wordResult, IWordContext wordContext, String originalText) {
      return (R)(wordResult == null ? null : this.doHandle(wordResult, wordContext, originalText));
   }
}
