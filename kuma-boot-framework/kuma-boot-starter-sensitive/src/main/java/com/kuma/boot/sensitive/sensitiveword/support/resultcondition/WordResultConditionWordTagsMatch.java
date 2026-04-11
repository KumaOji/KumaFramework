package com.kuma.boot.sensitive.sensitiveword.support.resultcondition;

import com.kuma.boot.common.extension.CollectionUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;
import java.util.Collection;
import java.util.Set;

public class WordResultConditionWordTagsMatch extends AbstractWordResultCondition {
   private final Collection<String> tags;

   public WordResultConditionWordTagsMatch(Collection<String> tags) {
      ArgUtils.notEmpty(tags, "tags");
      this.tags = tags;
   }

   protected boolean doMatch(IWordResult wordResult, String text, WordValidModeEnum modeEnum, IWordContext context) {
      String word = text.substring(wordResult.startIndex(), wordResult.endIndex());
      IWordTag wordTag = context.wordTag();
      Set<String> wordTags = wordTag.getTag(word);
      if (CollectionUtils.isEmpty(wordTags)) {
         return false;
      } else {
         for(String tag : this.tags) {
            if (wordTags.contains(tag)) {
               return true;
            }
         }

         return false;
      }
   }
}
