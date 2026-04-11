package com.kuma.boot.webagg.controller;

import com.kuma.boot.webagg.entity.SuperEntity;
import com.kuma.boot.webagg.service.BaseSuperService;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseSuperController<S extends BaseSuperService<T, I>, T extends SuperEntity<T, I>, I extends Serializable, QueryDTO, SaveDTO, UpdateDTO, QueryVO> extends BaseBusinessController<S, T, I> implements BaseQueryController<T, I, QueryDTO, QueryVO>, BaseSaveController<T, I, SaveDTO>, BaseUpdateController<T, I, UpdateDTO>, BaseDeleteController<T, I>, BaseBatchController<T, I, SaveDTO, UpdateDTO>, BaseExcelController<T, I, QueryDTO, QueryVO> {
   private Class<QueryVO> queryVOClass;

   public BaseSuperController() {
   }

   public Class<QueryVO> getQueryVOClass() {
      if (this.queryVOClass == null) {
         Type genericSuperclass = this.getClass().getGenericSuperclass();
         if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)genericSuperclass;
            this.queryVOClass = (Class)parameterizedType.getActualTypeArguments()[6];
         }
      }

      return this.queryVOClass;
   }
}
