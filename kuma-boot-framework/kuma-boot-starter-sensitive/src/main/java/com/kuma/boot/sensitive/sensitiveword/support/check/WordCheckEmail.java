package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.common.utils.common.CharUtils;
import com.kuma.boot.common.utils.common.RegexUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordTypeEnum;

public class WordCheckEmail extends AbstractConditionWordCheck {
   private static final IWordCheck INSTANCE = new WordCheckEmail();

   public WordCheckEmail() {
   }

   public static IWordCheck getInstance() {
      return INSTANCE;
   }

   protected Class<? extends IWordCheck> getSensitiveCheckClass() {
      return WordCheckEmail.class;
   }

   protected String getType() {
      return WordTypeEnum.EMAIL.getCode();
   }

   protected boolean isCharCondition(char mappingChar, int index, InnerSensitiveWordContext checkContext) {
      return CharUtils.isEmilChar(mappingChar);
   }

   protected boolean isStringCondition(int index, StringBuilder stringBuilder, InnerSensitiveWordContext checkContext) {
      int bufferLen = stringBuilder.length();
      if (bufferLen < 6) {
         return false;
      } else if (bufferLen > 64) {
         return false;
      } else {
         String string = stringBuilder.toString();
         return RegexUtils.isEmail(string);
      }
   }
}
