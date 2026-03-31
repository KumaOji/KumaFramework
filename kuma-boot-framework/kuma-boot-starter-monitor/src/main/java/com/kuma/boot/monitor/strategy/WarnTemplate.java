package com.kuma.boot.monitor.strategy;

import java.util.HashMap;

public class WarnTemplate extends HashMap<String, String> {
   public WarnTemplate() {
   }

   public WarnTemplate register(String filed, String template) {
      if (!this.containsKey(filed)) {
         this.put(filed, template);
      } else {
         this.replace(filed, template);
      }

      return this;
   }

   public String getTemplate(String filed) {
      return !this.containsKey(filed) ? (String)this.get("") : (String)this.get(filed);
   }

   public String getWarnContent(String filedName, String filedDesc, Object value, Rule.RuleInfo rule) {
      return this.getTemplate(filedName).replace("{value}", value.toString()).replace("{rule}", rule.toString()).replace("{name}", filedName).replace("{desc}", filedDesc);
   }
}
