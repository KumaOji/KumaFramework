package com.kuma.boot.sensitive.sensitiveword.support.tag;

import com.kuma.boot.common.support.pipeline.Pipeline;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class WordTags {
   public WordTags() {
   }

   public static IWordTag none() {
      return new NoneWordTag();
   }

   public static IWordTag file(String filePath) {
      return new FileWordTag(filePath);
   }

   public static IWordTag file(String filePath, final String wordSplit, final String tagSplit) {
      return new FileWordTag(filePath, wordSplit, tagSplit);
   }

   public static IWordTag map(final Map<String, Set<String>> wordTagMap) {
      return new WordTagMap(wordTagMap);
   }

   public static IWordTag lines(final Collection<String> lines) {
      return new WordTagLines(lines);
   }

   public static IWordTag lines(final Collection<String> lines, final String wordSplit, final String tagSplit) {
      return new WordTagLines(lines, wordSplit, tagSplit);
   }

   public static IWordTag system() {
      return new WordTagSystem();
   }

   public static IWordTag defaults() {
      return system();
   }

   public static IWordTag chains(final IWordTag wordTag, final IWordTag... others) {
      ArgUtils.notNull(wordTag, "wordTag");
      return new AbstractWordTagInit() {
         protected void init(Pipeline<IWordTag> pipeline) {
            pipeline.addLast(wordTag);
            if (ArrayUtils.isNotEmpty(others)) {
               for(IWordTag other : others) {
                  pipeline.addLast(other);
               }
            }

         }
      };
   }
}
