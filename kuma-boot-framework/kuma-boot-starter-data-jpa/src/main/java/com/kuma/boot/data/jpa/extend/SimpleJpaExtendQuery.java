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
import jakarta.persistence.Query;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.QueryUtils;

/**
 * SimpleJpaExtendQuery
 *
 * @author kuma
 * @version 2026.03
 * @since 2025-12-19 09:30:45
 */
public class SimpleJpaExtendQuery extends AbstractJpaExtendQuery {

   /**
    * Creates a new {@link AbstractJpaQuery} from the given {@link JpaQueryMethod}.
    */
   public SimpleJpaExtendQuery( JpaQueryMethod method, EntityManager em, MyQuery myQuery ) {
      super(method, em, myQuery);
   }

   @Override
   protected Query createJpaQuery( String sortedQueryString ) {
      Class<?> objectType = getQueryMethod().getReturnedObjectType();

      Query query;

      if (getQueryMethod().isQueryForEntity()) {
         query = em.createQuery(sortedQueryString, objectType);
      } else {
         query = em.createQuery(sortedQueryString);
         //  query.unwrap(QueryImpl.class).setResultTransformer(new
         // JpaExtendResultTransformer(objectType));
      }
      return query;
   }

   @Override
   protected Query createJpaCountQuery( String queryString ) {
      if (StringUtils.isEmpty(declaredQuery.getCountQueryString())) {
         // 没有手动指定 count 语句
         queryString =
                 QueryUtils.createCountQueryFor(queryString, declaredQuery.getQueryString(),
                         declaredQuery.isNativeQuery());
      }

      Query query = em.createQuery(queryString, Long.class);
      return query;
   }

   @Override
   public boolean hasDeclaredCountQuery() {
      return false;
   }
}
