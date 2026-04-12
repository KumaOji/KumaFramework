package com.kuma.boot.ddd.util.validation;

import com.kuma.boot.ddd.model.exception.ValidateException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Validates {
   private static final String DOT = ".";
   private static final String SEPARATOR_COLON = ": ";
   private static final String SEPARATOR_COMMA = ", ";
   private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
   public static BiFunction errorToExceptionFunc = (errCode, errMessage) -> (RuntimeException)Optional.ofNullable(errCode).map((errCodeInner) -> new ValidateException(errMessage)).orElse(new ValidateException(errMessage));
   private static final ThreadLocal validationExceptionHolder = new ThreadLocal();

   public static void validateEntity(Object object, Class... groups) {
      Set<ConstraintViolation<T>> constraintViolations = validator.validate(object, groups);
      RuntimeException validationException = getValidationException(true);
      if (Objects.nonNull(validationException)) {
         throw validationException;
      } else if (null != constraintViolations && !constraintViolations.isEmpty()) {
         throw (RuntimeException)errorToExceptionFunc.apply((Object)null, convertConstraintViolations(constraintViolations));
      }
   }

   public static void setValidationException(RuntimeException validationException) {
      validationExceptionHolder.set(validationException);
   }

   public static RuntimeException getValidationException(Boolean removeException) {
      RuntimeException validationException = (RuntimeException)validationExceptionHolder.get();
      if (Objects.nonNull(validationException) && Boolean.TRUE.equals(removeException)) {
         validationExceptionHolder.remove();
      }

      return validationException;
   }

   public static void setValidator(Validator validator) {
      Validates.validator = validator;
   }

   public static String convertConstraintViolations(Set constraintViolations) {
      return null != constraintViolations && !constraintViolations.isEmpty() ? (String)constraintViolations.stream().map(Validates::convertConstrainViolation).collect(Collectors.joining(", ")) : null;
   }

   public static String convertConstraintViolationsUnknownType(Set constraintViolations) {
      return null != constraintViolations && !constraintViolations.isEmpty() ? (String)constraintViolations.stream().map(Validates::convertConstrainViolation).collect(Collectors.joining(", ")) : null;
   }

   public static String convertConstrainViolation(ConstraintViolation constraintViolation) {
      String rootBeanName = constraintViolation.getRootBeanClass().getSimpleName();
      String path = constraintViolation.getPropertyPath().toString();
      String errMsg = constraintViolation.getMessage();
      return String.format("%s.%s:%s", rootBeanName, path, errMsg);
   }

   public static void isTrue(boolean expression, String errCode, String errMessage) {
      if (!Boolean.TRUE.equals(expression)) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void isTrue(boolean expression, String errMessage) {
      isTrue(expression, (String)null, errMessage);
   }

   public static void isTrue(boolean expression) {
      isTrue(expression, "[Assertion failed] Must be true");
   }

   public static void isFalse(boolean expression, String errCode, String errMessage) {
      if (!Boolean.FALSE.equals(expression)) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void isFalse(boolean expression, String errMessage) {
      isFalse(expression, (String)null, errMessage);
   }

   public static void isFalse(boolean expression) {
      isFalse(expression, "[Assertion failed] Must be false");
   }

   public static void notNull(Object object, String errCode, String errMessage) {
      if (object == null) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void notNull(Object object, String errMessage) {
      notNull(object, (String)null, errMessage);
   }

   public static void notNull(Object object) {
      notNull(object, "[Assertion failed] Must not null");
   }

   public static void notBlank(String str, String errCode, String errMessage) {
      if (str == null || str.trim().length() <= 0) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void notBlank(String str, String errMessage) {
      notBlank(str, (String)null, errMessage);
   }

   public static void notBlank(String str) {
      notBlank(str, "[Assertion failed] Must not blank");
   }

   public static void notEmpty(String str, String errCode, String errMessage) {
      if (str == null || str.isEmpty()) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void notEmpty(String str, String errMessage) {
      notEmpty((String)str, (String)null, errMessage);
   }

   public static void notEmpty(String str) {
      notEmpty(str, "[Assertion failed] Must not empty");
   }

   public static void notEmpty(Collection collection, String errCode, String errMessage) {
      if (collection == null || collection.isEmpty()) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void notEmpty(Collection collection, String errMessage) {
      notEmpty((Collection)collection, (String)null, errMessage);
   }

   public static void notEmpty(Collection collection) {
      notEmpty(collection, "[Assertion failed] Collection must not be empty: it must contain at least 1 element");
   }

   public static void notEmpty(Map map, String errCode, String errMessage) {
      if (map == null || map.isEmpty()) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void notEmpty(Map map, String errMessage) {
      notEmpty((Map)map, (String)null, errMessage);
   }

   public static void notEmpty(Map map) {
      notEmpty(map, "[Assertion failed] Map must not be empty: it must contain at least one entry");
   }

   public static void range(Short num, Short min, Short max, String errCode, String errMessage) {
      Boolean notInRange = null != num && (num < min || num > max);
      if (notInRange) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void range(Short num, Short min, Short max, String errMessage) {
      range((Short)num, (Short)min, (Short)max, (String)null, errMessage);
   }

   public static void range(Short num, Short min, Short max) {
      range(num, min, max, "[Assertion failed] Must not exceed range");
   }

   public static void min(Short num, Short min, String errCode, String errMessage) {
      if (null != num && num < min) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void min(Short num, Short min, String errMessage) {
      min((Short)num, (Short)min, (String)null, errMessage);
   }

   public static void min(Short num, Short min) {
      min(num, min, "[Assertion failed] Must not less than min");
   }

   public static void max(Short num, Short max, String errCode, String errMessage) {
      if (null != num && num > max) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void max(Short num, Short max, String errMessage) {
      max((Short)num, (Short)max, (String)null, errMessage);
   }

   public static void max(Short num, Short max) {
      max(num, max, "[Assertion failed] Must not greater than max");
   }

   public static void range(Integer num, Integer min, Integer max, String errCode, String errMessage) {
      Boolean notInRange = null != num && (num < min || num > max);
      if (notInRange) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void range(Integer num, Integer min, Integer max, String errMessage) {
      range((Integer)num, (Integer)min, (Integer)max, (String)null, errMessage);
   }

   public static void range(Integer num, Integer min, Integer max) {
      range(num, min, max, "[Assertion failed] Must not exceed range");
   }

   public static void min(Integer num, Integer min, String errCode, String errMessage) {
      if (null != num && num < min) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void min(Integer num, Integer min, String errMessage) {
      min((Integer)num, (Integer)min, (String)null, errMessage);
   }

   public static void min(Integer num, Integer min) {
      min(num, min, "[Assertion failed] Must not less than min");
   }

   public static void max(Integer num, Integer max, String errCode, String errMessage) {
      if (null != num && num > max) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void max(Integer num, Integer max, String errMessage) {
      max((Integer)num, (Integer)max, (String)null, errMessage);
   }

   public static void max(Integer num, Integer max) {
      max(num, max, "[Assertion failed] Must not greater than max");
   }

   public static void range(Long num, Long min, Long max, String errCode, String errMessage) {
      Boolean notInRange = null != num && (num < min || num > max);
      if (notInRange) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void range(Long num, Long min, Long max, String errMessage) {
      range((Long)num, (Long)min, (Long)max, (String)null, errMessage);
   }

   public static void range(Long num, Long min, Long max) {
      range(num, min, max, "[Assertion failed] Must not exceed range");
   }

   public static void min(Long num, Long min, String errCode, String errMessage) {
      if (null != num && num < min) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void min(Long num, Long min, String errMessage) {
      min((Long)num, (Long)min, (String)null, errMessage);
   }

   public static void min(Long num, Long min) {
      min(num, min, "[Assertion failed] Must not less than min");
   }

   public static void max(Long num, Long max, String errCode, String errMessage) {
      if (null != num && num > max) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void max(Long num, Long max, String errMessage) {
      max((Long)num, (Long)max, (String)null, errMessage);
   }

   public static void max(Long num, Long max) {
      max(num, max, "[Assertion failed] Must not greater than max");
   }

   public static void range(Double num, Double min, Double max, String errCode, String errMessage) {
      Boolean notInRange = null != num && (num < min || num > max);
      if (notInRange) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void range(Double num, Double min, Double max, String errMessage) {
      range((Double)num, (Double)min, (Double)max, (String)null, errMessage);
   }

   public static void range(Double num, Double min, Double max) {
      range(num, min, max, "[Assertion failed] Must not exceed range");
   }

   public static void min(Double num, Double min, String errCode, String errMessage) {
      if (null != num && num < min) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void min(Double num, Double min, String errMessage) {
      min((Double)num, (Double)min, (String)null, errMessage);
   }

   public static void min(Double num, Double min) {
      min(num, min, "[Assertion failed] Must not less than min");
   }

   public static void max(Double num, Double max, String errCode, String errMessage) {
      if (null != num && num > max) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void max(Double num, Double max, String errMessage) {
      max((Double)num, (Double)max, (String)null, errMessage);
   }

   public static void max(Double num, Double max) {
      max(num, max, "[Assertion failed] Must not greater than max");
   }

   public static void range(Float num, Float min, Float max, String errCode, String errMessage) {
      Boolean notInRange = null != num && (num < min || num > max);
      if (notInRange) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void range(Float num, Float min, Float max, String errMessage) {
      range((Float)num, (Float)min, (Float)max, (String)null, errMessage);
   }

   public static void range(Float num, Float min, Float max) {
      range(num, min, max, "[Assertion failed] Must not exceed range");
   }

   public static void min(Float num, Float min, String errCode, String errMessage) {
      if (null != num && num < min) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void min(Float num, Float min, String errMessage) {
      min((Float)num, (Float)min, (String)null, errMessage);
   }

   public static void min(Float num, Float min) {
      min(num, min, "[Assertion failed] Must not less than min");
   }

   public static void max(Float num, Float max, String errCode, String errMessage) {
      if (null != num && num > max) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void max(Float num, Float max, String errMessage) {
      max((Float)num, (Float)max, (String)null, errMessage);
   }

   public static void max(Float num, Float max) {
      max(num, max, "[Assertion failed] Must not greater than max");
   }

   public static void range(BigDecimal num, BigDecimal min, BigDecimal max, String errCode, String errMessage) {
      Boolean notInRange = null != num && (num.compareTo(min) < 0 || num.compareTo(max) > 0);
      if (notInRange) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void range(BigDecimal num, BigDecimal min, BigDecimal max, String errMessage) {
      range((BigDecimal)num, (BigDecimal)min, (BigDecimal)max, (String)null, errMessage);
   }

   public static void range(BigDecimal num, BigDecimal min, BigDecimal max) {
      range(num, min, max, "[Assertion failed] Must not exceed range");
   }

   public static void min(BigDecimal num, BigDecimal min, String errCode, String errMessage) {
      if (null != num && num.compareTo(min) < 0) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void min(BigDecimal num, BigDecimal min, String errMessage) {
      min((BigDecimal)num, (BigDecimal)min, (String)null, errMessage);
   }

   public static void min(BigDecimal num, BigDecimal min) {
      min(num, min, "[Assertion failed] Must not less than min");
   }

   public static void max(BigDecimal num, BigDecimal max, String errCode, String errMessage) {
      if (null != num && num.compareTo(max) > 0) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void max(BigDecimal num, BigDecimal max, String errMessage) {
      max((BigDecimal)num, (BigDecimal)max, (String)null, errMessage);
   }

   public static void max(BigDecimal num, BigDecimal max) {
      max(num, max, "[Assertion failed] Must not greater than max");
   }

   public static void length(String text, Integer min, Integer max, String errCode, String errMessage) {
      Boolean notInLength = null != text && (text.length() < min || text.length() > max);
      if (notInLength) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void length(String text, Integer min, Integer max, String errMessage) {
      length(text, min, max, (String)null, errMessage);
   }

   public static void length(String text, Integer min, Integer max) {
      length(text, min, max, (String)null, "[Assertion failed] Must not exceed length");
   }

   public static void regex(String text, String regex, String errCode, String errMessage) {
      if (null != text && !Pattern.compile(regex).matcher(text).matches()) {
         throw (RuntimeException)errorToExceptionFunc.apply(errCode, errMessage);
      }
   }

   public static void regex(String text, String regex, String errMessage) {
      regex(text, regex, (String)null, errMessage);
   }

   public static void regex(String text, String regex) {
      regex(text, "[Assertion failed] Must match regex");
   }
}
