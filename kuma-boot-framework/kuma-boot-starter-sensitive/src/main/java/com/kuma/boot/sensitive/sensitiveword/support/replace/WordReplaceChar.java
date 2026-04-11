package com.kuma.boot.sensitive.sensitiveword.support.replace;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.IWordReplace;
import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;

public class WordReplaceChar implements IWordReplace {
   private final char replaceChar;

   public WordReplaceChar(char replaceChar) {
      this.replaceChar = replaceChar;
   }

   public WordReplaceChar() {
      this('*');
   }

   public void replace(StringBuilder stringBuilder, final char[] rawChars, IWordResult wordResult, IWordContext wordContext) {
      int wordLen = wordResult.endIndex() - wordResult.startIndex();

      for(int i = 0; i < wordLen; ++i) {
         stringBuilder.append(this.replaceChar);
      }

   }
}
