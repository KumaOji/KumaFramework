package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.common.utils.common.CharUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordTypeEnum;
import com.kuma.boot.sensitive.sensitiveword.utils.InnerCharUtils;
import java.util.List;

public class WordCheckIPV4 extends AbstractConditionWordCheck {
   private static final IWordCheck INSTANCE = new WordCheckIPV4();

   public WordCheckIPV4() {
   }

   public static IWordCheck getInstance() {
      return INSTANCE;
   }

   protected Class<? extends IWordCheck> getSensitiveCheckClass() {
      return WordCheckIPV4.class;
   }

   protected String getType() {
      return WordTypeEnum.IPV4.getCode();
   }

   protected boolean isCharCondition(char mappingChar, int index, InnerSensitiveWordContext checkContext) {
      return CharUtils.isNumber(mappingChar) || '.' == mappingChar;
   }

   protected boolean isStringCondition(int index, StringBuilder stringBuilder, InnerSensitiveWordContext checkContext) {
      int bufferLen = stringBuilder.length();
      if (bufferLen >= 7 && bufferLen <= 15) {
         String string = stringBuilder.toString();
         List<String> stringList = StringUtils.splitToList(string, ".");
         if (stringList.size() != 4) {
            return false;
         } else {
            for(String numStr : stringList) {
               int integer = InnerCharUtils.parseInt(numStr);
               if (integer < 0 || integer > 256) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
