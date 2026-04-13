package com.kuma.boot.data.jpa.tenant;

import com.kuma.boot.common.enums.DataItemStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseSysEntity extends BaseEntity {
   @Schema(
      name = "\u6570\u636e\u72b6\u6001"
   )
   @Column(
      name = "status"
   )
   @Enumerated(EnumType.ORDINAL)
   private DataItemStatusEnum status;
   @Schema(
      name = "\u662f\u5426\u4e3a\u4fdd\u7559\u6570\u636e",
      description = "True \u4e3a\u4e0d\u80fd\u5220\uff0cFalse\u4e3a\u53ef\u4ee5\u5220\u9664"
   )
   @Column(
      name = "is_reserved"
   )
   private Boolean reserved;
   @Schema(
      name = "\u7248\u672c\u53f7"
   )
   @Column(
      name = "reversion"
   )
   private Integer reversion;
   @Schema(
      name = "\u5907\u6ce8"
   )
   @Column(
      name = "description",
      length = 512
   )
   private String description;

   public BaseSysEntity() {
      this.status = DataItemStatusEnum.ENABLE;
      this.reserved = Boolean.FALSE;
      this.reversion = 0;
   }

   public DataItemStatusEnum getStatus() {
      return this.status;
   }

   public void setStatus(DataItemStatusEnum status) {
      this.status = status;
   }

   public Boolean getReserved() {
      return this.reserved;
   }

   public void setReserved(Boolean reserved) {
      this.reserved = reserved;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Integer getReversion() {
      return this.reversion;
   }

   public void setReversion(Integer reversion) {
      this.reversion = reversion;
   }
}
