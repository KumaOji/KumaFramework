package com.kuma.boot.flowengine.simpleflow.expression.evaluator;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

public class JexlExpressionWrapper implements Expression {
   private final String expressionString;
   private final JexlExpression jexlExpression;
   private final Set<String> variableNames;
   private final Expression.ExpressionType type;
   private final Class<?> returnType;

   public JexlExpressionWrapper(String expressionString, JexlExpression jexlExpression) {
      this.expressionString = expressionString;
      this.jexlExpression = jexlExpression;
      this.variableNames = this.extractVariableNames();
      this.type = this.determineExpressionType();
      this.returnType = this.determineReturnType();
   }

   public Object execute(Map<String, Object> context) throws ExpressionException {
      try {
         JexlContext jexlContext = this.createJexlContext(context);
         Object result = this.jexlExpression.evaluate(jexlContext);
         LogUtils.debug("Executed expression '{}' with result: {}", new Object[]{this.expressionString, result});
         return result;
      } catch (JexlException e) {
         LogUtils.error("Failed to execute expression: {}", new Object[]{this.expressionString, e});
         throw new ExpressionException("Failed to execute expression: " + e.getMessage(), this.expressionString, e);
      } catch (Exception e) {
         LogUtils.error("Unexpected error executing expression: {}", new Object[]{this.expressionString, e});
         throw new ExpressionException("Unexpected error: " + e.getMessage(), this.expressionString, e);
      }
   }

   public <T> T execute(Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
      Object result = this.execute(context);
      if (result == null) {
         return null;
      } else if (expectedType.isInstance(result)) {
         return (T)expectedType.cast(result);
      } else {
         try {
            return (T)this.convertType(result, expectedType);
         } catch (Exception e) {
            throw new ExpressionException("Cannot convert result of type " + result.getClass().getSimpleName() + " to expected type " + expectedType.getSimpleName(), this.expressionString, e);
         }
      }
   }

   public String getExpressionString() {
      return this.expressionString;
   }

   public Set<String> getVariableNames() {
      return Collections.unmodifiableSet(this.variableNames);
   }

   public boolean isConstant() {
      return this.variableNames.isEmpty();
   }

   public Expression.ExpressionType getType() {
      return this.type;
   }

   public Class<?> getReturnType() {
      return this.returnType;
   }

   public Expression.ValidationResult validate(Map<String, Object> context) {
      Set<String> missingVariables = new HashSet();

      for(String variableName : this.variableNames) {
         if (context == null || !context.containsKey(variableName)) {
            missingVariables.add(variableName);
         }
      }

      if (!missingVariables.isEmpty()) {
         return Expression.ValidationResult.missingVariables(missingVariables);
      } else {
         try {
            this.execute(context);
            return Expression.ValidationResult.success();
         } catch (ExpressionException e) {
            return Expression.ValidationResult.failure(e.getMessage());
         }
      }
   }

   private Set<String> extractVariableNames() {
      Set<String> variables = new HashSet();

      try {
         String expr = this.expressionString;
         String cleanExpr = expr.replaceAll("'[^']*'", " ").replaceAll("\"[^\"]*\"", " ");
         cleanExpr = cleanExpr.replaceAll("[>=<!?:+\\-*/()\\[\\]{}.,;]", " ");
         Pattern pattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b");
         Matcher matcher = pattern.matcher(cleanExpr);

         while(matcher.find()) {
            String token = matcher.group().trim();
            if (!token.isEmpty() && !this.isKeywordOrConstant(token) && !token.matches("\\d+")) {
               variables.add(token);
            }
         }
      } catch (Exception e) {
         LogUtils.warn("Failed to extract variables from expression: {}", new Object[]{this.expressionString, e});
      }

      return variables;
   }

   private boolean isKeywordOrConstant(String token) {
      return "true".equals(token) || "false".equals(token) || "null".equals(token) || "if".equals(token) || "else".equals(token) || "for".equals(token) || "while".equals(token) || "return".equals(token) || "new".equals(token) || "var".equals(token) || "function".equals(token) || "size".equals(token) || "length".equals(token) || "empty".equals(token);
   }

   private Expression.ExpressionType determineExpressionType() {
      String expr = this.expressionString.toLowerCase().trim();
      if (!expr.contains("==") && !expr.contains("!=") && !expr.contains(">=") && !expr.contains("<=") && !expr.contains(">") && !expr.contains("<") && !expr.contains("&&") && !expr.contains("||") && !expr.contains("!") && !expr.equals("true") && !expr.equals("false")) {
         if (!expr.matches(".*[+\\-*/].*") && !expr.matches("\\d+(\\.\\d+)?")) {
            return (!expr.startsWith("'") || !expr.endsWith("'")) && (!expr.startsWith("\"") || !expr.endsWith("\"")) ? Expression.ExpressionType.UNKNOWN : Expression.ExpressionType.STRING;
         } else {
            return Expression.ExpressionType.NUMERIC;
         }
      } else {
         return Expression.ExpressionType.BOOLEAN;
      }
   }

   private Class<?> determineReturnType() {
      switch (this.type) {
         case BOOLEAN -> {
            return Boolean.class;
         }
         case NUMERIC -> {
            return Number.class;
         }
         case STRING -> {
            return String.class;
         }
         default -> {
            return Object.class;
         }
      }
   }

   private JexlContext createJexlContext(Map<String, Object> context) {
      JexlContext jexlContext = new MapContext();
      if (context != null) {
         Objects.requireNonNull(jexlContext);
         context.forEach(jexlContext::set);
      }

      return jexlContext;
   }

   private <T> T convertType(Object value, Class<T> targetType) {
      if (targetType == String.class) {
         return (T)value.toString();
      } else {
         if (targetType == Boolean.class || targetType == Boolean.TYPE) {
            if (value instanceof Boolean) {
               return (T)value;
            }

            if (value instanceof Number) {
               return (T)((Number)value).doubleValue() != (double)0.0F;
            }

            if (value instanceof String) {
               return (T)Boolean.parseBoolean((String)value);
            }
         }

         if (targetType == Integer.class || targetType == Integer.TYPE) {
            if (value instanceof Number) {
               return (T)((Number)value).intValue();
            }

            if (value instanceof String) {
               return (T)Integer.parseInt((String)value);
            }
         }

         if (targetType == Long.class || targetType == Long.TYPE) {
            if (value instanceof Number) {
               return (T)((Number)value).longValue();
            }

            if (value instanceof String) {
               return (T)Long.parseLong((String)value);
            }
         }

         if (targetType == Double.class || targetType == Double.TYPE) {
            if (value instanceof Number) {
               return (T)((Number)value).doubleValue();
            }

            if (value instanceof String) {
               return (T)Double.parseDouble((String)value);
            }
         }

         String var10002 = String.valueOf(value.getClass());
         throw new ClassCastException("Cannot convert " + var10002 + " to " + String.valueOf(targetType));
      }
   }

   public String toString() {
      String var10000 = this.expressionString;
      return "JexlExpressionWrapper{expression='" + var10000 + "', type=" + String.valueOf(this.type) + ", variables=" + String.valueOf(this.variableNames) + "}";
   }
}
