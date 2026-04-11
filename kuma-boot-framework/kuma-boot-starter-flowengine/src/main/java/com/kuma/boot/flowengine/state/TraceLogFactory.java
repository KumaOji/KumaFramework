package com.kuma.boot.flowengine.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceLogFactory {
   private static final Map<String, Logger> loggers = new ConcurrentHashMap();

   public TraceLogFactory() {
   }

   public static Logger getLogger(String logName) {
      Logger logger = (Logger)loggers.get(logName);
      if (logger == null) {
         Logger newLogger = LoggerFactory.getLogger(logName);
         logger = (Logger)loggers.putIfAbsent(logName, newLogger);
         if (logger == null) {
            logger = newLogger;
         }
      }

      return logger;
   }
}
