package com.kuma.boot.sensitive.sensitiveword.support.result;

import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerWordCharUtils;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerWordTagUtils;
import java.util.Set;

public class WordResultHandlerWordTags extends AbstractWordResultHandler<WordTagsDto> {
   public WordResultHandlerWordTags() {
   }

   protected WordTagsDto doHandle(IWordResult wordResult, IWordContext wordContext, String originalText) {
      WordTagsDto dto = new WordTagsDto();
      String word = InnerWordCharUtils.getString(originalText.toCharArray(), wordResult);
      Set<String> wordTags = InnerWordTagUtils.tags(word, wordContext);
      if (CollectionUtils.isEmpty(wordTags)) {
         wordTags = InnerWordTagUtils.tags(wordResult.word(), wordContext);
      }

      dto.setWord(word);
      dto.setTags(wordTags);
      return dto;
   }
}
