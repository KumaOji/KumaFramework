package com.kuma.boot.data.jpa.base.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.LockModeType;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

public interface JpaExtendRepository<T, I extends Serializable> {
   JdbcClient jdbcClient();

   JdbcTemplate jdbcTemplate();

   Page<T> findPageable(Predicate predicate, Pageable pageable, OrderSpecifier<?>... orders);

   int countByPredicate(Predicate predicate);

   Boolean existsByPredicate(Predicate predicate);

   List<T> fetch(Predicate predicate);

   T fetchOne(Predicate predicate);

   int fetchCount(Predicate predicate);

   List<?> find(Predicate predicate, Expression<?> expr, OrderSpecifier<?>... o);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   Optional<T> findWithLockingById(I id);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   List<T> findAllWithLockingByIdIn(Collection<I> ids);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   <S extends T> Optional<S> findOneWithLocking(Example<S> example);

   T findWithLockingWithEm(I id);

   List<T> findAllWithLockingWithEm(List<I> ids);
}
