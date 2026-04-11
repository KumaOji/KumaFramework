package com.kuma.boot.sensitive.sensitiveword.utils;

import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import java.util.Set;

public class InnerWordTagUtils {
   public InnerWordTagUtils() {
   }

   public static Set<String> tags(final String word, final IWordContext wordContext) {
      if (StringUtils.isEmpty(word)) {
         return null;
      } else {
         IWordTag wordTag = wordContext.wordTag();
         Set<String> actualSet = wordTag.getTag(word);
         if (CollectionUtils.isNotEmpty(actualSet)) {
            return actualSet;
         } else {
            String formatWord = InnerWordFormatUtils.format(word, wordContext);
            return wordContext.wordTag().getTag(formatWord);
         }
      }
   }
}
