package com.kuma.boot.sensitive.sensitiveword.support.tag;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordTag;
import java.util.Collections;
import java.util.Set;

public abstract class AbstractWordTag implements IWordTag {
   public AbstractWordTag() {
   }

   protected abstract Set<String> doGetTag(String word);

   public Set<String> getTag(String word) {
      return StringUtils.isEmpty(word) ? Collections.emptySet() : this.doGetTag(word);
   }
}
