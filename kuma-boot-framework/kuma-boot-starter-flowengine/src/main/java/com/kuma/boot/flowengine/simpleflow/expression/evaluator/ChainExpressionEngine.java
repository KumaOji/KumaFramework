package com.kuma.boot.flowengine.simpleflow.expression.evaluator;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChainExpressionEngine implements ExpressionEngine {
   private static final String ENGINE_NAME = "Chain";
   private static final String ENGINE_VERSION = "1.0.0";
   private static final String SUPPORTED_SYNTAX = "THEN(component1, component2, ...), IF(condition, trueComponent, falseComponent), SWITCH(condition, case1:component1, case2:component2, default:defaultComponent)";
   private static final Pattern THEN_PATTERN = Pattern.compile("THEN\\s*\\(\\s*([^)]+)\\s*\\)");
   private static final Pattern IF_PATTERN = Pattern.compile("IF\\s*\\(\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^)]+)\\s*\\)");
   private static final Pattern SWITCH_PATTERN = Pattern.compile("SWITCH\\s*\\(\\s*([^,]+)\\s*,\\s*(.+)\\s*\\)");
   private static final Pattern CASE_PATTERN = Pattern.compile("([^:]+)\\s*:\\s*([^,]+)");

   public ChainExpressionEngine() {
   }

   public Object evaluate(String expression, Map<String, Object> context) throws ExpressionException {
      if (expression != null && !expression.trim().isEmpty()) {
         String trimmedExpression = expression.trim();

         try {
            if (trimmedExpression.startsWith("THEN")) {
               return this.evaluateThenExpression(trimmedExpression, context);
            } else if (trimmedExpression.startsWith("IF")) {
               return this.evaluateIfExpression(trimmedExpression, context);
            } else {
               return trimmedExpression.startsWith("SWITCH") ? this.evaluateSwitchExpression(trimmedExpression, context) : this.evaluateComponent(trimmedExpression, context);
            }
         } catch (Exception e) {
            LogUtils.error("Failed to evaluate chain expression: {}", new Object[]{expression, e});
            throw new ExpressionException("Failed to evaluate expression: " + expression, e);
         }
      } else {
         throw new ExpressionException("Expression cannot be null or empty");
      }
   }

   private Object evaluateThenExpression(String expression, Map<String, Object> context) throws ExpressionException {
      Matcher matcher = THEN_PATTERN.matcher(expression);
      if (!matcher.matches()) {
         throw new ExpressionException("Invalid THEN syntax: " + expression);
      } else {
         String componentsStr = matcher.group(1);
         String[] components = this.parseComponents(componentsStr);
         Object result = null;

         for(String component : components) {
            result = this.evaluateComponent(component.trim(), context);
            if (result != null) {
               context.put("previousResult", result);
            }
         }

         return result;
      }
   }

   private Object evaluateIfExpression(String expression, Map<String, Object> context) throws ExpressionException {
      Matcher matcher = IF_PATTERN.matcher(expression);
      if (!matcher.matches()) {
         throw new ExpressionException("Invalid IF syntax: " + expression);
      } else {
         String condition = matcher.group(1).trim();
         String trueComponent = matcher.group(2).trim();
         String falseComponent = matcher.group(3).trim();
         boolean conditionResult = this.evaluateCondition(condition, context);
         return conditionResult ? this.evaluateComponent(trueComponent, context) : this.evaluateComponent(falseComponent, context);
      }
   }

   private Object evaluateSwitchExpression(String expression, Map<String, Object> context) throws ExpressionException {
      Matcher matcher = SWITCH_PATTERN.matcher(expression);
      if (!matcher.matches()) {
         throw new ExpressionException("Invalid SWITCH syntax: " + expression);
      } else {
         String condition = matcher.group(1).trim();
         String casesStr = matcher.group(2).trim();
         Object conditionValue = this.evaluateSimpleExpression(condition, context);
         String[] cases = casesStr.split(",");
         String defaultComponent = null;

         for(String caseStr : cases) {
            caseStr = caseStr.trim();
            if (caseStr.startsWith("default:")) {
               defaultComponent = caseStr.substring(8).trim();
            } else {
               Matcher caseMatcher = CASE_PATTERN.matcher(caseStr);
               if (caseMatcher.matches()) {
                  String caseValue = caseMatcher.group(1).trim();
                  String component = caseMatcher.group(2).trim();
                  if (Objects.equals(String.valueOf(conditionValue), caseValue) || Objects.equals(conditionValue, this.parseValue(caseValue))) {
                     return this.evaluateComponent(component, context);
                  }
               }
            }
         }

         if (defaultComponent != null) {
            return this.evaluateComponent(defaultComponent, context);
         } else {
            return null;
         }
      }
   }

   private Object evaluateComponent(String component, Map<String, Object> context) throws ExpressionException {
      if (!component.contains("THEN(") && !component.contains("IF(") && !component.contains("SWITCH(")) {
         return component.contains(".") ? this.evaluateBeanMethod(component, context) : this.evaluateSimpleComponent(component, context);
      } else {
         return this.evaluate(component, context);
      }
   }

   private Object evaluateBeanMethod(String component, Map<String, Object> context) throws ExpressionException {
      String[] parts = component.split("\\.", 2);
      if (parts.length != 2) {
         throw new ExpressionException("Invalid bean method syntax: " + component);
      } else {
         String beanName = parts[0].trim();
         String methodName = parts[1].trim();
         Object bean = context.get(beanName);
         if (bean == null) {
            throw new ExpressionException("Bean not found: " + beanName);
         } else {
            try {
               return bean.getClass().getMethod(methodName).invoke(bean);
            } catch (Exception e) {
               throw new ExpressionException("Failed to invoke method: " + component, e);
            }
         }
      }
   }

   private Object evaluateSimpleComponent(String component, Map<String, Object> context) throws ExpressionException {
      Object componentObj = context.get(component);
      if (componentObj == null) {
         throw new ExpressionException("Component not found: " + component);
      } else if (componentObj instanceof Runnable) {
         ((Runnable)componentObj).run();
         return null;
      } else {
         return componentObj;
      }
   }

   private boolean evaluateCondition(String condition, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluateSimpleExpression(condition, context);
      return result instanceof Boolean ? (Boolean)result : Boolean.parseBoolean(String.valueOf(result));
   }

   private Object evaluateSimpleExpression(String expression, Map<String, Object> context) {
      return context.containsKey(expression) ? context.get(expression) : this.parseValue(expression);
   }

   private String[] parseComponents(String componentsStr) {
      List<String> components = new ArrayList();
      StringBuilder current = new StringBuilder();
      int parentheses = 0;

      for(char c : componentsStr.toCharArray()) {
         if (c == '(') {
            ++parentheses;
         } else if (c == ')') {
            --parentheses;
         } else if (c == ',' && parentheses == 0) {
            components.add(current.toString().trim());
            current = new StringBuilder();
            continue;
         }

         current.append(c);
      }

      if (current.length() > 0) {
         components.add(current.toString().trim());
      }

      return (String[])components.toArray(new String[0]);
   }

   private Object parseValue(String value) {
      if (value == null) {
         return null;
      } else {
         value = value.trim();
         if ("true".equalsIgnoreCase(value)) {
            return true;
         } else if ("false".equalsIgnoreCase(value)) {
            return false;
         } else {
            try {
               return value.contains(".") ? Double.parseDouble(value) : Long.parseLong(value);
            } catch (NumberFormatException var3) {
               return (!value.startsWith("\"") || !value.endsWith("\"")) && (!value.startsWith("'") || !value.endsWith("'")) ? value : value.substring(1, value.length() - 1);
            }
         }
      }
   }

   public <T> T evaluate(String expression, Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      if (result == null) {
         return null;
      } else if (expectedType.isInstance(result)) {
         return (T)expectedType.cast(result);
      } else if (expectedType == String.class) {
         return (T)expectedType.cast(String.valueOf(result));
      } else {
         throw new ExpressionException("Cannot convert result to expected type: " + expectedType.getName());
      }
   }

   public boolean evaluateBoolean(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      return result instanceof Boolean ? (Boolean)result : Boolean.parseBoolean(String.valueOf(result));
   }

   public String evaluateString(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      return result != null ? String.valueOf(result) : null;
   }

   public Number evaluateNumber(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      if (result instanceof Number) {
         return (Number)result;
      } else {
         try {
            String str = String.valueOf(result);
            return (Number)(str.contains(".") ? Double.parseDouble(str) : Long.parseLong(str));
         } catch (NumberFormatException e) {
            throw new ExpressionException("Cannot convert result to number: " + String.valueOf(result), e);
         }
      }
   }

   public boolean isValidExpression(String expression) {
      if (expression != null && !expression.trim().isEmpty()) {
         String trimmed = expression.trim();
         if (trimmed.startsWith("THEN")) {
            return THEN_PATTERN.matcher(trimmed).matches();
         } else if (trimmed.startsWith("IF")) {
            return IF_PATTERN.matcher(trimmed).matches();
         } else {
            return trimmed.startsWith("SWITCH") ? SWITCH_PATTERN.matcher(trimmed).matches() : trimmed.matches("[a-zA-Z_][a-zA-Z0-9_.]*");
         }
      } else {
         return false;
      }
   }

   public Expression parseExpression(String expression) throws ExpressionException {
      return new ChainExpression(expression, this);
   }

   public String getEngineName() {
      return "Chain";
   }

   public String getEngineVersion() {
      return "1.0.0";
   }

   public String getSupportedSyntax() {
      return "THEN(component1, component2, ...), IF(condition, trueComponent, falseComponent), SWITCH(condition, case1:component1, case2:component2, default:defaultComponent)";
   }

   private static class ChainExpression implements Expression {
      private final String expression;
      private final ChainExpressionEngine engine;

      public ChainExpression(String expression, ChainExpressionEngine engine) {
         this.expression = expression;
         this.engine = engine;
      }

      public Object execute(Map<String, Object> context) throws ExpressionException {
         return this.engine.evaluate(this.expression, context);
      }

      public <T> T execute(Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
         return (T)this.engine.evaluate(this.expression, context, expectedType);
      }

      public String getExpressionString() {
         return this.expression;
      }

      public Set<String> getVariableNames() {
         Set<String> variables = new HashSet();
         return variables;
      }

      public boolean isConstant() {
         return false;
      }

      public Expression.ExpressionType getType() {
         return Expression.ExpressionType.OBJECT;
      }

      public Class<?> getReturnType() {
         return Object.class;
      }

      public Expression.ValidationResult validate(Map<String, Object> context) {
         try {
            this.engine.isValidExpression(this.expression);
            return Expression.ValidationResult.success();
         } catch (Exception e) {
            return Expression.ValidationResult.failure("Invalid chain expression: " + e.getMessage());
         }
      }
   }
}
