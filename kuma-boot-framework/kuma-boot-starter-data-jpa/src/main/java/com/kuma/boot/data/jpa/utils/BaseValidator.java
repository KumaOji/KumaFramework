package com.kuma.boot.data.jpa.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

public final class BaseValidator {
   private static final Validator validator = ((HibernateValidatorConfiguration)((HibernateValidatorConfiguration)Validation.byProvider(HibernateValidator.class).configure()).failFast(false)).buildValidatorFactory().getValidator();

   private BaseValidator() {
   }

   public static List<String> validate(Object obj) {
      Set<ConstraintViolation<Object>> set = validator.validate(obj, new Class[0]);
      return (List)((Set)Optional.ofNullable(set).orElse(Collections.emptySet())).stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
   }

   public static boolean isValid(Object obj) {
      Set<ConstraintViolation<Object>> set = validator.validate(obj, new Class[0]);
      return CollectionUtils.isEmpty(set);
   }

   public static boolean isInvalid(Object obj) {
      return !isValid(obj);
   }
}
