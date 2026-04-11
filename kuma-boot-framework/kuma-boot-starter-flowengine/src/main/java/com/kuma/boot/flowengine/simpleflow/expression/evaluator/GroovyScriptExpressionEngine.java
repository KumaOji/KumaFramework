package com.kuma.boot.flowengine.simpleflow.expression.evaluator;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import groovy.lang.Script;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyScriptExpressionEngine implements ExpressionEngine {
   private final GroovyShell groovyShell;
   private final Map<String, Script> scriptCache;
   private final boolean cacheEnabled;

   public GroovyScriptExpressionEngine() {
      this(true);
   }

   public GroovyScriptExpressionEngine(boolean cacheEnabled) {
      this.scriptCache = new ConcurrentHashMap();
      this.cacheEnabled = cacheEnabled;
      this.groovyShell = new GroovyShell();
   }

   public GroovyScriptExpressionEngine(Map<String, Object> configuration) {
      this.scriptCache = new ConcurrentHashMap();
      Boolean cacheEnabledConfig = (Boolean)configuration.get("cacheEnabled");
      this.cacheEnabled = cacheEnabledConfig != null ? cacheEnabledConfig : true;
      this.groovyShell = new GroovyShell();
   }

   public Object evaluate(String expression, Map<String, Object> context) throws ExpressionException {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            Script script = this.getScript(expression);
            Binding binding = new Binding();
            if (context != null) {
               for(Map.Entry<String, Object> entry : context.entrySet()) {
                  binding.setVariable((String)entry.getKey(), entry.getValue());
               }
            }

            script.setBinding(binding);
            Object result = script.run();
            LogUtils.debug("Groovy script '{}' evaluated to: {}", new Object[]{expression, result});
            return result;
         } catch (Exception e) {
            LogUtils.error("Failed to evaluate Groovy script: {}", new Object[]{expression, e});
            throw new ExpressionException("Groovy script evaluation failed: " + e.getMessage(), e);
         }
      } else {
         throw new ExpressionException("Expression cannot be null or empty");
      }
   }

   public <T> T evaluate(String expression, Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
      Object result = this.evaluate(expression, context);

      try {
         return (T)this.convertType(result, expectedType);
      } catch (Exception e) {
         throw new ExpressionException("Failed to convert result to expected type: " + expectedType.getName(), e);
      }
   }

   public boolean evaluateBoolean(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      if (result instanceof Boolean) {
         return (Boolean)result;
      } else if (result instanceof Number) {
         return ((Number)result).doubleValue() != (double)0.0F;
      } else if (result instanceof String) {
         return Boolean.parseBoolean((String)result);
      } else {
         return result != null;
      }
   }

   public String evaluateString(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      return result != null ? result.toString() : null;
   }

   public Number evaluateNumber(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      if (result instanceof Number) {
         return (Number)result;
      } else if (result instanceof String) {
         try {
            return Double.parseDouble((String)result);
         } catch (NumberFormatException e) {
            throw new ExpressionException("Cannot convert string to number: " + String.valueOf(result), e);
         }
      } else {
         throw new ExpressionException("Result is not a number: " + String.valueOf(result));
      }
   }

   public boolean isValidExpression(String expression) {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            this.groovyShell.parse(expression);
            return true;
         } catch (Exception e) {
            LogUtils.debug("Invalid Groovy script: {}", new Object[]{expression, e});
            return false;
         }
      } else {
         return false;
      }
   }

   public Expression parseExpression(String expression) throws ExpressionException {
      try {
         Script script = this.getScript(expression);
         return new GroovyScriptExpression(script, expression);
      } catch (Exception e) {
         throw new ExpressionException("Failed to parse Groovy script: " + expression, e);
      }
   }

   public String getEngineName() {
      return "Groovy Script";
   }

   public String getEngineVersion() {
      return GroovySystem.getVersion();
   }

   public String getSupportedSyntax() {
      return "Groovy Script Syntax - Full Groovy language support for condition evaluation";
   }

   private Script getScript(String expression) {
      return this.cacheEnabled ? (Script)this.scriptCache.computeIfAbsent(expression, (expr) -> {
         try {
            return this.groovyShell.parse(expr);
         } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groovy script: " + expr, e);
         }
      }) : this.groovyShell.parse(expression);
   }

   private <T> T convertType(Object value, Class<T> targetType) {
      if (value == null) {
         return null;
      } else if (targetType.isInstance(value)) {
         return (T)value;
      } else if (targetType == String.class) {
         return (T)value.toString();
      } else if (targetType != Boolean.class && targetType != Boolean.TYPE) {
         if (Number.class.isAssignableFrom(targetType)) {
            if (value instanceof Number) {
               Number num = (Number)value;
               if (targetType == Integer.class || targetType == Integer.TYPE) {
                  return (T)num.intValue();
               }

               if (targetType == Long.class || targetType == Long.TYPE) {
                  return (T)num.longValue();
               }

               if (targetType == Double.class || targetType == Double.TYPE) {
                  return (T)num.doubleValue();
               }

               if (targetType == Float.class || targetType == Float.TYPE) {
                  return (T)num.floatValue();
               }
            } else if (value instanceof String) {
               String str = (String)value;
               if (targetType == Integer.class || targetType == Integer.TYPE) {
                  return (T)Integer.valueOf(str);
               }

               if (targetType == Long.class || targetType == Long.TYPE) {
                  return (T)Long.valueOf(str);
               }

               if (targetType == Double.class || targetType == Double.TYPE) {
                  return (T)Double.valueOf(str);
               }

               if (targetType == Float.class || targetType == Float.TYPE) {
                  return (T)Float.valueOf(str);
               }
            }
         }

         String var10002 = String.valueOf(value.getClass());
         throw new IllegalArgumentException("Cannot convert " + var10002 + " to " + String.valueOf(targetType));
      } else if (value instanceof Boolean) {
         return (T)value;
      } else {
         return (T)(value instanceof Number ? ((Number)value).doubleValue() != (double)0.0F : Boolean.valueOf(value.toString()));
      }
   }

   public void clearCache() {
      this.scriptCache.clear();
      LogUtils.debug("Groovy script cache cleared", new Object[0]);
   }

   public int getCacheSize() {
      return this.scriptCache.size();
   }

   private static class GroovyScriptExpression implements Expression {
      private final Script script;
      private final String expressionString;

      public GroovyScriptExpression(Script script, String expressionString) {
         this.script = script;
         this.expressionString = expressionString;
      }

      public Object execute(Map<String, Object> context) throws ExpressionException {
         try {
            Binding binding = new Binding();
            if (context != null) {
               for(Map.Entry<String, Object> entry : context.entrySet()) {
                  binding.setVariable((String)entry.getKey(), entry.getValue());
               }
            }

            this.script.setBinding(binding);
            return this.script.run();
         } catch (Exception e) {
            throw new ExpressionException("Failed to execute Groovy script: " + this.expressionString, e);
         }
      }

      public <T> T execute(Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
         Object result = this.execute(context);
         if (result == null) {
            return null;
         } else if (expectedType.isInstance(result)) {
            return (T)expectedType.cast(result);
         } else {
            throw new ExpressionException("Cannot convert result to expected type: " + expectedType.getName());
         }
      }

      public String getExpressionString() {
         return this.expressionString;
      }

      public Set<String> getVariableNames() {
         return new HashSet();
      }

      public boolean isConstant() {
         return this.getVariableNames().isEmpty();
      }

      public Expression.ExpressionType getType() {
         return Expression.ExpressionType.OBJECT;
      }

      public Class<?> getReturnType() {
         return Object.class;
      }

      public Expression.ValidationResult validate(Map<String, Object> context) {
         try {
            this.execute(context);
            return Expression.ValidationResult.success();
         } catch (ExpressionException e) {
            return Expression.ValidationResult.failure(e.getMessage());
         }
      }
   }
}
