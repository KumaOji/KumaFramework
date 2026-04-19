/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.jpa.extend;

import jakarta.persistence.EntityManager;

import java.util.Optional;

import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryImpl;
import org.springframework.data.envers.repository.support.ReflectionRevisionEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

/**
 * RevisionRepositoryFactory
 *
 * @author kuma
 * @version 2026.03
 * @since 2025-12-19 09:30:45
 */
public class RevisionRepositoryFactory<T, ID, N extends Number & Comparable<N>> extends JpaRepositoryFactory {

   private final RevisionEntityInformation revisionEntityInformation;
   private final EntityManager entityManager;

   /**
    * Creates a new  using the given {@link EntityManager} and revision entity class.
    *
    * @param entityManager must not be {@literal null}.
    * @param revisionEntityClass can be {@literal null}, will default to {@link DefaultRevisionEntity}.
    */
   public RevisionRepositoryFactory( EntityManager entityManager, Class<?> revisionEntityClass ) {

      super(entityManager);

      this.entityManager = entityManager;
      this.revisionEntityInformation =
              Optional.ofNullable(revisionEntityClass) //
                      .filter(it -> !it.equals(DefaultRevisionEntity.class)) //
                      .<RevisionEntityInformation>map(
                              ReflectionRevisionEntityInformation::new)
                      .orElseGet(DefaultRevisionEntityInformation::new);
   }

   @Override
   protected RepositoryComposition.RepositoryFragments getRepositoryFragments(
           RepositoryMetadata metadata ) {
      return super.getRepositoryFragments(metadata);
//        Object fragmentImplementation =
//                getTargetRepositoryViaReflection( //
//                        EnversRevisionRepositoryImpl.class, //
//                        getEntityInformation(metadata.getDomainType()), //
//                        revisionEntityInformation, //
//                        entityManager //
//                        );
//
//        return RepositoryComposition.RepositoryFragments //
//                .just(fragmentImplementation) //
//                .append(super.getRepositoryFragments(metadata));
   }
}
