package com.kuma.boot.flowengine.simpleflow.expression.evaluator;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import com.kuma.boot.flowengine.simpleflow.expression.wrapper.SpELExpressionWrapper;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELExpressionEngine implements ExpressionEngine {
   private static final String ENGINE_NAME = "SpEL";
   private static final String ENGINE_VERSION = "5.2.0";
   private static final String SUPPORTED_SYNTAX = "Spring Expression Language (SpEL)";
   private final ExpressionParser spelParser;
   private final Map<String, Expression> expressionCache;
   private final boolean cacheEnabled;

   public SpELExpressionEngine() {
      this(true);
   }

   public SpELExpressionEngine(boolean cacheEnabled) {
      this.expressionCache = new ConcurrentHashMap();
      this.spelParser = new SpelExpressionParser();
      this.cacheEnabled = cacheEnabled;
      LogUtils.debug("SpEL expression engine initialized with cache: {}", new Object[]{cacheEnabled});
   }

   public SpELExpressionEngine(Map<String, Object> configuration) {
      this.expressionCache = new ConcurrentHashMap();
      this.spelParser = new SpelExpressionParser();
      this.cacheEnabled = configuration != null && Boolean.parseBoolean(configuration.getOrDefault("cacheEnabled", "true").toString());
      LogUtils.debug("SpEL expression engine initialized with configuration: {}", new Object[]{configuration});
   }

   public Object evaluate(String expression, Map<String, Object> context) throws ExpressionException {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            LogUtils.debug("Evaluating SpEL expression: {}", new Object[]{expression});
            Expression spelExpression = this.getCompiledExpression(expression);
            StandardEvaluationContext evaluationContext = this.createEvaluationContext(context);
            Object result = spelExpression.getValue(evaluationContext);
            LogUtils.debug("SpEL expression evaluation result: {}", new Object[]{result});
            return result;
         } catch (Exception e) {
            LogUtils.error("Error evaluating SpEL expression: {}", new Object[]{expression, e});
            throw new ExpressionException("Failed to evaluate SpEL expression: " + expression, e);
         }
      } else {
         throw new ExpressionException("Expression cannot be null or empty");
      }
   }

   public <T> T evaluate(String expression, Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
      if (expression != null && !expression.trim().isEmpty()) {
         if (expectedType == null) {
            throw new ExpressionException("Expected type cannot be null");
         } else {
            try {
               LogUtils.debug("Evaluating SpEL expression with expected type {}: {}", new Object[]{expectedType.getSimpleName(), expression});
               Expression spelExpression = this.getCompiledExpression(expression);
               StandardEvaluationContext evaluationContext = this.createEvaluationContext(context);
               T result = (T)spelExpression.getValue(evaluationContext, expectedType);
               LogUtils.debug("SpEL expression evaluation result: {}", new Object[]{result});
               return result;
            } catch (Exception e) {
               LogUtils.error("Error evaluating SpEL expression with type {}: {}", new Object[]{expectedType.getSimpleName(), expression, e});
               throw new ExpressionException("Failed to evaluate SpEL expression: " + expression, e);
            }
         }
      } else {
         throw new ExpressionException("Expression cannot be null or empty");
      }
   }

   public boolean evaluateBoolean(String expression, Map<String, Object> context) throws ExpressionException {
      return (Boolean)this.evaluate(expression, context, Boolean.class);
   }

   public String evaluateString(String expression, Map<String, Object> context) throws ExpressionException {
      Object result = this.evaluate(expression, context);
      return result != null ? result.toString() : null;
   }

   public Number evaluateNumber(String expression, Map<String, Object> context) throws ExpressionException {
      return (Number)this.evaluate(expression, context, Number.class);
   }

   public boolean isValidExpression(String expression) {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            this.spelParser.parseExpression(expression);
            return true;
         } catch (Exception e) {
            LogUtils.debug("Invalid SpEL expression: {}", new Object[]{expression, e});
            return false;
         }
      } else {
         return false;
      }
   }

   public com.kuma.boot.flowengine.simpleflow.expression.api.Expression parseExpression(String expression) throws ExpressionException {
      if (expression != null && !expression.trim().isEmpty()) {
         try {
            Expression spelExpression = this.getCompiledExpression(expression);
            return new SpELExpressionWrapper(spelExpression, expression);
         } catch (Exception e) {
            LogUtils.error("Error parsing SpEL expression: {}", new Object[]{expression, e});
            throw new ExpressionException("Failed to parse SpEL expression: " + expression, e);
         }
      } else {
         throw new ExpressionException("Expression cannot be null or empty");
      }
   }

   public String getEngineName() {
      return "SpEL";
   }

   public String getEngineVersion() {
      return "5.2.0";
   }

   public String getSupportedSyntax() {
      return "Spring Expression Language (SpEL)";
   }

   private Expression getCompiledExpression(String expression) {
      if (this.cacheEnabled) {
         return (Expression)this.expressionCache.computeIfAbsent(expression, (key) -> {
            LogUtils.debug("Compiling and caching SpEL expression: {}", new Object[]{key});
            return this.spelParser.parseExpression(key);
         });
      } else {
         LogUtils.debug("Compiling SpEL expression (no cache): {}", new Object[]{expression});
         return this.spelParser.parseExpression(expression);
      }
   }

   private StandardEvaluationContext createEvaluationContext(Map<String, Object> context) {
      StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
      if (context != null) {
         Objects.requireNonNull(evaluationContext);
         context.forEach(evaluationContext::setVariable);
      }

      return evaluationContext;
   }

   public void clearCache() {
      if (this.cacheEnabled) {
         this.expressionCache.clear();
         LogUtils.debug("SpEL expression cache cleared", new Object[0]);
      }

   }

   public Map<String, Object> getCacheStats() {
      Map<String, Object> stats = new ConcurrentHashMap();
      stats.put("cacheEnabled", this.cacheEnabled);
      stats.put("cacheSize", this.expressionCache.size());
      return stats;
   }
}
