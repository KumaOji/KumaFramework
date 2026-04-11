package com.kuma.boot.sensitive.sensitiveword.support.ignore;

import com.kuma.boot.sensitive.sensitiveword.api.ISensitiveWordCharIgnore;

public class SensitiveWordCharIgnores {
   public SensitiveWordCharIgnores() {
   }

   public static ISensitiveWordCharIgnore specialChars() {
      return new SpecialCharSensitiveWordCharIgnore();
   }

   public static ISensitiveWordCharIgnore none() {
      return new NoneSensitiveWordCharIgnore();
   }

   public static ISensitiveWordCharIgnore defaults() {
      return none();
   }
}
