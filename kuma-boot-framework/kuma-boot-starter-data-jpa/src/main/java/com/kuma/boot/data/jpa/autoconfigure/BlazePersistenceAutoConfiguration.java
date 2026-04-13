package com.kuma.boot.data.jpa.autoconfigure;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@AutoConfiguration
@ConditionalOnClass(CriteriaBuilderFactory.class)
public class BlazePersistenceAutoConfiguration {
   @PersistenceUnit
   private EntityManagerFactory entityManagerFactory;

   public BlazePersistenceAutoConfiguration() {
   }

   @Bean
   @Scope("singleton")
   @Lazy(false)
   public CriteriaBuilderFactory createCriteriaBuilderFactory() {
      CriteriaBuilderConfiguration config = Criteria.getDefault();
      return config.createCriteriaBuilderFactory(this.entityManagerFactory);
   }
}
