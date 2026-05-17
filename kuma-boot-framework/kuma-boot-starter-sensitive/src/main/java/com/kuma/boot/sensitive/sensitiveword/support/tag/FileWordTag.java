package com.kuma.boot.sensitive.sensitiveword.support.tag;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.core.utils.io.FileUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import java.util.List;
import java.util.Set;

public class FileWordTag extends AbstractWordTag {
   protected final IWordTag wordTag;

   public FileWordTag(String filePath) {
      this(filePath, " ", ",");
   }

   public FileWordTag(String filePath, String wordSplit, String tagSplit) {
      ArgUtils.notEmpty(filePath, "filePath");
      ArgUtils.notEmpty(wordSplit, "wordSplit");
      ArgUtils.notEmpty(tagSplit, "tagSplit");
      List<String> lines = FileUtils.readAllLines(filePath);
      this.wordTag = WordTags.lines(lines, wordSplit, tagSplit);
   }

   protected Set<String> doGetTag(String word) {
      return this.wordTag.getTag(word);
   }
}
