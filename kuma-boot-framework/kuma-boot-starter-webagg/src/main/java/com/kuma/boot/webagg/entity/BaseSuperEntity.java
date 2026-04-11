package com.kuma.boot.webagg.entity;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, JpaEntityListener.class})
public class BaseSuperEntity<T extends SuperEntity<T, I>, I extends Serializable> extends SuperEntity<T, I> implements Serializable {
   private static final long serialVersionUID = -4603650115461757622L;
   @CreatedDate
   @Column(
      name = "`create_date`",
      updatable = false,
      columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP comment '\u521b\u5efa\u65f6\u95f4'"
   )
   @TableField(
      value = "create_date",
      fill = FieldFill.INSERT
   )
   private LocalDateTime createDate;
   @CreatedBy
   @Column(
      name = "`create_user`",
      columnDefinition = "varchar(256) not null default 'system' comment '\u521b\u5efa\u4eba'"
   )
   @TableField(
      value = "create_user",
      fill = FieldFill.INSERT
   )
   private String createUser;
   @LastModifiedDate
   @Column(
      name = "`modify_date`",
      columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '\u66f4\u65b0\u65f6\u95f4'"
   )
   @TableField(
      value = "modify_date",
      fill = FieldFill.INSERT_UPDATE
   )
   private LocalDateTime modifyDate;
   @LastModifiedBy
   @Column(
      name = "`modify_user`",
      columnDefinition = "varchar(256) not null default 'system' comment '\u6700\u540e\u4fee\u6539\u4eba'"
   )
   @TableField(
      value = "modify_user",
      fill = FieldFill.INSERT_UPDATE
   )
   private String modifyUser;
   @Version
   @com.baomidou.mybatisplus.annotation.Version
   @Column(
      name = "`version`",
      columnDefinition = "int not null default 1 comment '\u7248\u672c\u53f7'"
   )
   private Integer version = 1;
   @TableLogic
   @Column(
      name = "`del_flag`",
      columnDefinition = "tinyint(1) NOT NULL DEFAULT '0' COMMENT '\u662f\u5426\u5220\u9664 0-\u6b63\u5e38 1-\u5220\u9664'"
   )
   private Boolean delFlag = false;
   @Column(
      name = "`remark`",
      columnDefinition = "varchar(256) null comment '\u5907\u6ce8'"
   )
   private String remark;
   @Column(
      name = "`extra`",
      columnDefinition = "varchar(10000) null comment '\u989d\u5916\u4fe1\u606fjson\u6570\u636e'"
   )
   private String extra;

   public BaseSuperEntity() {
   }

   public BaseSuperEntity(I id, LocalDateTime createDate, String createUser, LocalDateTime modifyDate, String modifyUser, Integer version, Boolean delFlag) {
      super(id);
      this.createDate = createDate;
      this.createUser = createUser;
      this.modifyDate = modifyDate;
      this.modifyUser = modifyUser;
      this.version = version;
      this.delFlag = delFlag;
   }

   public BaseSuperEntity(LocalDateTime createDate, String createUser, LocalDateTime modifyDate, String modifyUser, Integer version, Boolean delFlag) {
      this.createDate = createDate;
      this.createUser = createUser;
      this.modifyDate = modifyDate;
      this.modifyUser = modifyUser;
      this.version = version;
      this.delFlag = delFlag;
   }

   public LocalDateTime getCreateDate() {
      return this.createDate;
   }

   public void setCreateDate(LocalDateTime createDate) {
      this.createDate = createDate;
   }

   public String getCreateUser() {
      return this.createUser;
   }

   public void setCreateUser(String createUser) {
      this.createUser = createUser;
   }

   public LocalDateTime getModifyDate() {
      return this.modifyDate;
   }

   public void setModifyDate(LocalDateTime modifyDate) {
      this.modifyDate = modifyDate;
   }

   public String getModifyUser() {
      return this.modifyUser;
   }

   public void setModifyUser(String modifyUser) {
      this.modifyUser = modifyUser;
   }

   public Integer getVersion() {
      return this.version;
   }

   public void setVersion(Integer version) {
      this.version = version;
   }

   public Boolean getDelFlag() {
      return this.delFlag;
   }

   public void setDelFlag(Boolean delFlag) {
      this.delFlag = delFlag;
   }

   public LocalDateTime createDate() {
      return this.createDate;
   }

   public T createDate(LocalDateTime createDate) {
      this.createDate = createDate;
      return (T)this;
   }

   public String createUser() {
      return this.createUser;
   }

   public T createUser(String createUser) {
      this.createUser = createUser;
      return (T)this;
   }

   public LocalDateTime modifyDate() {
      return this.modifyDate;
   }

   public T modifyDate(LocalDateTime modifyDate) {
      this.modifyDate = modifyDate;
      return (T)this;
   }

   public String modifyUser() {
      return this.modifyUser;
   }

   public T modifyUser(String modifyUser) {
      this.modifyUser = modifyUser;
      return (T)this;
   }

   public Integer version() {
      return this.version;
   }

   public T version(Integer version) {
      this.version = version;
      return (T)this;
   }

   public Boolean delFlag() {
      return this.delFlag;
   }

   public T delFlag(Boolean delFlag) {
      this.delFlag = delFlag;
      return (T)this;
   }

   protected void logicDelete() {
      this.delFlag = true;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setRemark(String remark) {
      if (StrUtil.length(remark) > 256) {
         remark = StringUtils.abbreviate(remark, 256);
      }

      this.remark = remark;
   }

   public String getExtra() {
      return this.extra;
   }

   public void setExtra(String extra) {
      this.extra = extra;
   }

   public <INFO> INFO getExtra(Class<INFO> infoClass) {
      return (INFO)(StrUtil.isNotBlank(this.extra) ? JSON.parseObject(this.extra, infoClass) : null);
   }

   public <INFO> void setExtra(INFO extra) {
      if (extra == null) {
         this.extra = null;
      } else {
         this.extra = JSON.toJSONString(extra);
      }

   }
}
