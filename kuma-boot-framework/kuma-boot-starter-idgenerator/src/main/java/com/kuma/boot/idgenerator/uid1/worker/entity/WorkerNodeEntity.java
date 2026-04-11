package com.kuma.boot.idgenerator.uid1.worker.entity;

import java.util.Date;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WorkerNodeEntity {
   private long id;
   private String hostName;
   private String port;
   private int type;
   private Date launchDate = new Date();
   private Date created;
   private Date modified;

   public WorkerNodeEntity() {
   }

   public long getId() {
      return this.id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getHostName() {
      return this.hostName;
   }

   public void setHostName(String hostName) {
      this.hostName = hostName;
   }

   public String getPort() {
      return this.port;
   }

   public void setPort(String port) {
      this.port = port;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public Date getLaunchDate() {
      return this.launchDate;
   }

   public void setLaunchDateDate(Date launchDate) {
      this.launchDate = launchDate;
   }

   public Date getCreated() {
      return this.created;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Date getModified() {
      return this.modified;
   }

   public void setModified(Date modified) {
      this.modified = modified;
   }

   public String toString() {
      return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
   }
}
