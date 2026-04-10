package com.kuma.boot.logger.logging.constant;

public class LoggingConstant {
   public static final String DEFAULT_LOG_DIR = "logs";
   public static final String LOG_FILE_ALL = "all.log";
   public static final String LOG_FILE_ERROR = "error.log";
   public static final String CONSOLE_APPENDER_NAME = "CONSOLE";
   public static final String FILE_APPENDER_NAME = "FILE";
   public static final String FILE_ERROR_APPENDER_NAME = "FILE_ERROR";
   public static final String DEFAULT_FILE_LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} %level ${PID} [${APP_NAME}:${SERVER_IP}:${SERVER_PORT}] [${KMC_VERSION}] [%thread] [${SPRING_PROFILES_ACTIVE:-}] [${OS_NAME:-}:${OS_VERSION:-}:${USER_TIMEZONE:-}:${JAVA_VERSION:-}] Tlog:[%X{tl}] TraceId:[%X{kmc-trace-id:-}] TenantId:[%X{kmc-tenant-id:-}] RequestVersion:[%X{kmc-request-version:-}] OtlpTraceId:[%X{traceId:-}:%X{spanId:-}] [%tid] %class{360}:%M:%L : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}\n";
   public static final String LOGGING_FILE_PATH_KEY = "logging.file.path";
   public static final String LOGGING_FILE_NAME_KEY = "logging.file.name";
   public static final String KUMA_LOGGING_PROPERTY_SOURCE_NAME = "kumaLoggingPropertySource";
   public static final String CONSOLE_APPENDER = "CONSOLE_APPENDER";

   public LoggingConstant() {
   }
}
