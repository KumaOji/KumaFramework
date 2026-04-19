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

package com.kuma.boot.data.jpa.simplestjpa.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @since 2023/7/20 15:21
 * @webSite https://github.com/coder-amiao
 * 每当你保存或更新实体时，createdDate 和 lastModifiedDate 字段就会自动更新。
 * @CreatedDate 只在实体首次持久化时设置，而 @LastModifiedDate 在每次更新实体时都会被设置。
 */
@EnableJpaAuditing
public class JpaConfig {
   // 让Spring管理JPAQueryFactory
   @Bean
   public JPAQueryFactory jpaQueryFactory(
           @Autowired(required = false) EntityManager entityManager) {
      return new JPAQueryFactory(entityManager);
   }
}
