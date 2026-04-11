package com.kuma.boot.webagg.controller;

import com.kuma.boot.web.annotation.BusinessApi;
import com.kuma.boot.webagg.entity.SuperEntity;
import com.kuma.boot.webagg.service.BaseSuperService;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.beans.factory.annotation.Autowired;

@BusinessApi
public abstract class BaseBusinessController<S extends BaseSuperService<T, I>, T extends SuperEntity<T, I>, I extends Serializable> implements BaseController<T, I> {
   private Class<T> entityClass;
   @Autowired
   private S service;

   public BaseBusinessController() {
   }

   public Class<T> getEntityClass() {
      if (this.entityClass == null) {
         Type genericSuperclass = this.getClass().getGenericSuperclass();
         if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)genericSuperclass;
            this.entityClass = (Class)parameterizedType.getActualTypeArguments()[1];
         }
      }

      return this.entityClass;
   }

   public S service() {
      return this.service;
   }
}
