package com.kuma.boot.data.mongodb.helper.bean;

import java.util.Collections;
import java.util.List;

public class Page<T> {
   Long count = 0L;
   Integer curr = 1;
   Integer limit = 10;
   Boolean queryCount = true;
   List<T> list = Collections.emptyList();

   public Page() {
   }

   public Boolean getQueryCount() {
      return this.queryCount;
   }

   public void setQueryCount(Boolean queryCount) {
      this.queryCount = queryCount;
   }

   public List<T> getList() {
      return this.list;
   }

   public void setList(List<T> list) {
      this.list = list;
   }

   public Long getCount() {
      return this.count;
   }

   public void setCount(Long count) {
      this.count = count;
   }

   public Integer getCurr() {
      return this.curr;
   }

   public void setCurr(Integer curr) {
      this.curr = curr;
   }

   public Integer getLimit() {
      return this.limit;
   }

   public void setLimit(Integer limit) {
      this.limit = limit;
   }
}
