package com.kuma.boot.sensitive.sensitiveword.support.data;

import com.kuma.boot.common.extension.CollectionUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordData;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordContainsTypeEnum;
import java.util.Collection;

public abstract class AbstractWordData implements IWordData {
   public AbstractWordData() {
   }

   protected abstract WordContainsTypeEnum doContains(StringBuilder stringBuilder, InnerSensitiveWordContext innerContext);

   protected abstract void doInitWordData(Collection<String> collection);

   protected abstract void doRemoveWord(Collection<String> collection);

   protected abstract void doAddWord(Collection<String> collection);

   public void initWordData(Collection<String> collection) {
      this.doInitWordData(collection);
   }

   public void removeWord(Collection<String> collection) {
      if (!CollectionUtils.isEmpty(collection)) {
         this.doRemoveWord(collection);
      }
   }

   public void addWord(Collection<String> collection) {
      if (!CollectionUtils.isEmpty(collection)) {
         this.doAddWord(collection);
      }
   }

   public WordContainsTypeEnum contains(StringBuilder stringBuilder, InnerSensitiveWordContext innerContext) {
      return stringBuilder != null && stringBuilder.length() > 0 ? this.doContains(stringBuilder, innerContext) : WordContainsTypeEnum.NOT_FOUND;
   }
}
