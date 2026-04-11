package com.kuma.boot.flowengine.simpleflow.expression.parser;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import com.kuma.boot.flowengine.simpleflow.expression.evaluator.JexlExpressionWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;

public class JexlExpressionParser implements ExpressionParser {
   private static final String PARSER_NAME = "JEXL";
   private static final String PARSER_VERSION = "3.0";
   private static final String SUPPORTED_SYNTAX = "Apache Commons JEXL Expression Language";
   private final JexlEngine jexlEngine;
   private final Map<String, Object> configuration;

   public JexlExpressionParser() {
      this(createDefaultJexlEngine());
   }

   public JexlExpressionParser(JexlEngine jexlEngine) {
      this.jexlEngine = (JexlEngine)Objects.requireNonNull(jexlEngine, "JexlEngine cannot be null");
      this.configuration = new HashMap();
      this.initializeConfiguration();
   }

   public JexlExpressionParser(Map<String, Object> configuration) {
      this.configuration = new HashMap((Map)(configuration != null ? configuration : new HashMap()));
      this.jexlEngine = createJexlEngineFromConfiguration(this.configuration);
   }

   public Expression parse(String expressionString) throws ExpressionException {
      if (expressionString != null && !expressionString.trim().isEmpty()) {
         try {
            JexlExpression jexlExpression = this.jexlEngine.createExpression(expressionString);
            LogUtils.debug("Successfully parsed expression: {}", new Object[]{expressionString});
            return new JexlExpressionWrapper(expressionString, jexlExpression);
         } catch (JexlException e) {
            LogUtils.error("Failed to parse expression: {}", new Object[]{expressionString, e});
            int position = this.extractErrorPosition(e);
            String errorCode = this.extractErrorCode(e);
            throw new ExpressionException("Failed to parse expression: " + e.getMessage(), expressionString, position, errorCode, e);
         } catch (Exception e) {
            LogUtils.error("Unexpected error parsing expression: {}", new Object[]{expressionString, e});
            throw new ExpressionException("Unexpected error: " + e.getMessage(), expressionString, e);
         }
      } else {
         throw new ExpressionException("Expression string cannot be null or empty", expressionString);
      }
   }

   public boolean validateSyntax(String expressionString) {
      if (expressionString != null && !expressionString.trim().isEmpty()) {
         try {
            this.jexlEngine.createExpression(expressionString);
            return true;
         } catch (Exception e) {
            LogUtils.debug("Expression syntax validation failed: {}", new Object[]{expressionString, e});
            return false;
         }
      } else {
         return false;
      }
   }

   public String getSupportedSyntax() {
      return "Apache Commons JEXL Expression Language";
   }

   public String getParserName() {
      return "JEXL";
   }

   public String getParserVersion() {
      return "3.0";
   }

   public boolean supportsFeature(String feature) {
      if (feature == null) {
         return false;
      } else {
         switch (feature.toLowerCase()) {
            case "arithmetic":
            case "comparison":
            case "logical":
            case "string":
            case "array":
            case "map":
            case "method_call":
            case "property_access":
            case "conditional":
            case "lambda":
               return true;
            case "regex":
            case "date":
            case "math_functions":
               return true;
            default:
               return false;
         }
      }
   }

   public Map<String, Object> getConfiguration() {
      return new HashMap(this.configuration);
   }

   public void setConfiguration(Map<String, Object> configuration) {
      if (configuration != null) {
         this.configuration.clear();
         this.configuration.putAll(configuration);
      }

   }

   public Expression precompile(String expressionString) throws ExpressionException {
      return this.parse(expressionString);
   }

   public JexlEngine getJexlEngine() {
      return this.jexlEngine;
   }

   private static JexlEngine createDefaultJexlEngine() {
      return (new JexlBuilder()).cache(512).strict(false).silent(false).safe(true).create();
   }

   private static JexlEngine createJexlEngineFromConfiguration(Map<String, Object> config) {
      JexlBuilder builder = new JexlBuilder();
      Object cacheSize = config.get("cacheSize");
      if (cacheSize instanceof Number) {
         builder.cache(((Number)cacheSize).intValue());
      } else {
         builder.cache(512);
      }

      Object strict = config.get("strict");
      if (strict instanceof Boolean) {
         builder.strict((Boolean)strict);
      } else {
         builder.strict(false);
      }

      Object silent = config.get("silent");
      if (silent instanceof Boolean) {
         builder.silent((Boolean)silent);
      } else {
         builder.silent(false);
      }

      Object safe = config.get("safe");
      if (safe instanceof Boolean) {
         builder.safe((Boolean)safe);
      } else {
         builder.safe(true);
      }

      return builder.create();
   }

   private void initializeConfiguration() {
      this.configuration.put("cacheSize", 512);
      this.configuration.put("strict", false);
      this.configuration.put("silent", false);
      this.configuration.put("safe", true);
      this.configuration.put("parserName", "JEXL");
      this.configuration.put("parserVersion", "3.0");
      this.configuration.put("supportedSyntax", "Apache Commons JEXL Expression Language");
   }

   private int extractErrorPosition(JexlException exception) {
      try {
         if (exception.getInfo() != null) {
            return exception.getInfo().getColumn();
         }
      } catch (Exception e) {
         LogUtils.debug("Failed to extract error position", new Object[]{e});
      }

      return -1;
   }

   private String extractErrorCode(JexlException exception) {
      if (exception instanceof JexlException.Parsing) {
         return "PARSING_ERROR";
      } else if (exception instanceof JexlException.Variable) {
         return "VARIABLE_ERROR";
      } else if (exception instanceof JexlException.Method) {
         return "METHOD_ERROR";
      } else {
         return exception instanceof JexlException.Property ? "PROPERTY_ERROR" : "UNKNOWN_ERROR";
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.configuration.get("cacheSize"));
      return "JexlExpressionParser{name='JEXL', version='3.0', cacheSize=" + var10000 + ", strict=" + String.valueOf(this.configuration.get("strict")) + ", safe=" + String.valueOf(this.configuration.get("safe")) + "}";
   }
}
