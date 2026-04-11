package com.kuma.boot.sensitive.sensitiveword.support.tag;

import com.kuma.boot.common.utils.common.ArgUtils;
import java.util.Map;
import java.util.Set;

public class WordTagMap extends AbstractWordTag {
   private final Map<String, Set<String>> wordTagMap;

   public WordTagMap(Map<String, Set<String>> wordTagMap) {
      ArgUtils.notNull(wordTagMap, "wordTagMap");
      this.wordTagMap = wordTagMap;
   }

   protected Set<String> doGetTag(String word) {
      return (Set)this.wordTagMap.get(word);
   }
}
