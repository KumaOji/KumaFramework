package com.kuma.boot.flowengine.simpleflow.expression.api;

import java.util.Map;
import java.util.Set;

public interface Expression {
   Object execute(Map<String, Object> context) throws ExpressionException;

   <T> T execute(Map<String, Object> context, Class<T> expectedType) throws ExpressionException;

   String getExpressionString();

   Set<String> getVariableNames();

   boolean isConstant();

   ExpressionType getType();

   Class<?> getReturnType();

   ValidationResult validate(Map<String, Object> context);

   public static enum ExpressionType {
      BOOLEAN,
      NUMERIC,
      STRING,
      OBJECT,
      UNKNOWN;

      private ExpressionType() {
      }

      // $FF: synthetic method
      private static ExpressionType[] $values() {
         return new ExpressionType[]{BOOLEAN, NUMERIC, STRING, OBJECT, UNKNOWN};
      }
   }

   public static class ValidationResult {
      private final boolean valid;
      private final String errorMessage;
      private final Set<String> missingVariables;

      public ValidationResult(boolean valid, String errorMessage, Set<String> missingVariables) {
         this.valid = valid;
         this.errorMessage = errorMessage;
         this.missingVariables = missingVariables;
      }

      public static ValidationResult success() {
         return new ValidationResult(true, (String)null, (Set)null);
      }

      public static ValidationResult failure(String errorMessage) {
         return new ValidationResult(false, errorMessage, (Set)null);
      }

      public static ValidationResult missingVariables(Set<String> missingVariables) {
         return new ValidationResult(false, "Missing variables: " + String.valueOf(missingVariables), missingVariables);
      }

      public boolean isValid() {
         return this.valid;
      }

      public String getErrorMessage() {
         return this.errorMessage;
      }

      public Set<String> getMissingVariables() {
         return this.missingVariables;
      }

      public String toString() {
         boolean var10000 = this.valid;
         return "ValidationResult{valid=" + var10000 + ", errorMessage='" + this.errorMessage + "', missingVariables=" + String.valueOf(this.missingVariables) + "}";
      }
   }
}
