package com.kuma.boot.ddd.domain.service.impl;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.kuma.boot.data.datasource.tx.TransactionalUtils;
import com.kuma.boot.ddd.domain.convertor.DomainEventConvertor;
import com.kuma.boot.ddd.domain.entity.DomainEventDO;
import com.kuma.boot.ddd.domain.mapper.DomainEventMapper;
import com.kuma.boot.ddd.domain.model.DomainEventA;
import com.kuma.boot.ddd.domain.service.DomainEventService;
import org.springframework.stereotype.Service;

@Service
public class DomainEventServiceImpl implements DomainEventService {
   private static final String DOMAIN = "domain";
   private final DomainEventMapper domainEventMapper;
   private final TransactionalUtils transactionalUtils;

   public DomainEventServiceImpl(DomainEventMapper domainEventMapper, TransactionalUtils transactionalUtils) {
      this.domainEventMapper = domainEventMapper;
      this.transactionalUtils = transactionalUtils;
   }

   public void create(DomainEventA domainEventA) {
      try {
         DynamicDataSourceContextHolder.push("domain");
         this.transactionalUtils.defaultExecuteWithoutResult((r) -> {
            try {
               DomainEventDO eventDO = DomainEventConvertor.toDataObject(domainEventA);
               this.domainEventMapper.insert(eventDO);
            } catch (Exception var4) {
               r.setRollbackOnly();
            }

         });
      } finally {
         DynamicDataSourceContextHolder.clear();
      }

   }

   public Long countById(Long id) {
      Long var2;
      try {
         DynamicDataSourceContextHolder.push("domain");
         var2 = 0L;
      } finally {
         DynamicDataSourceContextHolder.clear();
      }

      return var2;
   }

   public void deleteOldByServiceIdOfThreeMonths(String serviceId) {
      try {
         DynamicDataSourceContextHolder.push("domain");
         this.transactionalUtils.defaultExecuteWithoutResult((r) -> {
            try {
               this.domainEventMapper.deleteOldByServiceIdOfThreeMonths(serviceId);
            } catch (Exception var4) {
               r.setRollbackOnly();
            }

         });
      } finally {
         DynamicDataSourceContextHolder.clear();
      }

   }
}
