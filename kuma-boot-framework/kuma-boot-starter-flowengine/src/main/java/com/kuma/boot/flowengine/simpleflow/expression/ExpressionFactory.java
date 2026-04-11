package com.kuma.boot.flowengine.simpleflow.expression;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.expression.api.Expression;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.api.ExpressionException;
import com.kuma.boot.flowengine.simpleflow.expression.context.ExpressionContext;
import com.kuma.boot.flowengine.simpleflow.expression.evaluator.ChainExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.evaluator.GroovyScriptExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.evaluator.JexlExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.evaluator.KotlinScriptExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.evaluator.SpELExpressionEngine;
import com.kuma.boot.flowengine.simpleflow.expression.parser.ExpressionParser;
import com.kuma.boot.flowengine.simpleflow.expression.parser.JexlExpressionParser;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionFactory {
   private static final String DEFAULT_ENGINE_TYPE = "jexl";
   private static final Map<String, ExpressionEngine> engineCache = new ConcurrentHashMap();
   private static final Map<String, ExpressionParser> parserCache = new ConcurrentHashMap();
   private static volatile ExpressionEngine defaultEngine;
   private static volatile ExpressionParser defaultParser;

   private ExpressionFactory() {
   }

   public static ExpressionEngine getDefaultEngine() {
      if (defaultEngine == null) {
         synchronized(ExpressionFactory.class) {
            if (defaultEngine == null) {
               defaultEngine = createEngine("jexl");
            }
         }
      }

      return defaultEngine;
   }

   public static ExpressionParser getDefaultParser() {
      if (defaultParser == null) {
         synchronized(ExpressionFactory.class) {
            if (defaultParser == null) {
               defaultParser = createParser("jexl");
            }
         }
      }

      return defaultParser;
   }

   public static ExpressionEngine createEngine(String engineType) {
      return createEngine(engineType, (Map)null);
   }

   public static ExpressionEngine createEngine(String engineType, Map<String, Object> configuration) {
      if (engineType == null || engineType.trim().isEmpty()) {
         engineType = "jexl";
      }

      String cacheKey = engineType + "_" + (configuration != null ? configuration.hashCode() : 0);
      return (ExpressionEngine)engineCache.computeIfAbsent(cacheKey, (key) -> {
         LogUtils.debug("Creating expression engine: {}", new Object[]{engineType});
         switch (engineType.toLowerCase()) {
            case "jexl":
               if (configuration != null) {
                  return new JexlExpressionEngine(configuration);
               }

               return new JexlExpressionEngine();
            case "spel":
               if (configuration != null) {
                  return new SpELExpressionEngine(configuration);
               }

               return new SpELExpressionEngine();
            case "groovy":
            case "groovy-script":
               if (configuration != null) {
                  return new GroovyScriptExpressionEngine(configuration);
               }

               return new GroovyScriptExpressionEngine();
            case "kotlin":
            case "kotlin-script":
            case "kts":
               if (configuration != null) {
                  return new KotlinScriptExpressionEngine(configuration);
               }

               return new KotlinScriptExpressionEngine();
            case "chain":
            case "chain-expression":
               return new ChainExpressionEngine();
            default:
               LogUtils.warn("Unknown engine type: {}, using default JEXL engine", new Object[]{engineType});
               return new JexlExpressionEngine();
         }
      });
   }

   public static ExpressionParser createParser(String parserType) {
      return createParser(parserType, (Map)null);
   }

   public static ExpressionParser createParser(String parserType, Map<String, Object> configuration) {
      if (parserType == null || parserType.trim().isEmpty()) {
         parserType = "jexl";
      }

      String cacheKey = parserType + "_" + (configuration != null ? configuration.hashCode() : 0);
      return (ExpressionParser)parserCache.computeIfAbsent(cacheKey, (key) -> {
         LogUtils.debug("Creating expression parser: {}", new Object[]{parserType});
         switch (parserType.toLowerCase()) {
            case "jexl":
               if (configuration != null) {
                  return new JexlExpressionParser(configuration);
               }

               return new JexlExpressionParser();
            default:
               LogUtils.warn("Unknown parser type: {}, using default JEXL parser", new Object[]{parserType});
               return new JexlExpressionParser();
         }
      });
   }

   public static ExpressionEngine getEngine(String engineType) {
      return createEngine(engineType);
   }

   public static ExpressionParser getParser(String parserType) {
      return createParser(parserType);
   }

   public static void setDefaultEngine(ExpressionEngine engine) {
      defaultEngine = engine;
      LogUtils.info("Default expression engine set to: {}", new Object[]{engine != null ? engine.getEngineName() : "null"});
   }

   public static void setDefaultParser(ExpressionParser parser) {
      defaultParser = parser;
      LogUtils.info("Default expression parser set to: {}", new Object[]{parser != null ? parser.getParserName() : "null"});
   }

   public static Expression parse(String expressionString) throws ExpressionException {
      return getDefaultParser().parse(expressionString);
   }

   public static Object evaluate(String expressionString, Map<String, Object> context) throws ExpressionException {
      return getDefaultEngine().evaluate(expressionString, context);
   }

   public static Object evaluate(String expressionString, ExpressionContext context) throws ExpressionException {
      return evaluate(expressionString, context != null ? context.getAllVariables() : null);
   }

   public static <T> T evaluate(String expressionString, Map<String, Object> context, Class<T> expectedType) throws ExpressionException {
      return (T)getDefaultEngine().evaluate(expressionString, context, expectedType);
   }

   public static <T> T evaluate(String expressionString, ExpressionContext context, Class<T> expectedType) throws ExpressionException {
      return (T)evaluate(expressionString, context != null ? context.getAllVariables() : null, expectedType);
   }

   public static boolean validateSyntax(String expressionString) {
      return getDefaultParser().validateSyntax(expressionString);
   }

   public static ExpressionContext createContext() {
      return new ExpressionContext();
   }

   public static ExpressionContext createContext(Map<String, Object> initialVariables) {
      return new ExpressionContext(initialVariables);
   }

   public static void clearCache() {
      engineCache.clear();
      parserCache.clear();
      LogUtils.info("Expression factory cache cleared", new Object[0]);
   }

   public static Map<String, Object> getCacheStats() {
      Map<String, Object> stats = new ConcurrentHashMap();
      stats.put("engineCacheSize", engineCache.size());
      stats.put("parserCacheSize", parserCache.size());
      stats.put("engineCacheKeys", engineCache.keySet());
      stats.put("parserCacheKeys", parserCache.keySet());
      return stats;
   }

   public static String[] getSupportedEngineTypes() {
      return new String[]{"jexl", "spel", "groovy", "groovy-script", "kotlin", "kotlin-script", "kts", "chain", "chain-expression"};
   }

   public static String[] getSupportedParserTypes() {
      return new String[]{"jexl"};
   }

   public static boolean isSupportedEngineType(String engineType) {
      if (engineType == null) {
         return false;
      } else {
         for(String supportedType : getSupportedEngineTypes()) {
            if (supportedType.equalsIgnoreCase(engineType)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isSupportedParserType(String parserType) {
      if (parserType == null) {
         return false;
      } else {
         for(String supportedType : getSupportedParserTypes()) {
            if (supportedType.equalsIgnoreCase(parserType)) {
               return true;
            }
         }

         return false;
      }
   }
}
