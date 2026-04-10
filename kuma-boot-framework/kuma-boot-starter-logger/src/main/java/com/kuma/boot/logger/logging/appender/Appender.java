package com.kuma.boot.logger.logging.appender;

public enum Appender {
   CONSOLE,
   FILE,
   FILE_JSON,
   GRAY_LOG,
   KAFKA,
   LOG_STASH,
   LOKI;

   private Appender() {
   }

   // $FF: synthetic method
   private static Appender[] $values() {
      return new Appender[]{CONSOLE, FILE, FILE_JSON, GRAY_LOG, KAFKA, LOG_STASH, LOKI};
   }
}
