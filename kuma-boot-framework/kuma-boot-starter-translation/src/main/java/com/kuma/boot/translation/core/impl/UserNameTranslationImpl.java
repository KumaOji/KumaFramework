package com.kuma.boot.translation.core.impl;

import com.kuma.boot.translation.annotation.TranslationType;
import com.kuma.boot.translation.core.TranslationInterface;

@TranslationType(
   type = "user_id_to_name"
)
public class UserNameTranslationImpl implements TranslationInterface<String> {
   public UserNameTranslationImpl() {
   }

   public String translation(Object key, String other) {
      return null;
   }
}
