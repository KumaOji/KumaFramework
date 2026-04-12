package com.kuma.boot.ddd.model.domain.service.specification;

public abstract class AbstractSpecification implements Specification {
   public abstract boolean isSatisfiedBy(Object t);

   public Specification and(final Specification specification) {
      return new AndSpecification(this, specification);
   }

   public Specification or(final Specification specification) {
      return new OrSpecification(this, specification);
   }

   public Specification not(final Specification specification) {
      return new NotSpecification(specification);
   }
}
