package com.kuma.boot.data.jpa.fenix.jpa.transformer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PrefixUnderscoreTransformer extends UnderscoreTransformer {
   private static final Set<String> prefixSet = new HashSet(Arrays.asList("c_", "n_", "d_", "dt_"));

   public PrefixUnderscoreTransformer() {
   }

   public static Set<String> getPrefixSet() {
      return prefixSet;
   }

   protected String toLowerCamelCase(String name) {
      return super.toLowerCamelCase(this.getRemovedPrefixName(name));
   }

   private String getRemovedPrefixName(String name) {
      name = name.toLowerCase();

      for(String prefix : prefixSet) {
         if (name.startsWith(prefix)) {
            return name.substring(prefix.length());
         }
      }

      return name;
   }
}
