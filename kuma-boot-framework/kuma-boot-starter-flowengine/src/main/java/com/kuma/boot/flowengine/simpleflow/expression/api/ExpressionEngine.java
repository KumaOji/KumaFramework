package com.kuma.boot.flowengine.simpleflow.expression.api;

import java.util.Map;

public interface ExpressionEngine {
   Object evaluate(String expression, Map<String, Object> context) throws ExpressionException;

   <T> T evaluate(String expression, Map<String, Object> context, Class<T> expectedType) throws ExpressionException;

   boolean evaluateBoolean(String expression, Map<String, Object> context) throws ExpressionException;

   String evaluateString(String expression, Map<String, Object> context) throws ExpressionException;

   Number evaluateNumber(String expression, Map<String, Object> context) throws ExpressionException;

   boolean isValidExpression(String expression);

   Expression parseExpression(String expression) throws ExpressionException;

   String getEngineName();

   String getEngineVersion();

   String getSupportedSyntax();
}
