package com.kuma.boot.translation.core.impl;

import com.kuma.boot.translation.annotation.TranslationType;
import com.kuma.boot.translation.core.TranslationInterface;

@TranslationType(
   type = "oss_id_to_url"
)
public class OssUrlTranslationImpl implements TranslationInterface<String> {
   public OssUrlTranslationImpl() {
   }

   public String translation(Object key, String other) {
      return null;
   }
}
