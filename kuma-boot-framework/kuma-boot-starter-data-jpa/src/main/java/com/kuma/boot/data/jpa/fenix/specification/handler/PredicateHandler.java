package com.kuma.boot.data.jpa.fenix.specification.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

@FunctionalInterface
public interface PredicateHandler {
   Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value);
}
