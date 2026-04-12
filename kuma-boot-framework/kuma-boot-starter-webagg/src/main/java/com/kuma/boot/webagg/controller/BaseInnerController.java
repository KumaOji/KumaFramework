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

package com.kuma.boot.webagg.controller;

import com.kuma.boot.web.annotation.InnerApi;
import com.kuma.boot.webagg.entity.SuperEntity;
import com.kuma.boot.webagg.service.BaseSuperService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 简单的实现了BaseController，为了获取注入 Service 和 实体类型
 *
 * <p>基类该类后，没有任何方法。 可以让业务Controller继承 SuperSimpleController 后，按需实现 *Controller 接口
 *
 * @param <S> Service
 * @param <T> 实体
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:15:37
 */
@InnerApi
public abstract class BaseInnerController<
        S extends BaseSuperService<T, I>, T extends SuperEntity<T, I>, I extends Serializable>
        implements BaseController<T, I> {

   private Class<T> entityClass;

   @Autowired
   private S service;

   @Override
   @SuppressWarnings("unchecked")
   public Class<T> getEntityClass() {
      if (entityClass == null) {
         Type genericSuperclass = this.getClass().getGenericSuperclass();
         if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            this.entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[1];
         }
      }
      return entityClass;
   }

   @Override
   public S service() {
      return service;
   }
}
