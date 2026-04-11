package com.kuma.boot.data.elasticsearch.model;

import java.io.Serializable;
import java.util.Objects;

public class SearchDto implements Serializable {
   private static final long serialVersionUID = -2084416068307485742L;
   private String queryStr;
   private Integer page;
   private Integer limit;
   private String sortCol;
   private Boolean isHighlighter;
   private String routing;

   public SearchDto() {
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SearchDto searchDto = (SearchDto)o;
         return Objects.equals(this.queryStr, searchDto.queryStr) && Objects.equals(this.page, searchDto.page) && Objects.equals(this.limit, searchDto.limit) && Objects.equals(this.sortCol, searchDto.sortCol) && Objects.equals(this.isHighlighter, searchDto.isHighlighter) && Objects.equals(this.routing, searchDto.routing);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.queryStr, this.page, this.limit, this.sortCol, this.isHighlighter, this.routing});
   }

   public String getQueryStr() {
      return this.queryStr;
   }

   public void setQueryStr(String queryStr) {
      this.queryStr = queryStr;
   }

   public Integer getPage() {
      return this.page;
   }

   public void setPage(Integer page) {
      this.page = page;
   }

   public Integer getLimit() {
      return this.limit;
   }

   public void setLimit(Integer limit) {
      this.limit = limit;
   }

   public String getSortCol() {
      return this.sortCol;
   }

   public void setSortCol(String sortCol) {
      this.sortCol = sortCol;
   }

   public Boolean getHighlighter() {
      return this.isHighlighter;
   }

   public void setHighlighter(Boolean highlighter) {
      this.isHighlighter = highlighter;
   }

   public String getRouting() {
      return this.routing;
   }

   public void setRouting(String routing) {
      this.routing = routing;
   }
}
