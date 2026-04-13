package com.kuma.boot.data.jpa.fenix.specification.predicate;

import jakarta.persistence.criteria.Predicate;
import java.util.List;

@FunctionalInterface
public interface FenixPredicate {
   List<Predicate> toPredicate(FenixPredicateBuilder fenixPredicateBuilder);
}
