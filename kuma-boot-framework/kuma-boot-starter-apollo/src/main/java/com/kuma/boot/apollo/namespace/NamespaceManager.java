package com.kuma.boot.apollo.namespace;

import java.util.HashSet;
import java.util.Set;

public class NamespaceManager {
   private static final Set<String> NAMESPACES = new HashSet();

   public NamespaceManager() {
   }

   public static Set<String> get() {
      return NAMESPACES;
   }

   public static void addNamespace(String namespace) {
      NAMESPACES.add(namespace);
   }
}
