package com.kuma.boot.sensitive.sensitivewords;

import java.io.Serializable;
import java.util.TreeSet;

public class SensitiveWordsNode implements Serializable {
   protected final int headTwoCharMix;
   protected final TreeSet<StringPointer> words = new TreeSet();
   protected SensitiveWordsNode next;

   public SensitiveWordsNode(int headTwoCharMix) {
      this.headTwoCharMix = headTwoCharMix;
   }

   public SensitiveWordsNode(int headTwoCharMix, SensitiveWordsNode parent) {
      this.headTwoCharMix = headTwoCharMix;
      parent.next = this;
   }
}
