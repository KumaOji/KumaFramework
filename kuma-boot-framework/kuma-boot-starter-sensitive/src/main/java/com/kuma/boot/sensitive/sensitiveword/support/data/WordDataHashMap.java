package com.kuma.boot.sensitive.sensitiveword.support.data;

import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordContainsTypeEnum;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** @deprecated */
@Deprecated
public class WordDataHashMap extends AbstractWordData {
   private Map innerWordMap;

   public WordDataHashMap() {
   }

   public synchronized void doInitWordData(Collection<String> collection) {
      Map newInnerWordMap = new HashMap(collection.size());

      for(String key : collection) {
         if (!StringUtils.isEmpty(key)) {
            char[] chars = key.toCharArray();
            int size = chars.length;
            Map currentMap = newInnerWordMap;

            for(int i = 0; i < size; ++i) {
               char charKey = chars[i];
               Object wordMap = currentMap.get(charKey);
               if (ObjectUtils.isNotNull(wordMap)) {
                  currentMap = (Map)wordMap;
               } else {
                  Map<String, Boolean> newWordMap = new HashMap(8);
                  newWordMap.put("ED", false);
                  currentMap.put(charKey, newWordMap);
                  currentMap = newWordMap;
               }
            }

            currentMap.put("ED", true);
         }
      }

      this.innerWordMap = newInnerWordMap;
   }

   protected void doRemoveWord(Collection<String> collection) {
   }

   protected void doAddWord(Collection<String> collection) {
   }

   public WordContainsTypeEnum doContains(final StringBuilder stringBuilder, final InnerSensitiveWordContext innerContext) {
      return this.innerContainsSensitive(stringBuilder, innerContext);
   }

   private WordContainsTypeEnum innerContainsSensitive(StringBuilder stringBuilder, final InnerSensitiveWordContext innerContext) {
      Map nowMap = this.innerWordMap;
      int len = stringBuilder.length();

      for(int i = 0; i < len; ++i) {
         nowMap = this.getNowMap(nowMap, i, stringBuilder, innerContext);
         if (ObjectUtils.isNull(nowMap)) {
            return WordContainsTypeEnum.NOT_FOUND;
         }
      }

      boolean isEnd = isEnd(nowMap);
      if (isEnd) {
         return WordContainsTypeEnum.CONTAINS_END;
      } else {
         return WordContainsTypeEnum.CONTAINS_PREFIX;
      }
   }

   private static boolean isEnd(final Map map) {
      if (ObjectUtils.isNull(map)) {
         return false;
      } else {
         Object value = map.get("ED");
         return ObjectUtils.isNull(value) ? false : (Boolean)value;
      }
   }

   private Map getNowMap(Map nowMap, final int index, final StringBuilder stringBuilder, final InnerSensitiveWordContext sensitiveContext) {
      IWordContext context = sensitiveContext.wordContext();
      char mappingChar = stringBuilder.charAt(index);
      Map currentMap = (Map)nowMap.get(mappingChar);
      if (context.ignoreRepeat() && index > 0) {
         char preMappingChar = stringBuilder.charAt(index - 1);
         if (preMappingChar == mappingChar) {
            currentMap = nowMap;
         }
      }

      return currentMap;
   }

   public synchronized void destroy() {
      if (this.innerWordMap != null) {
         this.innerWordMap.clear();
      }

   }
}
