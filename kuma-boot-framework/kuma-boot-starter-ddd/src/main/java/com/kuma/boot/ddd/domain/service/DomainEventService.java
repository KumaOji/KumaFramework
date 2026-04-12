package com.kuma.boot.ddd.domain.service;

import com.kuma.boot.ddd.domain.model.DomainEventA;

public interface DomainEventService {
   void create(DomainEventA domainEventA);

   Long countById(Long id);

   void deleteOldByServiceIdOfThreeMonths(String serviceId);
}
