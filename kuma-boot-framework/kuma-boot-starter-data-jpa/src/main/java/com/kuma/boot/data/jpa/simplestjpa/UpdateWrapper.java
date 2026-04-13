package com.kuma.boot.data.jpa.simplestjpa;

import cn.hutool.extra.spring.SpringUtil;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAUpdateClause;

public class UpdateWrapper {
   public UpdateWrapper() {
   }

   public static JPAUpdateClause of(EntityPath entityPath) {
      return ((IService)SpringUtil.getBean(IService.class)).updateChain(entityPath);
   }
}
