package com.kuma.boot.data.jpa.fenix;

import com.kuma.boot.data.jpa.fenix.jpa.FenixJpaRepositoryFactoryBean;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({FenixJpaRepositoriesRegistrar.class})
public @interface EnableFenix {
   Class<?> repositoryFactoryBeanClass() default FenixJpaRepositoryFactoryBean.class;

   String[] value() default {};

   String[] basePackages() default {};

   Class<?>[] basePackageClasses() default {};

   ComponentScan.Filter[] includeFilters() default {};

   ComponentScan.Filter[] excludeFilters() default {};

   String repositoryImplementationPostfix() default "Impl";

   String namedQueriesLocation() default "";

   QueryLookupStrategy.Key queryLookupStrategy() default Key.CREATE_IF_NOT_FOUND;

   Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

   Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

   String entityManagerFactoryRef() default "entityManagerFactory";

   String transactionManagerRef() default "transactionManager";

   boolean considerNestedRepositories() default false;

   boolean enableDefaultTransactions() default true;

   BootstrapMode bootstrapMode() default BootstrapMode.DEFAULT;

   char escapeCharacter() default '\\';

   Class<? extends QueryEnhancerSelector> queryEnhancerSelector() default QueryEnhancerSelector.DefaultQueryEnhancerSelector.class;
}
