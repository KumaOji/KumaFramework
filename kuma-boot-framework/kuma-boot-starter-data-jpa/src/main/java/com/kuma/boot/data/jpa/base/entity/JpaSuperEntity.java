/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.jpa.base.entity;

import com.querydsl.core.BooleanBuilder;
import com.kuma.boot.data.jpa.bean.CustomRevisionListener;
import jakarta.persistence.*;
import jakarta.validation.groups.Default;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * JpaSuperEntity
 * @EnableJpaAuditing，启用JPA审计功能开关。
 * @EntityListeners，可以监听实体对象的增删改查操作，调用监听器中设置的回调方法。
 * @CreatedBy，创建人，执行insert操作时自动赋值。
 * @CreatedDate，创建日期，执行insert操作时自动赋值。
 * @LastModifiedBy，最后修改人，执行insert或update操作时自动赋值。
 * @LastModifiedDate，最后修改时间，执行insert或update操作时自动赋值
 *
 * @PostLoad，实体对象查询之后
 * @PrePersist，实体对象保存之前
 * @PostPersist，实体对象保存之后
 * @PreUpdate，实体对象修改之前
 * @PostUpdate，实体对象修改之后
 * @PreRemove，实体对象删除之前
 * @PostRemove，实体对象删除之后
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:35:39
 */
@Audited
@MappedSuperclass
//@RevisionEntity(CustomRevisionListener.class)
@EntityListeners(AuditingEntityListener.class)
// @EntityListeners({AuditingEntityListener.class, CustomEntityAuditingListener.class})
public class JpaSuperEntity<I extends Serializable> implements Serializable {

   @Serial private static final long serialVersionUID = -3685249101751401211L;

   /**
    * id
    */
   //@Id
   //@GeneratedValue(strategy = GenerationType.IDENTITY)
   //@Column(name = "id", columnDefinition = "bigint not null comment 'id'")
   //private I id;
   @Id
   //@GenericGenerator(name = "snowFlakeIdGenerator", type = com.kuma.boot.data.jpa.bean.SnowFlakeIdGenerator.class)
   //@GeneratedValue(generator = "snowFlakeIdGenerator")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "`id`", updatable = false, columnDefinition = "bigint not null AUTO_INCREMENT comment 'id'")
   private Long id;

   /**
    * 创建人
    */
   @CreatedBy
   @Column(name = "`create_user`", columnDefinition = "varchar(256) not null default 'system' comment '创建人'")
   private String createUser;

   /**
    * 最后修改人
    */
   @LastModifiedBy
   @Column(name = "`modify_user`", columnDefinition = "varchar(256) not null default 'system' comment '最后修改人'")
   private String modifyUser;

   /**
    * 创建时间
    */
   @CreatedDate
   @Column(
           name = "`create_date`",
           updatable = false,
           columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间'")
   private LocalDateTime createDate;

   @LastModifiedDate
   @Column(
           name = "`modify_date`",
           columnDefinition =
                   "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间'")
   private LocalDateTime modifyDate;

   /**
    * 版本号
    */
   @Version
//    @RevisionNumber
   @Column(name = "`version`", columnDefinition = "int not null default 1 comment '版本号'")
   private Integer version = 1;

   /**
    * 是否删除 0-正常 1-删除
    */
   @Column(name = "`del_flag`", columnDefinition = "boolean not null DEFAULT false comment '是否删除 false-正常 true-删除'")
   private Boolean delFlag = false;

   public BooleanBuilder booleanBuilder() {
      return new BooleanBuilder();
   }

   /**
    * 保存和缺省验证组
    */
   public interface Save extends Default {}

   /**
    * 更新和缺省验证组
    */
   public interface Update extends Default {}

   public JpaSuperEntity(
           Long id,
           String createUser,
           String modifyUser,
           LocalDateTime createDate,
           LocalDateTime modifyDate,
           Integer version,
           Boolean delFlag) {
      this.id = id;
      this.createUser = createUser;
      this.modifyUser = modifyUser;
      this.createDate = createDate;
      this.modifyDate = modifyDate;
      this.version = version;
      this.delFlag = delFlag;
   }

   public JpaSuperEntity() {}

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getCreateUser() {
      return createUser;
   }

   public void setCreateUser(String createUser) {
      this.createUser = createUser;
   }

   public String getModifyUser() {
      return modifyUser;
   }

   public void setModifyUser(String modifyUser) {
      this.modifyUser = modifyUser;
   }

   public LocalDateTime getCreateDate() {
      return createDate;
   }

   public void setCreateDate(LocalDateTime createDate) {
      this.createDate = createDate;
   }

   public LocalDateTime getModifyDate() {
      return modifyDate;
   }

   public void setModifyDate(LocalDateTime modifyDate) {
      this.modifyDate = modifyDate;
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
}
