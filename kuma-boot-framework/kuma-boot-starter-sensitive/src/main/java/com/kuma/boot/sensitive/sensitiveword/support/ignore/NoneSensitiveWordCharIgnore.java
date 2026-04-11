package com.kuma.boot.sensitive.sensitiveword.support.ignore;

import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;

public class NoneSensitiveWordCharIgnore extends AbstractSensitiveWordCharIgnore {
   public NoneSensitiveWordCharIgnore() {
   }

   protected boolean doIgnore(int ix, char[] chars, InnerSensitiveWordContext innerContext) {
      return false;
   }
}
