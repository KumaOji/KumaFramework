package com.kuma.boot.sensitive.sensitiveword.support.tag;

import com.kuma.boot.core.utils.io.FileStreamUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import java.util.List;
import java.util.Set;

public class WordTagSystem extends AbstractWordTag {
   private final IWordTag wordTag;

   public WordTagSystem() {
      List<String> lines = FileStreamUtils.readAllLines("/sensitive_word_tags.txt");
      this.wordTag = WordTags.lines(lines);
   }

   protected Set<String> doGetTag(String word) {
      return this.wordTag.getTag(word);
   }
}
