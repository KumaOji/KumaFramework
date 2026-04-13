/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.kuma.boot.common.utils.lang.StringUtils;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * SuperEntity
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-04 07:40:46
 */
// @TypeDefs({
//	@TypeDef(name = "json", typeClass = JsonType.class)
// })
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, JpaEntityListener.class})
public class BaseSuperEntity<T extends SuperEntity<T, I>, I extends Serializable> extends
        SuperEntity<T, I> implements Serializable {

   @Serial
   private static final long serialVersionUID = -4603650115461757622L;

   @CreatedDate
   @Column(
           name = "`create_date`",
           updatable = false,
           columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间'")
   @TableField(value = "create_date", fill = FieldFill.INSERT)
   private LocalDateTime createDate;

   @CreatedBy
   @Column(name = "`create_user`", columnDefinition = "varchar(256) not null default 'system' comment '创建人'")
   @TableField(value = "create_user", fill = FieldFill.INSERT)
   private String createUser;

   @LastModifiedDate
   @Column(
           name = "`modify_date`",
           columnDefinition =
                   "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间'")
   @TableField(value = "modify_date", fill = FieldFill.INSERT_UPDATE)
   private LocalDateTime modifyDate;

   @LastModifiedBy
   @Column(name = "`modify_user`", columnDefinition = "varchar(256) not null default 'system' comment '最后修改人'")
   @TableField(value = "modify_user", fill = FieldFill.INSERT_UPDATE)
   private String modifyUser;

   @Version
   @com.baomidou.mybatisplus.annotation.Version
   @Column(name = "`version`", columnDefinition = "int not null default 1 comment '版本号'")
   private Integer version = 1;

   @TableLogic
   @Column(name = "`del_flag`", columnDefinition = "tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0-正常 1-删除'")
   private Boolean delFlag = false;

   @Column(name = "`remark`", columnDefinition = "varchar(256) null comment '备注'")
   private String remark;

   @Column(name = "`extra`", columnDefinition = "varchar(10000) null comment '额外信息json数据'")
   private String extra;

   public BaseSuperEntity() {
   }

   public BaseSuperEntity(
           I id,
           LocalDateTime createDate,
           String createUser,
           LocalDateTime modifyDate,
           String modifyUser,
           Integer version,
           Boolean delFlag) {
      super(id);
      this.createDate = createDate;
      this.createUser = createUser;
      this.modifyDate = modifyDate;
      this.modifyUser = modifyUser;
      this.version = version;
      this.delFlag = delFlag;
   }

   public BaseSuperEntity(
           LocalDateTime createDate,
           String createUser,
           LocalDateTime modifyDate,
           String modifyUser,
           Integer version,
           Boolean delFlag) {
      this.createDate = createDate;
      this.createUser = createUser;
      this.modifyDate = modifyDate;
      this.modifyUser = modifyUser;
      this.version = version;
      this.delFlag = delFlag;
   }

   public LocalDateTime getCreateDate() {
      return createDate;
   }

   public void setCreateDate(LocalDateTime createDate) {
      this.createDate = createDate;
   }

   public String getCreateUser() {
      return createUser;
   }

   public void setCreateUser(String createUser) {
      this.createUser = createUser;
   }

   public LocalDateTime getModifyDate() {
      return modifyDate;
   }

   public void setModifyDate(LocalDateTime modifyDate) {
      this.modifyDate = modifyDate;
   }

   public String getModifyUser() {
      return modifyUser;
   }

   public void setModifyUser(String modifyUser) {
      this.modifyUser = modifyUser;
   }

   public Integer getVersion() {
      return version;
   }

   public void setVersion(Integer version) {
      this.version = version;
   }

   public Boolean getDelFlag() {
      return delFlag;
   }

   public void setDelFlag(Boolean delFlag) {
      this.delFlag = delFlag;
   }

   public LocalDateTime createDate() {
      return createDate;
   }

   @SuppressWarnings("unchecked")
   public T createDate(LocalDateTime createDate) {
      this.createDate = createDate;
      return (T) this;
   }

   public String createUser() {
      return createUser;
   }

   @SuppressWarnings("unchecked")
   public T createUser(String createUser) {
      this.createUser = createUser;
      return (T) this;
   }

   public LocalDateTime modifyDate() {
      return modifyDate;
   }

   @SuppressWarnings("unchecked")
   public T modifyDate(LocalDateTime modifyDate) {
      this.modifyDate = modifyDate;
      return (T) this;
   }

   public String modifyUser() {
      return modifyUser;
   }

   @SuppressWarnings("unchecked")
   public T modifyUser(String modifyUser) {
      this.modifyUser = modifyUser;
      return (T) this;
   }

   public Integer version() {
      return version;
   }

   @SuppressWarnings("unchecked")
   public T version(Integer version) {
      this.version = version;
      return (T) this;
   }

   public Boolean delFlag() {
      return delFlag;
   }

   @SuppressWarnings("unchecked")
   public T delFlag(Boolean delFlag) {
      this.delFlag = delFlag;
      return (T) this;
   }

   // 逻辑删除方法
   protected void logicDelete() {
      this.delFlag = true;
   }

   public String getRemark() {
      return remark;
   }

   public void setRemark(String remark) {
      if (StrUtil.length(remark) > 256) {
         remark = abbreviate(remark, 256);
      }
      this.remark = remark;
   }

   public String getExtra() {
      return extra;
   }

   public void setExtra(String extra) {
      this.extra = extra;
   }

   public <INFO> INFO getExtra(Class<INFO> infoClass) {
      if (StrUtil.isNotBlank(extra)) {
         return JSON.parseObject(extra, infoClass);
      }
      return null;
   }

   public <INFO> void setExtra(INFO extra) {
      if (extra == null) {
         this.extra = null;
      } else {
         this.extra = JSON.toJSONString(extra);
      }
   }
}
