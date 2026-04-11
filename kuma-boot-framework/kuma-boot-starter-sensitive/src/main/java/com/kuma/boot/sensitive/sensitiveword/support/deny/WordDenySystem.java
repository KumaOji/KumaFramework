package com.kuma.boot.sensitive.sensitiveword.support.deny;

import com.kuma.boot.sensitive.sensitiveword.api.IWordDeny;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerStreamUtils;
import java.util.List;

public class WordDenySystem implements IWordDeny {
   private static final IWordDeny INSTANCE = new WordDenySystem();

   public WordDenySystem() {
   }

   public static IWordDeny getInstance() {
      return INSTANCE;
   }

   public List<String> deny() {
      List<String> results = InnerStreamUtils.readAllLines("/sensitive_word_dict.txt");
      results.addAll(InnerStreamUtils.readAllLines("/sensitive_word_dict_en.txt"));
      results.addAll(InnerStreamUtils.readAllLines("/sensitive_word_deny.txt"));
      return results;
   }
}
