package com.kuma.boot.ddd.model.domain.service.specification;

public interface Specification {
   boolean isSatisfiedBy(Object t);

   Specification and(Specification specification);

   Specification or(Specification specification);

   Specification not(Specification specification);
}
