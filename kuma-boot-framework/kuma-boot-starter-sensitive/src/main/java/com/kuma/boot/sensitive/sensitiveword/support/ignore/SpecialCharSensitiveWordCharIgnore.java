package com.kuma.boot.sensitive.sensitiveword.support.ignore;

import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.context.InnerSensitiveWordContext;
import java.util.Set;

public class SpecialCharSensitiveWordCharIgnore extends AbstractSensitiveWordCharIgnore {
   private static final String SPECIAL = "`-=~!@#$%^&*()_+[]{}\\|;:'\",./<>?";
   private static final Set<Character> SET = StringUtils.toCharSet("`-=~!@#$%^&*()_+[]{}\\|;:'\",./<>?");

   public SpecialCharSensitiveWordCharIgnore() {
   }

   protected boolean doIgnore(int ix, char[] chars, InnerSensitiveWordContext innerContext) {
      char c = chars[ix];
      return SET.contains(c);
   }
}
