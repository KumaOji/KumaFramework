package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordTypeEnum;

public class WordCheckNum extends AbstractConditionWordCheck {
   private static final IWordCheck INSTANCE = new WordCheckNum();

   public WordCheckNum() {
   }

   public static IWordCheck getInstance() {
      return INSTANCE;
   }

   protected Class<? extends IWordCheck> getSensitiveCheckClass() {
      return WordCheckNum.class;
   }

   protected String getType() {
      return WordTypeEnum.NUM.getCode();
   }

   protected boolean isCharCondition(char mappingChar, int index, InnerSensitiveWordContext checkContext) {
      return Character.isDigit(mappingChar);
   }

   protected boolean isStringCondition(int index, StringBuilder stringBuilder, InnerSensitiveWordContext checkContext) {
      int bufferLen = stringBuilder.length();
      return bufferLen >= checkContext.wordContext().sensitiveCheckNumLen();
   }
}
