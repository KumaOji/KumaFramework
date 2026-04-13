package com.kuma.boot.data.jpa.base.entity;

import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.groups.Default;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Audited
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class JpaSuperEntity<I extends Serializable> implements Serializable {
   private static final long serialVersionUID = -3685249101751401211L;
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   @Column(
      name = "`id`",
      updatable = false,
      columnDefinition = "bigint not null AUTO_INCREMENT comment 'id'"
   )
   private Long id;
   @CreatedBy
   @Column(
      name = "`create_user`",
      columnDefinition = "varchar(256) not null default 'system' comment '\u521b\u5efa\u4eba'"
   )
   private String createUser;
   @LastModifiedBy
   @Column(
      name = "`modify_user`",
      columnDefinition = "varchar(256) not null default 'system' comment '\u6700\u540e\u4fee\u6539\u4eba'"
   )
   private String modifyUser;
   @CreatedDate
   @Column(
      name = "`create_date`",
      updatable = false,
      columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP comment '\u521b\u5efa\u65f6\u95f4'"
   )
   private LocalDateTime createDate;
   @LastModifiedDate
   @Column(
      name = "`modify_date`",
      columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '\u66f4\u65b0\u65f6\u95f4'"
   )
   private LocalDateTime modifyDate;
   @Version
   @Column(
      name = "`version`",
      columnDefinition = "int not null default 1 comment '\u7248\u672c\u53f7'"
   )
   private Integer version = 1;
   @Column(
      name = "`del_flag`",
      columnDefinition = "boolean not null DEFAULT false comment '\u662f\u5426\u5220\u9664 false-\u6b63\u5e38 true-\u5220\u9664'"
   )
   private Boolean delFlag = false;

   public BooleanBuilder booleanBuilder() {
      return new BooleanBuilder();
   }

   public JpaSuperEntity(Long id, String createUser, String modifyUser, LocalDateTime createDate, LocalDateTime modifyDate, Integer version, Boolean delFlag) {
      this.id = id;
      this.createUser = createUser;
      this.modifyUser = modifyUser;
      this.createDate = createDate;
      this.modifyDate = modifyDate;
      this.version = version;
      this.delFlag = delFlag;
   }

   public JpaSuperEntity() {
   }

   public Long getId() {
      return this.id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getCreateUser() {
      return this.createUser;
   }

   public void setCreateUser(String createUser) {
      this.createUser = createUser;
   }

   public String getModifyUser() {
      return this.modifyUser;
   }

   public void setModifyUser(String modifyUser) {
      this.modifyUser = modifyUser;
   }

   public LocalDateTime getCreateDate() {
      return this.createDate;
   }

   public void setCreateDate(LocalDateTime createDate) {
      this.createDate = createDate;
   }

   public LocalDateTime getModifyDate() {
      return this.modifyDate;
   }

   public void setModifyDate(LocalDateTime modifyDate) {
      this.modifyDate = modifyDate;
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

   public interface Save extends Default {
   }

   public interface Update extends Default {
   }
}
