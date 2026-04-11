package com.kuma.boot.data.mongodb.mongodb.vo;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {
   private static final long serialVersionUID = 5760097915453738435L;
   public static final int DEFAULT_PAGE_SIZE = 10;
   private int pageSize;
   private int currentPage;
   private int totalPage;
   private int totalCount;
   private List<T> rows;

   public Page() {
      this.currentPage = 1;
      this.pageSize = 10;
   }

   public Page(int currentPage, int pageSize) {
      this.currentPage = currentPage <= 0 ? 1 : currentPage;
      this.pageSize = pageSize <= 0 ? 1 : pageSize;
   }

   public int getPageSize() {
      return this.pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public int getCurrentPage() {
      return this.currentPage;
   }

   public void setCurrentPage(int currentPage) {
      this.currentPage = currentPage;
   }

   public int getTotalPage() {
      return this.totalPage;
   }

   public void setTotalPage(int totalPage) {
      this.totalPage = totalPage;
   }

   public int getTotalCount() {
      return this.totalCount;
   }

   public void setTotalCount(int totalCount) {
      this.totalCount = totalCount;
   }

   public void build(List<T> rows) {
      this.setRows(rows);
      int count = this.getTotalCount();
      int divisor = count / this.getPageSize();
      int remainder = count % this.getPageSize();
      this.setTotalPage(remainder == 0 ? (divisor == 0 ? 1 : divisor) : divisor + 1);
   }

   public List<T> getRows() {
      return this.rows;
   }

   public void setRows(List<T> rows) {
      this.rows = rows;
   }
}
