package com.kuma.boot.data.jpa.tenant;

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
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface BaseRepository<E extends Entity, ID extends Serializable> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {
   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   List<E> findAll();

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   List<E> findAll(Sort sort);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   Optional<E> findOne(Specification<E> specification);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   List<E> findAll(Specification<E> specification);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   Page<E> findAll(Specification<E> specification, Pageable pageable);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   List<E> findAll(Specification<E> specification, Sort sort);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   long count(Specification<E> specification);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   Page<E> findAll(Pageable pageable);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   Optional<E> findById(ID id);

   @QueryHints({@QueryHint(
   name = "org.hibernate.cacheable",
   value = "true"
)})
   long count();

   @Transactional
   void deleteById(ID id);
}
