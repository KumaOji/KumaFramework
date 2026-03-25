package com.kuma.boot.sms.common.properties;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public abstract class AbstractHandlerProperties {
   private int weight = 1;
   private Map templates;
   private Map paramsOrders;

   public final @Nullable Object getTemplates(String type) {
      return this.templates == null ? null : this.templates.get(type);
   }

   public final List getParamsOrder(String type) {
      return (List)this.paramsOrders.get(type);
   }

   public int getWeight() {
      return this.weight;
   }

   public void setWeight(int weight) {
      if (weight >= 0) {
         this.weight = weight;
      }

   }

   public Map getTemplates() {
      return this.templates;
   }

   public void setTemplates(Map templates) {
      this.templates = templates;
   }

   public Map getParamsOrders() {
      return this.paramsOrders;
   }

   public void setParamsOrders(Map paramsOrders) {
      this.paramsOrders = paramsOrders;
   }
}
