package com.kuma.boot.ddd.model.domain.service.specification;

public class NotSpecification extends AbstractSpecification {
   private Specification spec1;

   public NotSpecification(final Specification spec1) {
      this.spec1 = spec1;
   }

   public boolean isSatisfiedBy(final Object t) {
      return !this.spec1.isSatisfiedBy(t);
   }
}
