package com.kuma.boot.ddd.model.domain.service.specification;

public class OrSpecification extends AbstractSpecification {
   private Specification spec1;
   private Specification spec2;

   public OrSpecification(final Specification spec1, final Specification spec2) {
      this.spec1 = spec1;
      this.spec2 = spec2;
   }

   public boolean isSatisfiedBy(final Object t) {
      return this.spec1.isSatisfiedBy(t) || this.spec2.isSatisfiedBy(t);
   }
}
