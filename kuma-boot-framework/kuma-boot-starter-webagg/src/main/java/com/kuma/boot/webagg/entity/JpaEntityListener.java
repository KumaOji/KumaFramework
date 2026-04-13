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

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

/**
 * 抽象侦听器
 *
 * @author shuigedeng
 * @version 2022.09
 * @since 2022-10-21 11:59:54
 */
public class JpaEntityListener {

   /**
    * 在新实体持久化之前（添加到EntityManager）
    *
    * @param object 对象
    * @since 2022-10-21 11:59:54
    */
   @PrePersist
   public void prePersist(Object object) {
      LogUtils.info(" AbstractListener prePersis: {}", object);

      // LogUtils.info("进行insert之前");
      // if(entity instanceof TestEntity) {
      // 	LogUtils.info(entity.toString());
      // 	CommonField commonField = ((TestEntity) entity).getCommonField();
      // 	if(ObjectUtils.isEmpty(commonField)) {
      // 		commonField=new CommonField();
      // 		commonField.setCreateTime(new Date());
      // 		commonField.setUpdateTime(new Date());
      // 		commonField.setCreateUserId("111");
      // 		commonField.setCreateUserName("ccc");
      // 		((TestEntity) entity).setCommonField(commonField);
      // 	}
      // 	LogUtils.info(entity.toString());
      // }
   }

   /**
    * 在数据库中存储新实体（在commit或期间flush）
    *
    * @param object 对象
    * @since 2022-10-21 11:59:54
    */
   @PostPersist
   public void postPersist(Object object) {
      LogUtils.info("AbstractListener postPersist: {}", object);
   }

   /**
    * 从数据库中检索实体后。
    *
    * @param object 对象
    * @since 2022-10-21 11:59:55
    */
   @PostLoad
   public void postLoad(Object object) {
      LogUtils.info("AbstractListener postLoad: {}", object);
   }

   /**
    * 当一个实体被识别为被修改时EntityManager
    *
    * @param object 对象
    * @since 2022-10-21 11:59:54
    */
   @PreUpdate
   public void preUpdate(Object object) {
      LogUtils.info("AbstractListener preUpdate: {}", object);
   }

   /**
    * 更新数据库中的实体（在commit或期间flush）
    *
    * @param object 对象
    * @since 2022-10-21 11:59:54
    */
   @PostUpdate
   public void postUpdate(Object object) {
      LogUtils.info("AbstractListener postUpdate: {}", object);
   }

   /**
    * 在EntityManager中标记要删除的实体时
    *
    * @param object 对象
    * @since 2022-10-21 11:59:54
    */
   @PreRemove
   public void preRemove(Object object) {
      LogUtils.info("AbstractListener preRemove: {}", object);
   }

   /**
    * 从数据库中删除实体（在commit或期间flush）
    *
    * @param object 对象
    * @since 2022-10-21 11:59:55
    */
   @PostRemove
   public void postRemove(Object object) {
      LogUtils.info("AbstractListener postRemove: {}", object);
   }

   /**
    * 前摧毁
    *
    * @param object 对象
    * @since 2022-10-21 11:59:54
    */
   @PreDestroy
   public void preDestroy(Object object) {
      LogUtils.info("AbstractListener preDestroy: {}", object);
   }
}
