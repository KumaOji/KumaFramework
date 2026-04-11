package com.kuma.boot.sensitive.sensitiveword.core;

import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWord;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordReplace;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.xkzhangsan.time.utils.CollectionUtil;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSensitiveWord implements ISensitiveWord {
   public AbstractSensitiveWord() {
   }

   protected abstract List<IWordResult> doFindAll(String string, IWordContext context);

   protected abstract IWordResult doFindFirst(String string, IWordContext context);

   protected String doReplace(String target, List<IWordResult> allList, IWordContext context) {
      IWordReplace replace = context.wordReplace();
      StringBuilder stringBuilder = new StringBuilder();
      int startIndex = 0;
      char[] rawChars = target.toCharArray();

      for(IWordResult wordResult : allList) {
         int itemStartIx = wordResult.startIndex();
         int itemEndIx = wordResult.endIndex();
         if (startIndex < itemStartIx) {
            stringBuilder.append(rawChars, startIndex, itemStartIx - startIndex);
         }

         replace.replace(stringBuilder, rawChars, wordResult, context);
         startIndex = Math.max(startIndex, itemEndIx);
      }

      if (startIndex < rawChars.length) {
         stringBuilder.append(rawChars, startIndex, rawChars.length - startIndex);
      }

      return stringBuilder.toString();
   }

   public List<IWordResult> findAll(String string, IWordContext context) {
      return StringUtils.isEmpty(string) ? Collections.emptyList() : this.doFindAll(string, context);
   }

   public IWordResult findFirst(String string, IWordContext context) {
      return StringUtils.isEmpty(string) ? null : this.doFindFirst(string, context);
   }

   public String replace(String target, IWordContext context) {
      if (StringUtils.isEmpty(target)) {
         return target;
      } else {
         List<IWordResult> allList = this.findAll(target, context);
         return CollectionUtil.isEmpty(allList) ? target : this.doReplace(target, allList, context);
      }
   }

   public boolean contains(String string, IWordContext context) {
      IWordResult firstResult = this.findFirst(string, context);
      return firstResult != null;
   }
}
