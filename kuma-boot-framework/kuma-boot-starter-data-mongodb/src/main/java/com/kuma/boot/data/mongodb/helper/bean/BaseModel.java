package com.kuma.boot.data.mongodb.helper.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;

public class BaseModel implements Serializable {
   @Id
   private String id;
   @CreateTime
   private LocalDateTime createTime;
   @UpdateTime
   private LocalDateTime updateTime;

   public BaseModel() {
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public LocalDateTime getCreateTime() {
      return this.createTime;
   }

   public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public LocalDateTime getUpdateTime() {
      return this.updateTime;
   }

   public void setUpdateTime(LocalDateTime updateTime) {
      this.updateTime = updateTime;
   }
}
