package com.kuma.boot.data.jpa.fenix.specification;

import com.kuma.boot.data.jpa.fenix.specification.predicate.FenixPredicate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FenixJpaSpecificationExecutor<T> extends JpaSpecificationExecutor<T> {
   default Optional<T> findOne(FenixPredicate fenixPredicate) {
      return this.findOne(FenixSpecification.of(fenixPredicate));
   }

   default Optional<T> findOneOfBean(Object beanParam) {
      return this.findOne(FenixSpecification.ofBean(beanParam));
   }

   default List<T> findAll(FenixPredicate fenixPredicate) {
      return this.findAll(FenixSpecification.of(fenixPredicate));
   }

   default Page<T> findAll(FenixPredicate fenixPredicate, Pageable pageable) {
      return this.findAll((Specification)FenixSpecification.of(fenixPredicate), (Pageable)pageable);
   }

   default List<T> findAll(FenixPredicate fenixPredicate, Sort sort) {
      return this.findAll((Specification)FenixSpecification.of(fenixPredicate), (Sort)sort);
   }

   default List<T> findAllOfBean(Object beanParam) {
      return this.findAll(FenixSpecification.ofBean(beanParam));
   }

   default Page<T> findAllOfBean(Object beanParam, Pageable pageable) {
      return this.findAll((Specification)FenixSpecification.ofBean(beanParam), (Pageable)pageable);
   }

   default List<T> findAllOfBean(Object beanParam, Sort sort) {
      return this.findAll((Specification)FenixSpecification.ofBean(beanParam), (Sort)sort);
   }

   default long count(FenixPredicate fenixPredicate) {
      return this.count(FenixSpecification.of(fenixPredicate));
   }

   default long countOfBean(Object beanParam) {
      return this.count(FenixSpecification.ofBean(beanParam));
   }
}
