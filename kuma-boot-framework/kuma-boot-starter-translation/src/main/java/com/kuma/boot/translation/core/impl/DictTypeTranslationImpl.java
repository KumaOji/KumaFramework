package com.kuma.boot.translation.core.impl;

import com.kuma.boot.translation.annotation.TranslationType;
import com.kuma.boot.translation.core.TranslationInterface;

@TranslationType(
   type = "dict_type_to_label"
)
public class DictTypeTranslationImpl implements TranslationInterface<String> {
   public DictTypeTranslationImpl() {
   }

   public String translation(Object key, String other) {
      return null;
   }
}
