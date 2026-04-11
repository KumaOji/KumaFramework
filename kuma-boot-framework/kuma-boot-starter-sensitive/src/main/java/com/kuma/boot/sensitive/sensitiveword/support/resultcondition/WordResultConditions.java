package com.kuma.boot.sensitive.sensitiveword.support.resultcondition;

import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResultCondition;
import java.util.List;

public final class WordResultConditions {
   public WordResultConditions() {
   }

   public static IWordResultCondition alwaysTrue() {
      return new WordResultConditionAlwaysTrue();
   }

   public static IWordResultCondition englishWordMatch() {
      return new WordResultConditionEnglishWordMatch();
   }

   public static IWordResultCondition englishWordNumMatch() {
      return new WordResultConditionEnglishWordNumMatch();
   }

   public static IWordResultCondition wordTags(List<String> tags) {
      ArgUtils.notEmpty(tags, "tags");
      return new WordResultConditionWordTagsMatch(tags);
   }

   public static IWordResultCondition chains(final IWordResultCondition condition, final IWordResultCondition... others) {
      return new WordResultConditionInit() {
         protected void init(Pipeline<IWordResultCondition> pipeline) {
            pipeline.addLast(condition);
            if (ArrayUtils.isNotEmpty(others)) {
               for(IWordResultCondition other : others) {
                  pipeline.addLast(other);
               }
            }

         }
      };
   }
}
