package com.kuma.boot.flowengine.simpleflow.expression.evaluator;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

public class JexlExpressionEngine implements ExpressionEngine {
   private final JexlEngine jexlEngine;
   private final Map<String, JexlExpression> expressionCache;
   private final boolean cacheEnabled;

   public JexlExpressionEngine() {
      this(true);
   }

   public JexlExpressionEngine(boolean cacheEnabled) {
      this.expressionCache = new ConcurrentHashMap();
      this.cacheEnabled = cacheEnabled;
      this.jexlEngine = (new JexlBuilder()).cache(512).strict(false).silent(false).create();
   }

   public JexlExpressionEngine(JexlEngine jexlEngine, boolean cacheEnabled) {
      this.expressionCache = new ConcurrentHashMap();
      this.jexlEngine = jexlEngine;
      this.cacheEnabled = cacheEnabled;
   }

   public JexlExpressionEngine(Map<String, Object> configuration) {
      this.expressionCache = new ConcurrentHashMap();
      Boolean cacheEnabledConfig = (Boolean)configuration.get("cacheEnabled");
      this.cacheEnabled = cacheEnabledConfig != null ? cacheEnabledConfig : true;
      JexlBuilder builder = (new JexlBuilder()).cache(512).strict(false).silent(false);
      Integer cacheSize = (Integer)configuration.get("cacheSize");
      if (cacheSize != null) {
         builder.cache(cacheSize);
      }

      Boolean strict = (Boolean)configuration.get("strict");
      if (strict != null) {
         builder.strict(strict);
      }

      Boolean silent = (Boolean)configuration.get("silent");
      if (silent != null) {
         builder.silent(silent);
      }

      this.jexlEngine = builder.create();
   }

   public Object evaluate(String expression, Map<String, Object> context) throws ExpressionException {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            JexlExpression jexlExpression = this.getJexlExpression(expression);
            JexlContext jexlContext = this.createJexlContext(context);
            Object result = jexlExpression.evaluate(jexlContext);
            LogUtils.debug("Evaluated expression '{}' with result: {}", new Object[]{expression, result});
            return result;
         } catch (JexlException e) {
            LogUtils.error("Failed to evaluate expression: {}", new Object[]{expression, e});
            throw new ExpressionException("Failed to evaluate expression: " + e.getMessage(), expression, e);
         } catch (Exception e) {
            LogUtils.error("Unexpected error evaluating expression: {}", new Object[]{expression, e});
            throw new ExpressionException("Unexpected error: " + e.getMessage(), expression, e);
         }
      } else {
         throw new ExpressionException("Expression cannot be null or empty");
      }
   }

   public <T> T evaluate(String expression, Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      if (result == null) {
         return null;
      } else if (expectedType.isInstance(result)) {
         return (T)expectedType.cast(result);
      } else {
         try {
            return (T)this.convertType(result, expectedType);
         } catch (Exception e) {
            throw new ExpressionException("Cannot convert result of type " + result.getClass().getSimpleName() + " to expected type " + expectedType.getSimpleName(), expression, e);
         }
      }
   }

   public boolean evaluateBoolean(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      if (result instanceof Boolean) {
         return (Boolean)result;
      } else if (result == null) {
         return false;
      } else if (result instanceof Number) {
         return ((Number)result).doubleValue() != (double)0.0F;
      } else if (!(result instanceof String)) {
         return true;
      } else {
         String str = (String)result;
         return !str.isEmpty() && !"false".equalsIgnoreCase(str) && !"0".equals(str);
      }
   }

   public String evaluateString(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      return result == null ? null : result.toString();
   }

   public Number evaluateNumber(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      if (result instanceof Number) {
         return (Number)result;
      } else if (result instanceof String) {
         try {
            return Double.parseDouble((String)result);
         } catch (NumberFormatException e) {
            throw new ExpressionException("Cannot convert string '" + String.valueOf(result) + "' to number", expression, e);
         }
      } else {
         throw new ExpressionException("Result is not a number: " + String.valueOf(result), expression);
      }
   }

   public boolean isValidExpression(String expression) {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            this.jexlEngine.createExpression(expression);
            return true;
         } catch (JexlException e) {
            LogUtils.debug("Invalid expression: {}", new Object[]{expression, e});
            return false;
         }
      } else {
         return false;
      }
   }

   public Expression parseExpression(String expression) throws ExpressionException {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            JexlExpression jexlExpression = this.getJexlExpression(expression);
            return new JexlExpressionWrapper(expression, jexlExpression);
         } catch (JexlException e) {
            throw new ExpressionException("Failed to parse expression: " + e.getMessage(), expression, e);
         }
      } else {
         throw new ExpressionException("Expression cannot be null or empty");
      }
   }

   public String getEngineName() {
      return "JEXL";
   }

   public String getEngineVersion() {
      return "3.x";
   }

   public String getSupportedSyntax() {
      return "Apache Commons JEXL Expression Language";
   }

   private JexlExpression getJexlExpression(String expression) {
      if (this.cacheEnabled) {
         Map var10000 = this.expressionCache;
         JexlEngine var10002 = this.jexlEngine;
         Objects.requireNonNull(var10002);
         return (JexlExpression)var10000.computeIfAbsent(expression, var10002::createExpression);
      } else {
         return this.jexlEngine.createExpression(expression);
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

   public void clearCache() {
      this.expressionCache.clear();
      LogUtils.info("Expression cache cleared", new Object[0]);
   }

   public int getCacheSize() {
      return this.expressionCache.size();
   }
}
