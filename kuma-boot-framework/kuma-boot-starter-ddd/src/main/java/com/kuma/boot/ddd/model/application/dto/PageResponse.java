package com.kuma.boot.ddd.model.application.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PageResponse extends Response {
   private static final long serialVersionUID = 1L;
   private int totalCount = 0;
   private int pageSize = 1;
   private int pageIndex = 1;
   private Collection data;

   public int getTotalCount() {
      return this.totalCount;
   }

   public void setTotalCount(int totalCount) {
      this.totalCount = totalCount;
   }

   public int getPageSize() {
      return this.pageSize < 1 ? 1 : this.pageSize;
   }

   public void setPageSize(int pageSize) {
      if (pageSize < 1) {
         this.pageSize = 1;
      } else {
         this.pageSize = pageSize;
      }

   }

   public int getPageIndex() {
      return this.pageIndex < 1 ? 1 : this.pageIndex;
   }

   public void setPageIndex(int pageIndex) {
      if (pageIndex < 1) {
         this.pageIndex = 1;
      } else {
         this.pageIndex = pageIndex;
      }

   }

   public List getData() {
      if (null == this.data) {
         return Collections.emptyList();
      } else {
         return (List)(this.data instanceof List ? (List)this.data : new ArrayList(this.data));
      }
   }

   public void setData(Collection data) {
      this.data = data;
   }

   public int getTotalPages() {
      return this.totalCount % this.pageSize == 0 ? this.totalCount / this.pageSize : this.totalCount / this.pageSize + 1;
   }

   public boolean isEmpty() {
      return this.data == null || this.data.isEmpty();
   }

   public boolean isNotEmpty() {
      return !this.isEmpty();
   }

   public static PageResponse buildSuccess() {
      PageResponse response = new PageResponse();
      response.setSuccess(true);
      return response;
   }

   public static PageResponse buildFailure(String errCode, String errMessage) {
      PageResponse response = new PageResponse();
      response.setSuccess(false);
      response.setErrCode(errCode);
      response.setErrMessage(errMessage);
      return response;
   }

   public static PageResponse of(int pageSize, int pageIndex) {
      PageResponse<T> response = new PageResponse();
      response.setSuccess(true);
      response.setData(Collections.emptyList());
      response.setTotalCount(0);
      response.setPageSize(pageSize);
      response.setPageIndex(pageIndex);
      return response;
   }

   public static PageResponse of(Collection data, int totalCount, int pageSize, int pageIndex) {
      PageResponse<T> response = new PageResponse();
      response.setSuccess(true);
      response.setData(data);
      response.setTotalCount(totalCount);
      response.setPageSize(pageSize);
      response.setPageIndex(pageIndex);
      return response;
   }

   public String toString() {
      boolean var10000 = super.isSuccess();
      return "PageResponse{success=" + var10000 + ", errCode='" + super.getErrCode() + "', errMessage='" + super.getErrMessage() + "', totalCount=" + this.totalCount + ", pageSize=" + this.pageSize + ", pageIndex=" + this.pageIndex + ", data=" + String.valueOf(this.data) + "}";
   }
}
