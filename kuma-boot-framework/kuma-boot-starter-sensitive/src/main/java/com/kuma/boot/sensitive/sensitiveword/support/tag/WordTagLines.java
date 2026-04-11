package com.kuma.boot.sensitive.sensitiveword.support.tag;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordTagLines extends AbstractWordTag {
   private final IWordTag wordTag;
   private final String wordSplit;
   private final String tagSplit;

   public WordTagLines(Collection<String> lines, final String wordSplit, final String tagSplit) {
      ArgUtils.notNull(lines, "lines");
      ArgUtils.notEmpty(wordSplit, "wordSplit");
      ArgUtils.notEmpty(tagSplit, "tagSplit");
      this.wordSplit = wordSplit;
      this.tagSplit = tagSplit;
      Map<String, Set<String>> wordTagMap = this.buildWordTagMap(lines);
      this.wordTag = WordTags.map(wordTagMap);
   }

   public WordTagLines(Collection<String> lines) {
      this(lines, " ", ",");
   }

   private Map<String, Set<String>> buildWordTagMap(final Collection<String> lines) {
      Map<String, Set<String>> wordTagMap = new HashMap();

      for(String line : lines) {
         String[] strings = line.split(this.wordSplit);
         String key = strings[0];
         Set<String> tags = new HashSet(StringUtils.splitToList(strings[1], this.tagSplit));
         wordTagMap.put(key, tags);
      }

      return wordTagMap;
   }

   protected Set<String> doGetTag(String word) {
      return this.wordTag.getTag(word);
   }
}
