package com.kuma.boot.data.jpa.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity extends AbstractEntity {
   @Schema(
      name = "数据创建时间"
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
      name = "数据更新时间"
   )
   @Column(
      name = "update_time"
   )
   @LastModifiedDate
   @JsonFormat(
      pattern = "yyyy-MM-dd HH:mm:ss"
   )
   private Date updateTime = new Date();
   @Schema(
      name = "排序值"
   )
   @Column(
      name = "ranking"
   )
   private Integer ranking = 0;

   public BaseEntity() {
   }

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

   public Integer getRanking() {
      return this.ranking;
   }

   public void setRanking(Integer ranking) {
      this.ranking = ranking;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("createTime", this.createTime).add("updateTime", this.updateTime).add("ranking", this.ranking).toString();
   }
}
