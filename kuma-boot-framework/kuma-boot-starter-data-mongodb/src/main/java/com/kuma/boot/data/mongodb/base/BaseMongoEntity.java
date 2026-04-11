package com.kuma.boot.data.mongodb.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import java.util.Date;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

public abstract class BaseMongoEntity {
   @Schema(
      title = "\u6570\u636e\u521b\u5efa\u65f6\u95f4"
   )
   @Column(
      name = "create_time",
      updatable = false
   )
   @CreatedDate
   @JsonFormat(
      pattern = "yyyy-MM-dd HH:mm:ss"
   )
   private Date createTime = new Date();
   @Schema(
      title = "\u6570\u636e\u66f4\u65b0\u65f6\u95f4"
   )
   @Column(
      name = "update_time"
   )
   @LastModifiedDate
   @JsonFormat(
      pattern = "yyyy-MM-dd HH:mm:ss"
   )
   private Date updateTime = new Date();

   public BaseMongoEntity() {
   }

   public abstract String getId();

   public abstract void setId(String id);

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setCreateTime(Date createTime) {
      this.createTime = createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setUpdateTime(Date updateTime) {
      this.updateTime = updateTime;
   }
}
