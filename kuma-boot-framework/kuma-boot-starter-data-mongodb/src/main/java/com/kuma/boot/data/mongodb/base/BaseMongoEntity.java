package com.kuma.boot.data.mongodb.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import java.util.Date;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

public abstract class BaseMongoEntity {
   @Schema(
      title = "数据创建时间"
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
      title = "数据更新时间"
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
