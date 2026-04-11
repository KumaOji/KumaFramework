package com.kuma.boot.flowengine.simpleflow.expression.wrapper;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELExpressionWrapper implements Expression {
   private final org.springframework.expression.Expression spelExpression;
   private final String originalExpression;
   private static final Pattern VARIABLE_PATTERN = Pattern.compile("#(\\w+)");

   public SpELExpressionWrapper(org.springframework.expression.Expression spelExpression, String originalExpression) {
      this.spelExpression = spelExpression;
      this.originalExpression = originalExpression;
   }

   public Object execute(Map<String, Object> context) throws ExpressionException {
      try {
         LogUtils.debug("Evaluating SpEL expression: {}", new Object[]{this.originalExpression});
         EvaluationContext evaluationContext = this.createEvaluationContext(context);
         Object result = this.spelExpression.getValue(evaluationContext);
         LogUtils.debug("SpEL expression evaluation result: {}", new Object[]{result});
         return result;
      } catch (Exception e) {
         LogUtils.error("Error evaluating SpEL expression: {}", new Object[]{this.originalExpression, e});
         throw new ExpressionException("Failed to evaluate SpEL expression: " + this.originalExpression, e);
      }
   }

   public <T> T execute(Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
      if (expectedType == null) {
         throw new ExpressionException("Expected type cannot be null");
      } else {
         try {
            LogUtils.debug("Evaluating SpEL expression with expected type {}: {}", new Object[]{expectedType.getSimpleName(), this.originalExpression});
            EvaluationContext evaluationContext = this.createEvaluationContext(context);
            T result = (T)this.spelExpression.getValue(evaluationContext, expectedType);
            LogUtils.debug("SpEL expression evaluation result: {}", new Object[]{result});
            return result;
         } catch (Exception e) {
            LogUtils.error("Error evaluating SpEL expression with type {}: {}", new Object[]{expectedType.getSimpleName(), this.originalExpression, e});
            throw new ExpressionException("Failed to evaluate SpEL expression: " + this.originalExpression, e);
         }
      }
   }

   public String getExpressionString() {
      return this.originalExpression;
   }

   public Set<String> getVariableNames() {
      Set<String> variables = new HashSet();
      Matcher matcher = VARIABLE_PATTERN.matcher(this.originalExpression);

      while(matcher.find()) {
         variables.add(matcher.group(1));
      }

      return variables;
   }

   public boolean isConstant() {
      return this.getVariableNames().isEmpty();
   }

   public Expression.ExpressionType getType() {
      try {
         Class<?> returnType = this.spelExpression.getValueType();
         if (returnType == null) {
            return Expression.ExpressionType.UNKNOWN;
         } else if (!Boolean.class.isAssignableFrom(returnType) && !Boolean.TYPE.isAssignableFrom(returnType)) {
            if (!Number.class.isAssignableFrom(returnType) && !returnType.isPrimitive()) {
               return String.class.isAssignableFrom(returnType) ? Expression.ExpressionType.STRING : Expression.ExpressionType.OBJECT;
            } else {
               return Expression.ExpressionType.NUMERIC;
            }
         } else {
            return Expression.ExpressionType.BOOLEAN;
         }
      } catch (Exception var2) {
         return Expression.ExpressionType.UNKNOWN;
      }
   }

   public Class<?> getReturnType() {
      try {
         return this.spelExpression.getValueType();
      } catch (Exception var2) {
         return Object.class;
      }
   }

   public Expression.ValidationResult validate(Map<String, Object> context) {
      try {
         EvaluationContext evaluationContext = this.createEvaluationContext(context);
         this.spelExpression.getValue(evaluationContext);
         return Expression.ValidationResult.success();
      } catch (Exception e) {
         return Expression.ValidationResult.failure(e.getMessage());
      }
   }

   private EvaluationContext createEvaluationContext(Map<String, Object> context) {
      StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
      if (context != null) {
         Objects.requireNonNull(evaluationContext);
         context.forEach(evaluationContext::setVariable);
      }

      return evaluationContext;
   }

   public org.springframework.expression.Expression getSpelExpression() {
      return this.spelExpression;
   }

   public String toString() {
      return "SpELExpressionWrapper{expression='" + this.originalExpression + "', engineType='spel'}";
   }
}
