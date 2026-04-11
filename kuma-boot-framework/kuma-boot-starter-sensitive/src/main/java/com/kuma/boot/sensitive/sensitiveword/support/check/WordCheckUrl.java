package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.common.utils.common.CharUtils;
import com.kuma.boot.common.utils.common.RegexUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordTypeEnum;

public class WordCheckUrl extends AbstractConditionWordCheck {
   private static final IWordCheck INSTANCE = new WordCheckUrl();

   public WordCheckUrl() {
   }

   public static IWordCheck getInstance() {
      return INSTANCE;
   }

   protected Class<? extends IWordCheck> getSensitiveCheckClass() {
      return WordCheckUrl.class;
   }

   protected String getType() {
      return WordTypeEnum.URL.getCode();
   }

   protected boolean isCharCondition(char mappingChar, int index, InnerSensitiveWordContext checkContext) {
      return CharUtils.isWebSiteChar(mappingChar) || mappingChar == ':' || mappingChar == '/';
   }

   protected boolean isStringCondition(int index, StringBuilder stringBuilder, InnerSensitiveWordContext checkContext) {
      int bufferLen = stringBuilder.length();
      if (bufferLen < 4) {
         return false;
      } else if (bufferLen > 70) {
         return false;
      } else {
         String string = stringBuilder.toString();
         return this.isUrl(string);
      }
   }

   protected boolean isUrl(final String text) {
      return RegexUtils.isUrl(text);
   }
}
