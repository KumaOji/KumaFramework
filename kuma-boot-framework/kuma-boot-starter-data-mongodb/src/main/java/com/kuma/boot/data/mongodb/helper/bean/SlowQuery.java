package com.kuma.boot.data.mongodb.helper.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(
   collection = "_slowQuery"
)
public class SlowQuery {
   @Id
   String id;
   String query;
   String time;
   Long queryTime;
   String system;
   String stack;

   public SlowQuery() {
   }

   public String getStack() {
      return this.stack;
   }

   public void setStack(String stack) {
      this.stack = stack;
   }

   public String getSystem() {
      return this.system;
   }

   public void setSystem(String system) {
      this.system = system;
   }

   public Long getQueryTime() {
      return this.queryTime;
   }

   public void setQueryTime(Long queryTime) {
      this.queryTime = queryTime;
   }

   public String getQuery() {
      return this.query;
   }

   public void setQuery(String query) {
      this.query = query;
   }

   public String getTime() {
      return this.time;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }
}
