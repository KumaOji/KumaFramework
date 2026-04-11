package com.kuma.boot.sensitive.sensitiveword.support.allow;

import com.kuma.boot.common.utils.io.FileStreamUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordAllow;
import java.util.List;

public class WordAllowSystem implements IWordAllow {
   private static final WordAllowSystem INSTANCE = new WordAllowSystem();

   public WordAllowSystem() {
   }

   public static WordAllowSystem getInstance() {
      return INSTANCE;
   }

   public List<String> allow() {
      return FileStreamUtils.readAllLines("/sensitive_word_allow.txt");
   }
}
