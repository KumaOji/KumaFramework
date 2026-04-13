package com.kuma.boot.data.jpa.simplestjpa;

import cn.hutool.extra.spring.SpringUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class QueryWrapper {
   public QueryWrapper() {
   }

   public static JPAQueryFactory of() {
      return ((IService)SpringUtil.getBean(IService.class)).queryChain();
   }
}
