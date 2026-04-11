package com.kuma.boot.sensitive.sensitiveword.support.deny;

import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordDeny;

public final class WordDenys {
   private WordDenys() {
   }

   public static IWordDeny chains(final IWordDeny wordDeny, final IWordDeny... others) {
      return new WordDenyInit() {
         protected void init(Pipeline<IWordDeny> pipeline) {
            pipeline.addLast(wordDeny);
            if (ArrayUtils.isNotEmpty(others)) {
               for(IWordDeny other : others) {
                  pipeline.addLast(other);
               }
            }

         }
      };
   }

   public static IWordDeny defaults() {
      return WordDenySystem.getInstance();
   }

   public static IWordDeny empty() {
      return new WordDenyEmpty();
   }
}
