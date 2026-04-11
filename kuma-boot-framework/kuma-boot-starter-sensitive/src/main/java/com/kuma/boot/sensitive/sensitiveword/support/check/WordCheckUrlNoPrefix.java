package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.common.utils.common.RegexUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;

public class WordCheckUrlNoPrefix extends WordCheckUrl {
   private static final IWordCheck INSTANCE = new WordCheckUrlNoPrefix();

   public WordCheckUrlNoPrefix() {
   }

   public static IWordCheck getInstance() {
      return INSTANCE;
   }

   protected boolean isUrl(String text) {
      return RegexUtils.isWebSite(text);
   }
}
