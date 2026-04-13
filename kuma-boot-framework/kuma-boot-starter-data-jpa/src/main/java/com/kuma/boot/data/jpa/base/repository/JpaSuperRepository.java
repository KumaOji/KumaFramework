package com.kuma.boot.data.jpa.base.repository;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.persistence.QueryHint;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.QueryRewriter;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface JpaSuperRepository<T, I extends Serializable> extends JpaRepository<T, I>, JpaSpecificationExecutor<T>, QuerydslPredicateExecutor<T>, QueryRewriter {
   default String rewrite(String query, Sort sort) {
      LogUtils.info("query rewriter sql : {}", new Object[]{query});
      return query;
   }

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   List<T> findAll();

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   List<T> findAll(Sort sort);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   Optional<T> findOne(Specification<T> specification);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   List<T> findAll(Specification<T> specification);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   Page<T> findAll(Specification<T> specification, Pageable pageable);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   List<T> findAll(Specification<T> specification, Sort sort);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   long count(Specification<T> specification);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   Page<T> findAll(Pageable pageable);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   Optional<T> findById(I id);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   long count();

   @Transactional
   void deleteById(I id);
}
