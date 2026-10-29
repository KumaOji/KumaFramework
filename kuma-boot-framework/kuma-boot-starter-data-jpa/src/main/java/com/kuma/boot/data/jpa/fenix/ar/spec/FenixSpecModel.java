package com.kuma.boot.data.jpa.fenix.ar.spec;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.specification.FenixJpaSpecificationExecutor;
import com.kuma.boot.data.jpa.fenix.specification.FenixSpecification;
import com.kuma.boot.data.jpa.fenix.specification.predicate.FenixPredicate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public interface FenixSpecModel<T, R extends FenixJpaSpecificationExecutor<T>> extends SpecModel<T, R> {
   default void validExecutor(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof FenixJpaSpecificationExecutor)) {
         throw new FenixException(StringHelper.format("【Fenix 异常】获取到的 Spring Data JPA 的 Repository 接口【{}】不是真正的 FenixJpaSpecificationExecutor 接口。", repository.getClass().getName()));
      }
   }

   default Optional<T> findOne(FenixPredicate fenixPredicate) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).findOne(FenixSpecification.of(fenixPredicate));
   }

   default Optional<T> findOneOfBean(Object beanParam) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).findOne(FenixSpecification.ofBean(beanParam));
   }

   default List<T> findAll(FenixPredicate fenixPredicate) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).findAll(FenixSpecification.of(fenixPredicate));
   }

   default Page<T> findAll(FenixPredicate fenixPredicate, Pageable pageable) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).findAll((Specification)FenixSpecification.of(fenixPredicate), (Pageable)pageable);
   }

   default List<T> findAll(FenixPredicate fenixPredicate, Sort sort) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).findAll((Specification)FenixSpecification.of(fenixPredicate), (Sort)sort);
   }

   default List<T> findAllOfBean(Object beanParam) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).findAll(FenixSpecification.ofBean(beanParam));
   }

   default Page<T> findAllOfBean(Object beanParam, Pageable pageable) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).findAll((Specification)FenixSpecification.ofBean(beanParam), (Pageable)pageable);
   }

   default List<T> findAllOfBean(Object beanParam, Sort sort) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).findAll((Specification)FenixSpecification.ofBean(beanParam), (Sort)sort);
   }

   default long count(FenixPredicate fenixPredicate) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).count(FenixSpecification.of(fenixPredicate));
   }

   default long countOfBean(Object beanParam) {
      return ((FenixJpaSpecificationExecutor)this.getRepository()).count(FenixSpecification.ofBean(beanParam));
   }
}
