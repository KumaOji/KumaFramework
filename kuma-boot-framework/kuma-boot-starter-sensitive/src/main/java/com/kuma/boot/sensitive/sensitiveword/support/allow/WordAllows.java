package com.kuma.boot.sensitive.sensitiveword.support.allow;

import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordAllow;

public final class WordAllows {
   private WordAllows() {
   }

   public static IWordAllow chains(final IWordAllow wordAllow, final IWordAllow... others) {
      return new WordAllowInit() {
         protected void init(Pipeline<IWordAllow> pipeline) {
            pipeline.addLast(wordAllow);
            if (ArrayUtils.isNotEmpty(others)) {
               for(IWordAllow other : others) {
                  pipeline.addLast(other);
               }
            }

         }
      };
   }

   public static IWordAllow defaults() {
      return WordAllowSystem.getInstance();
   }

   public static IWordAllow empty() {
      return new WordAllowEmpty();
   }
}
