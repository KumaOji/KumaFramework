package com.kuma.boot.job.xxl.executor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class XxlJobGroup {
   private int id;
   private String appname;
   private String title;
   private int addressType;
   private String addressList;
   private Date updateTime;
   private List<String> registryList;

   public XxlJobGroup() {
   }

   public List<String> getRegistryList() {
      if (this.addressList != null && !this.addressList.trim().isEmpty()) {
         this.registryList = new ArrayList(Arrays.asList(this.addressList.split(",")));
      }

      return this.registryList;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getAppname() {
      return this.appname;
   }

   public void setAppname(String appname) {
      this.appname = appname;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public int getAddressType() {
      return this.addressType;
   }

   public void setAddressType(int addressType) {
      this.addressType = addressType;
   }

   public String getAddressList() {
      return this.addressList;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setUpdateTime(Date updateTime) {
      this.updateTime = updateTime;
   }

   public void setAddressList(String addressList) {
      this.addressList = addressList;
   }
}
