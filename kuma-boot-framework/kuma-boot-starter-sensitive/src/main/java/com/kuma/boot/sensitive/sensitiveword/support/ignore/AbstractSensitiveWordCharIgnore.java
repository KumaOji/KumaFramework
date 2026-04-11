package com.kuma.boot.sensitive.sensitiveword.support.ignore;

import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWordCharIgnore;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;

public abstract class AbstractSensitiveWordCharIgnore implements ISensitiveWordCharIgnore {
   public AbstractSensitiveWordCharIgnore() {
   }

   protected abstract boolean doIgnore(int ix, char[] chars, InnerSensitiveWordContext innerContext);

   public boolean ignore(int ix, char[] chars, InnerSensitiveWordContext innerContext) {
      return this.doIgnore(ix, chars, innerContext);
   }
}
