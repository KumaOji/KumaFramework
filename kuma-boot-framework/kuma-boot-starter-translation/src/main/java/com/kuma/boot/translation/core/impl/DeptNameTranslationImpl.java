package com.kuma.boot.translation.core.impl;

import com.kuma.boot.translation.annotation.TranslationType;
import com.kuma.boot.translation.core.TranslationInterface;

@TranslationType(
   type = "dept_id_to_name"
)
public class DeptNameTranslationImpl implements TranslationInterface<String> {
   public DeptNameTranslationImpl() {
   }

   public String translation(Object key, String other) {
      return null;
   }
}
