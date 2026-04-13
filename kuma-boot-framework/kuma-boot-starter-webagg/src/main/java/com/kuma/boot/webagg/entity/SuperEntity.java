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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.io.Serial;
import java.io.Serializable;

/**
 * SuperEntity
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-04 07:40:46
 */
@MappedSuperclass
public class SuperEntity<T extends SuperEntity<T, I>, I extends Serializable> extends Model<T> implements Serializable {

   @Serial
   private static final long serialVersionUID = -4603650115461757622L;

   @Id
   //@GenericGenerator(name = "snowFlakeIdGenerator", type = com.kuma.boot.data.jpa.bean.SnowFlakeIdGenerator.class)
   //@GeneratedValue(generator = "snowFlakeIdGenerator")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "`id`", updatable = false, columnDefinition = "bigint comment 'id'")
   @TableId(value = "id", type = IdType.AUTO)
   private I id;

   public SuperEntity() {}

   public SuperEntity(I id) {
      this.id = id;
   }

   public I getId() {
      return id;
   }

   public void setId(I id) {
      this.id = id;
   }

   public I id() {
      return id;
   }

   @SuppressWarnings("unchecked")
   public T id(I id) {
      this.id = id;
      return (T) this;
   }
}
