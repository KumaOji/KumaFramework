package com.kuma.boot.flowengine.simpleflow.expression.parser;

import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import java.util.Map;
import java.util.Set;

public interface ExpressionParser {
   Expression parse(String expressionString) throws ExpressionException;

   boolean validateSyntax(String expressionString);

   String getSupportedSyntax();

   String getParserName();

   String getParserVersion();

   boolean supportsFeature(String feature);

   Map<String, Object> getConfiguration();

   void setConfiguration(Map<String, Object> configuration);

   default Expression precompile(String expressionString) throws ExpressionException {
      return this.parse(expressionString);
   }

   default Expression[] parseBatch(String... expressionStrings) throws ExpressionException {
      if (expressionStrings != null && expressionStrings.length != 0) {
         Expression[] expressions = new Expression[expressionStrings.length];

         for(int i = 0; i < expressionStrings.length; ++i) {
            expressions[i] = this.parse(expressionStrings[i]);
         }

         return expressions;
      } else {
         return new Expression[0];
      }
   }

   default boolean isConstant(String expressionString) {
      try {
         Expression expression = this.parse(expressionString);
         return expression.isConstant();
      } catch (ExpressionException var3) {
         return false;
      }
   }

   default Set<String> getVariableNames(String expressionString) throws ExpressionException {
      Expression expression = this.parse(expressionString);
      return expression.getVariableNames();
   }
}
