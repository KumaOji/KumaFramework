package com.kuma.boot.ddd.model.application.dto;

public abstract class PageQuery extends Query {
   private static final long serialVersionUID = 1L;
   public static final String ASC = "ASC";
   public static final String DESC = "DESC";
   private static final int DEFAULT_PAGE_SIZE = 10;
   private int pageSize = 10;
   private int pageIndex = 1;
   private String orderBy;
   private String orderDirection = "DESC";
   private String groupBy;
   private boolean needTotalCount = true;

   public int getPageIndex() {
      return this.pageIndex < 1 ? 1 : this.pageIndex;
   }

   public PageQuery setPageIndex(int pageIndex) {
      this.pageIndex = pageIndex;
      return this;
   }

   public int getPageSize() {
      return this.pageSize;
   }

   public PageQuery setPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
   }

   public int getOffset() {
      return (this.getPageIndex() - 1) * this.getPageSize();
   }

   public String getOrderBy() {
      return this.orderBy;
   }

   public PageQuery setOrderBy(String orderBy) {
      this.orderBy = orderBy;
      return this;
   }

   public String getOrderDirection() {
      return this.orderDirection;
   }

   public PageQuery setOrderDirection(String orderDirection) {
      if ("ASC".equalsIgnoreCase(orderDirection) || "DESC".equalsIgnoreCase(orderDirection)) {
         this.orderDirection = orderDirection;
      }

      return this;
   }

   public String getGroupBy() {
      return this.groupBy;
   }

   public void setGroupBy(String groupBy) {
      this.groupBy = groupBy;
   }

   public boolean isNeedTotalCount() {
      return this.needTotalCount;
   }

   public void setNeedTotalCount(boolean needTotalCount) {
      this.needTotalCount = needTotalCount;
   }

   public String toString() {
      return "PageQuery{pageSize=" + this.pageSize + ", pageIndex=" + this.pageIndex + ", orderBy='" + this.orderBy + "', orderDirection='" + this.orderDirection + "', groupBy='" + this.groupBy + "', needTotalCount=" + this.needTotalCount + "}";
   }
}
