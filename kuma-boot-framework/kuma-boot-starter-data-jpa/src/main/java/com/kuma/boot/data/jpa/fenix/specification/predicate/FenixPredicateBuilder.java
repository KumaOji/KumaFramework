package com.kuma.boot.data.jpa.fenix.specification.predicate;

import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.PredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.BetweenPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.EndsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.EqualsPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.GreaterThanEqualPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.GreaterThanPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.InPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.IsNotNullPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.IsNullPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LessThanEqualPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LessThanPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LikePatternPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.LikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotBetweenPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotEndsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotEqualsPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotInPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotLikePatternPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotLikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.NotStartsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrBetweenPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrEndsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrEqualsPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrGreaterThanEqualPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrGreaterThanPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrInPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrIsNotNullPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrIsNullPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLessThanEqualPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLessThanPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLikePatternPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrLikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotBetweenPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotEndsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotEqualsPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotInPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotLikePatternPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotLikePredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrNotStartsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.OrStartsWithPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.impl.StartsWithPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FenixPredicateBuilder {
   private final CriteriaBuilder criteriaBuilder;
   private final From<?, ?> from;
   private final CriteriaQuery<?> criteriaQuery;
   private final List<Predicate> predicates;

   public CriteriaBuilder getCriteriaBuilder() {
      return this.criteriaBuilder;
   }

   public From<?, ?> getFrom() {
      return this.from;
   }

   public CriteriaQuery<?> getCriteriaQuery() {
      return this.criteriaQuery;
   }

   public List<Predicate> getPredicates() {
      return this.predicates;
   }

   public FenixPredicateBuilder(From<?, ?> from, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
      this.from = from;
      this.criteriaBuilder = criteriaBuilder;
      this.criteriaQuery = criteriaQuery;
      this.predicates = new ArrayList();
   }

   public List<Predicate> build() {
      return this.predicates;
   }

   public FenixPredicateBuilder andEquals(String fieldName, Object value) {
      this.predicates.add((new EqualsPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andEquals(String fieldName, Object value, boolean match) {
      return match ? this.andEquals(fieldName, value) : this;
   }

   public FenixPredicateBuilder orEquals(String fieldName, Object value) {
      this.predicates.add((new OrEqualsPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orEquals(String fieldName, Object value, boolean match) {
      return match ? this.orEquals(fieldName, value) : this;
   }

   public FenixPredicateBuilder andNotEquals(String fieldName, Object value) {
      this.predicates.add((new NotEqualsPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andNotEquals(String fieldName, Object value, boolean match) {
      return match ? this.andNotEquals(fieldName, value) : this;
   }

   public FenixPredicateBuilder orNotEquals(String fieldName, Object value) {
      this.predicates.add((new OrNotEqualsPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orNotEquals(String fieldName, Object value, boolean match) {
      return match ? this.orNotEquals(fieldName, value) : this;
   }

   public FenixPredicateBuilder andGreaterThan(String fieldName, Object value) {
      this.predicates.add((new GreaterThanPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andGreaterThan(String fieldName, Object value, boolean match) {
      return match ? this.andGreaterThan(fieldName, value) : this;
   }

   public FenixPredicateBuilder orGreaterThan(String fieldName, Object value) {
      this.predicates.add((new OrGreaterThanPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orGreaterThan(String fieldName, Object value, boolean match) {
      return match ? this.orGreaterThan(fieldName, value) : this;
   }

   public FenixPredicateBuilder andGreaterThanEqual(String fieldName, Object value) {
      this.predicates.add((new GreaterThanEqualPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andGreaterThanEqual(String fieldName, Object value, boolean match) {
      return match ? this.andGreaterThanEqual(fieldName, value) : this;
   }

   public FenixPredicateBuilder orGreaterThanEqual(String fieldName, Object value) {
      this.predicates.add((new OrGreaterThanEqualPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orGreaterThanEqual(String fieldName, Object value, boolean match) {
      return match ? this.orGreaterThanEqual(fieldName, value) : this;
   }

   public FenixPredicateBuilder andLessThan(String fieldName, Object value) {
      this.predicates.add((new LessThanPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andLessThan(String fieldName, Object value, boolean match) {
      return match ? this.andLessThan(fieldName, value) : this;
   }

   public FenixPredicateBuilder orLessThan(String fieldName, Object value) {
      this.predicates.add((new OrLessThanPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orLessThan(String fieldName, Object value, boolean match) {
      return match ? this.orLessThan(fieldName, value) : this;
   }

   public FenixPredicateBuilder andLessThanEqual(String fieldName, Object value) {
      this.predicates.add((new LessThanEqualPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andLessThanEqual(String fieldName, Object value, boolean match) {
      return match ? this.andLessThanEqual(fieldName, value) : this;
   }

   public FenixPredicateBuilder orLessThanEqual(String fieldName, Object value) {
      this.predicates.add((new OrLessThanEqualPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orLessThanEqual(String fieldName, Object value, boolean match) {
      return match ? this.orLessThanEqual(fieldName, value) : this;
   }

   public FenixPredicateBuilder andBetween(String fieldName, Object startValue, Object endValue) {
      this.predicates.add((new BetweenPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, new Object[]{startValue, endValue}));
      return this;
   }

   public FenixPredicateBuilder andBetween(String fieldName, Object startValue, Object endValue, boolean match) {
      return match ? this.andBetween(fieldName, startValue, endValue) : this;
   }

   public FenixPredicateBuilder orBetween(String fieldName, Object startValue, Object endValue) {
      this.predicates.add((new OrBetweenPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, new Object[]{startValue, endValue}));
      return this;
   }

   public FenixPredicateBuilder orBetween(String fieldName, Object startValue, Object endValue, boolean match) {
      return match ? this.orBetween(fieldName, startValue, endValue) : this;
   }

   public FenixPredicateBuilder andNotBetween(String fieldName, Object startValue, Object endValue) {
      this.predicates.add((new NotBetweenPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, new Object[]{startValue, endValue}));
      return this;
   }

   public FenixPredicateBuilder andNotBetween(String fieldName, Object startValue, Object endValue, boolean match) {
      return match ? this.andNotBetween(fieldName, startValue, endValue) : this;
   }

   public FenixPredicateBuilder orNotBetween(String fieldName, Object startValue, Object endValue) {
      this.predicates.add((new OrNotBetweenPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, new Object[]{startValue, endValue}));
      return this;
   }

   public FenixPredicateBuilder orNotBetween(String fieldName, Object startValue, Object endValue, boolean match) {
      return match ? this.orNotBetween(fieldName, startValue, endValue) : this;
   }

   public FenixPredicateBuilder andLike(String fieldName, Object value) {
      this.predicates.add((new LikePredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andLike(String fieldName, Object value, boolean match) {
      return match ? this.andLike(fieldName, value) : this;
   }

   public FenixPredicateBuilder orLike(String fieldName, Object value) {
      this.predicates.add((new OrLikePredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orLike(String fieldName, Object value, boolean match) {
      return match ? this.orLike(fieldName, value) : this;
   }

   public FenixPredicateBuilder andNotLike(String fieldName, Object value) {
      this.predicates.add((new NotLikePredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andNotLike(String fieldName, Object value, boolean match) {
      return match ? this.andNotLike(fieldName, value) : this;
   }

   public FenixPredicateBuilder orNotLike(String fieldName, Object value) {
      this.predicates.add((new OrNotLikePredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orNotLike(String fieldName, Object value, boolean match) {
      return match ? this.orNotLike(fieldName, value) : this;
   }

   public FenixPredicateBuilder andStartsWith(String fieldName, Object value) {
      this.predicates.add((new StartsWithPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andStartsWith(String fieldName, Object value, boolean match) {
      return match ? this.andStartsWith(fieldName, value) : this;
   }

   public FenixPredicateBuilder orStartsWith(String fieldName, Object value) {
      this.predicates.add((new OrStartsWithPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orStartsWith(String fieldName, Object value, boolean match) {
      return match ? this.orStartsWith(fieldName, value) : this;
   }

   public FenixPredicateBuilder andNotStartsWith(String fieldName, Object value) {
      this.predicates.add((new NotStartsWithPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andNotStartsWith(String fieldName, Object value, boolean match) {
      return match ? this.andNotStartsWith(fieldName, value) : this;
   }

   public FenixPredicateBuilder orNotStartsWith(String fieldName, Object value) {
      this.predicates.add((new OrNotStartsWithPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orNotStartsWith(String fieldName, Object value, boolean match) {
      return match ? this.orNotStartsWith(fieldName, value) : this;
   }

   public FenixPredicateBuilder andEndsWith(String fieldName, Object value) {
      this.predicates.add((new EndsWithPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andEndsWith(String fieldName, Object value, boolean match) {
      return match ? this.andEndsWith(fieldName, value) : this;
   }

   public FenixPredicateBuilder orEndsWith(String fieldName, Object value) {
      this.predicates.add((new OrEndsWithPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orEndsWith(String fieldName, Object value, boolean match) {
      return match ? this.orEndsWith(fieldName, value) : this;
   }

   public FenixPredicateBuilder andNotEndsWith(String fieldName, Object value) {
      this.predicates.add((new NotEndsWithPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andNotEndsWith(String fieldName, Object value, boolean match) {
      return match ? this.andNotEndsWith(fieldName, value) : this;
   }

   public FenixPredicateBuilder orNotEndsWith(String fieldName, Object value) {
      this.predicates.add((new OrNotEndsWithPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orNotEndsWith(String fieldName, Object value, boolean match) {
      return match ? this.orNotEndsWith(fieldName, value) : this;
   }

   public FenixPredicateBuilder andLikePattern(String fieldName, String pattern) {
      this.predicates.add((new LikePatternPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, pattern));
      return this;
   }

   public FenixPredicateBuilder andLikePattern(String fieldName, String pattern, boolean match) {
      return match ? this.andLikePattern(fieldName, pattern) : this;
   }

   public FenixPredicateBuilder orLikePattern(String fieldName, String pattern) {
      this.predicates.add((new OrLikePatternPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, pattern));
      return this;
   }

   public FenixPredicateBuilder orLikePattern(String fieldName, String pattern, boolean match) {
      return match ? this.orLikePattern(fieldName, pattern) : this;
   }

   public FenixPredicateBuilder andNotLikePattern(String fieldName, String pattern) {
      this.predicates.add((new NotLikePatternPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, pattern));
      return this;
   }

   public FenixPredicateBuilder andNotLikePattern(String fieldName, String pattern, boolean match) {
      return match ? this.andNotLikePattern(fieldName, pattern) : this;
   }

   public FenixPredicateBuilder orNotLikePattern(String fieldName, String pattern) {
      this.predicates.add((new OrNotLikePatternPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, pattern));
      return this;
   }

   public FenixPredicateBuilder orNotLikePattern(String fieldName, String pattern, boolean match) {
      return match ? this.orNotLikePattern(fieldName, pattern) : this;
   }

   public FenixPredicateBuilder andIn(String fieldName, Collection<?> value) {
      this.predicates.add((new InPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andIn(String fieldName, Collection<?> value, boolean match) {
      return match ? this.andIn(fieldName, value) : this;
   }

   public FenixPredicateBuilder andIn(String fieldName, Object[] value) {
      this.predicates.add((new InPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andIn(String fieldName, Object[] value, boolean match) {
      return match ? this.andIn(fieldName, value) : this;
   }

   public FenixPredicateBuilder orIn(String fieldName, Collection<?> value) {
      this.predicates.add((new OrInPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orIn(String fieldName, Collection<?> value, boolean match) {
      return match ? this.orIn(fieldName, value) : this;
   }

   public FenixPredicateBuilder orIn(String fieldName, Object[] value) {
      this.predicates.add((new OrInPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orIn(String fieldName, Object[] value, boolean match) {
      return match ? this.orIn(fieldName, value) : this;
   }

   public FenixPredicateBuilder andNotIn(String fieldName, Collection<?> value) {
      this.predicates.add((new NotInPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andNotIn(String fieldName, Collection<?> value, boolean match) {
      return match ? this.andNotIn(fieldName, value) : this;
   }

   public FenixPredicateBuilder andNotIn(String fieldName, Object[] value) {
      this.predicates.add((new NotInPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder andNotIn(String fieldName, Object[] value, boolean match) {
      return match ? this.andNotIn(fieldName, value) : this;
   }

   public FenixPredicateBuilder orNotIn(String fieldName, Collection<?> value) {
      this.predicates.add((new OrNotInPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orNotIn(String fieldName, Collection<?> value, boolean match) {
      return match ? this.orNotIn(fieldName, value) : this;
   }

   public FenixPredicateBuilder orNotIn(String fieldName, Object[] value) {
      this.predicates.add((new OrNotInPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder orNotIn(String fieldName, Object[] value, boolean match) {
      return match ? this.orNotIn(fieldName, value) : this;
   }

   public FenixPredicateBuilder andIsNull(String fieldName) {
      this.predicates.add((new IsNullPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, fieldName));
      return this;
   }

   public FenixPredicateBuilder andIsNull(String fieldName, boolean match) {
      return match ? this.andIsNull(fieldName) : this;
   }

   public FenixPredicateBuilder orIsNull(String fieldName) {
      this.predicates.add((new OrIsNullPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, fieldName));
      return this;
   }

   public FenixPredicateBuilder orIsNull(String fieldName, boolean match) {
      return match ? this.orIsNull(fieldName) : this;
   }

   public FenixPredicateBuilder andIsNotNull(String fieldName) {
      this.predicates.add((new IsNotNullPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, fieldName));
      return this;
   }

   public FenixPredicateBuilder andIsNotNull(String fieldName, boolean match) {
      return match ? this.andIsNotNull(fieldName) : this;
   }

   public FenixPredicateBuilder orIsNotNull(String fieldName) {
      this.predicates.add((new OrIsNotNullPredicateHandler()).buildPredicate(this.criteriaBuilder, this.from, fieldName, fieldName));
      return this;
   }

   public FenixPredicateBuilder orIsNotNull(String fieldName, boolean match) {
      return match ? this.orIsNotNull(fieldName) : this;
   }

   public FenixPredicateBuilder doAny(String fieldName, Object value, AbstractPredicateHandler handler) {
      this.predicates.add(handler.buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder doAny(String fieldName, Object value, AbstractPredicateHandler handler, boolean match) {
      return match ? this.doAny(fieldName, value, handler) : this;
   }

   public FenixPredicateBuilder doAny(String fieldName, Object value, PredicateHandler handler) {
      this.predicates.add(handler.buildPredicate(this.criteriaBuilder, this.from, fieldName, value));
      return this;
   }

   public FenixPredicateBuilder doAny(String fieldName, Object value, PredicateHandler handler, boolean match) {
      return match ? this.doAny(fieldName, value, handler) : this;
   }
}
