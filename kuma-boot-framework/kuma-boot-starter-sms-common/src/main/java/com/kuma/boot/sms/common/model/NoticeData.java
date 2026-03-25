package com.kuma.boot.sms.common.model;

import java.util.Map;

public class NoticeData {
   private String type;
   private Map params;
   private boolean asnyc;

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Map getParams() {
      return this.params;
   }

   public void setParams(Map params) {
      this.params = params;
   }

   public boolean isAsnyc() {
      return this.asnyc;
   }

   public void setAsnyc(boolean asnyc) {
      this.asnyc = asnyc;
   }
}
