package com.kuma.boot.xss.xssvalidation;

import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XssValidator implements ConstraintValidator<Xss, String> {
   private static final String HTML_PATTERN = "<(\\S*?)[^>]*>.*?|<.*? />";

   public XssValidator() {
   }

   public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
      if (StringUtils.isBlank(value)) {
         return true;
      } else {
         return !containsHtml(value);
      }
   }

   public static boolean containsHtml(String value) {
      Pattern pattern = Pattern.compile("<(\\S*?)[^>]*>.*?|<.*? />");
      Matcher matcher = pattern.matcher(value);
      return matcher.matches();
   }
}
