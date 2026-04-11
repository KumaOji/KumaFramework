package com.kuma.boot.sensitive.sensitiveword.support.tag;

import java.util.Collections;
import java.util.Set;

public class NoneWordTag extends AbstractWordTag {
   public NoneWordTag() {
   }

   protected Set<String> doGetTag(String word) {
      return Collections.emptySet();
   }
}
