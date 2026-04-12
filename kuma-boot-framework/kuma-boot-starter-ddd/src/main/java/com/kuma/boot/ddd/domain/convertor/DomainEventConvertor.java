package com.kuma.boot.ddd.domain.convertor;

import com.kuma.boot.ddd.domain.entity.DomainEventDO;
import com.kuma.boot.ddd.domain.model.DomainEventA;

public class DomainEventConvertor {
   public static DomainEventDO toDataObject(DomainEventA domainEventA) {
      DomainEventDO domainEventDO = new DomainEventDO();
      return domainEventDO;
   }
}
