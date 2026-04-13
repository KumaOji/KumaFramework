package com.kuma.boot.data.jpa.simplestjpa.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
public class JpaConfig {
   public JpaConfig() {
   }

   @Bean
   public JPAQueryFactory jpaQueryFactory(@Autowired(required = false) EntityManager entityManager) {
      return new JPAQueryFactory(entityManager);
   }
}
