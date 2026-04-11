package com.kuma.boot.eventbus.disruptor.tmp3.context;

import java.util.LinkedHashMap;
import java.util.Map;

public class EventHandlerDefinition {
   private int order = 0;
   private String definitions = null;
   private Map<String, String> definitionMap = new LinkedHashMap();

   public EventHandlerDefinition() {
   }

   public int getOrder() {
      return this.order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public String getDefinitions() {
      return this.definitions;
   }

   public void setDefinitions(String definitions) {
      this.definitions = definitions;
   }

   public Map<String, String> getDefinitionMap() {
      return this.definitionMap;
   }

   public void setDefinitionMap(Map<String, String> definitionMap) {
      this.definitionMap = definitionMap;
   }
}
