package com.kuma.boot.sensitive.sensitiveword.api;

import java.util.List;

public interface ISensitiveWord {
   List<IWordResult> findAll(final String string, final IWordContext context);

   IWordResult findFirst(final String string, final IWordContext context);

   String replace(final String target, final IWordContext context);

   boolean contains(final String string, final IWordContext context);
}
