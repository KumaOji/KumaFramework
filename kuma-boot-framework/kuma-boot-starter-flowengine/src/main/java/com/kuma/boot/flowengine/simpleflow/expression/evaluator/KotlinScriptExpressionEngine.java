package com.kuma.boot.flowengine.simpleflow.expression.evaluator;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class KotlinScriptExpressionEngine implements ExpressionEngine {
   private final ScriptEngine kotlinEngine;
   private final Map<String, Object> compiledScriptCache;
   private final boolean cacheEnabled;

   public KotlinScriptExpressionEngine() {
      this(true);
   }

   public KotlinScriptExpressionEngine(boolean cacheEnabled) {
      this.compiledScriptCache = new ConcurrentHashMap();
      this.cacheEnabled = cacheEnabled;
      ScriptEngineManager manager = new ScriptEngineManager();
      this.kotlinEngine = manager.getEngineByExtension("kts");
      if (this.kotlinEngine == null) {
         throw new RuntimeException("Kotlin script engine not found. Please ensure kotlin-scripting-jsr223 is in the classpath.");
      }
   }

   public KotlinScriptExpressionEngine(Map<String, Object> configuration) {
      this.compiledScriptCache = new ConcurrentHashMap();
      Boolean cacheEnabledConfig = (Boolean)configuration.get("cacheEnabled");
      this.cacheEnabled = cacheEnabledConfig != null ? cacheEnabledConfig : true;
      ScriptEngineManager manager = new ScriptEngineManager();
      this.kotlinEngine = manager.getEngineByExtension("kts");
      if (this.kotlinEngine == null) {
         throw new RuntimeException("Kotlin script engine not found. Please ensure kotlin-scripting-jsr223 is in the classpath.");
      }
   }

   public Object evaluate(String expression, Map<String, Object> context) throws ExpressionException {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            Bindings bindings = this.kotlinEngine.createBindings();
            if (context != null) {
               bindings.putAll(context);
            }

            Object result;
            if (this.cacheEnabled) {
               String cacheKey = expression + "_" + (context != null ? context.hashCode() : 0);
               result = this.compiledScriptCache.computeIfAbsent(cacheKey, (key) -> {
                  try {
                     return this.kotlinEngine.eval(expression, bindings);
                  } catch (ScriptException e) {
                     throw new RuntimeException("Failed to evaluate Kotlin script: " + expression, e);
                  }
               });
            } else {
               result = this.kotlinEngine.eval(expression, bindings);
            }

            LogUtils.debug("Kotlin script '{}' evaluated to: {}", new Object[]{expression, result});
            return result;
         } catch (ScriptException e) {
            LogUtils.error("Failed to evaluate Kotlin script: {}", new Object[]{expression, e});
            throw new ExpressionException("Kotlin script evaluation failed: " + e.getMessage(), e);
         } catch (Exception e) {
            LogUtils.error("Unexpected error evaluating Kotlin script: {}", new Object[]{expression, e});
            throw new ExpressionException("Kotlin script evaluation failed: " + e.getMessage(), e);
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
            this.kotlinEngine.eval(expression, this.kotlinEngine.createBindings());
            return true;
         } catch (Exception e) {
            LogUtils.debug("Invalid Kotlin script: {}", new Object[]{expression, e});
            return false;
         }
      } else {
         return false;
      }
   }

   public Expression parseExpression(String expression) throws ExpressionException {
      try {
         if (!this.isValidExpression(expression)) {
            throw new ExpressionException("Invalid Kotlin script syntax: " + expression);
         } else {
            return new KotlinScriptExpression(expression, this.kotlinEngine);
         }
      } catch (Exception e) {
         throw new ExpressionException("Failed to parse Kotlin script: " + expression, e);
      }
   }

   public String getEngineName() {
      return "Kotlin Script";
   }

   public String getEngineVersion() {
      return this.kotlinEngine != null ? this.kotlinEngine.getFactory().getEngineVersion() : "Unknown";
   }

   public String getSupportedSyntax() {
      return "Kotlin Script Syntax - Full Kotlin language support for condition evaluation";
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
         throw new IllegalArgumentException("Cannot convert " + var10002 + " + to " + String.valueOf(targetType));
      } else if (value instanceof Boolean) {
         return (T)value;
      } else {
         return (T)(value instanceof Number ? ((Number)value).doubleValue() != (double)0.0F : Boolean.valueOf(value.toString()));
      }
   }

   public void clearCache() {
      this.compiledScriptCache.clear();
      LogUtils.debug("Kotlin script cache cleared", new Object[0]);
   }

   public int getCacheSize() {
      return this.compiledScriptCache.size();
   }

   private static class KotlinScriptExpression implements Expression {
      private final String expressionString;
      private final ScriptEngine engine;

      public KotlinScriptExpression(String expressionString, ScriptEngine engine) {
         this.expressionString = expressionString;
         this.engine = engine;
      }

      public Object execute(Map<String, Object> context) throws ExpressionException {
         try {
            Bindings bindings = this.engine.createBindings();
            if (context != null) {
               bindings.putAll(context);
            }

            return this.engine.eval(this.expressionString, bindings);
         } catch (ScriptException e) {
            throw new ExpressionException("Failed to execute Kotlin script: " + this.expressionString, e);
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
