package com.kuma.boot.logger.logging.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.ClassUtils;

@RefreshScope
@ConfigurationProperties("kuma.boot.logger.logging")
public class LoggingProperties {
   public static final String PREFIX = "kuma.boot.logger.logging";
   private Console console = new Console();
   private Files files = new Files();
   private Logstash logstash = new Logstash();
   private Loki loki = new Loki();

   public LoggingProperties() {
   }

   public Console getConsole() {
      return this.console;
   }

   public void setConsole(Console console) {
      this.console = console;
   }

   public Files getFiles() {
      return this.files;
   }

   public void setFiles(Files files) {
      this.files = files;
   }

   public Logstash getLogstash() {
      return this.logstash;
   }

   public void setLogstash(Logstash logstash) {
      this.logstash = logstash;
   }

   public Loki getLoki() {
      return this.loki;
   }

   public void setLoki(Loki loki) {
      this.loki = loki;
   }

   public static class Console {
      private boolean enabled = true;
      private boolean closeAfterStart = false;

      public Console() {
      }

      public boolean isCloseAfterStart() {
         return this.closeAfterStart;
      }

      public void setCloseAfterStart(boolean closeAfterStart) {
         this.closeAfterStart = closeAfterStart;
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
      }
   }

   public static class Files {
      public static final String PREFIX = "kuma.boot.logger.logging.files";
      private boolean enabled = false;
      private boolean useJsonFormat = false;

      public Files() {
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
      }

      public boolean isUseJsonFormat() {
         return this.useJsonFormat;
      }

      public void setUseJsonFormat(boolean useJsonFormat) {
         this.useJsonFormat = useJsonFormat;
      }
   }

   public static class Logstash {
      public static final String PREFIX = "kuma.boot.logger.logging.logstash";
      private boolean enabled = false;
      private String destinations = "127.0.0.1:5000";
      private Map<String, Object> customFieldMap = new HashMap();
      private int ringBufferSize = 512;

      public Logstash() {
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
      }

      public String getDestinations() {
         return this.destinations;
      }

      public void setDestinations(String destinations) {
         this.destinations = destinations;
      }

      public Map<String, Object> getCustomFieldMap() {
         return this.customFieldMap;
      }

      public void setCustomFieldMap(Map<String, Object> customFieldMap) {
         this.customFieldMap = customFieldMap;
      }

      public int getRingBufferSize() {
         return this.ringBufferSize;
      }

      public void setRingBufferSize(int ringBufferSize) {
         this.ringBufferSize = ringBufferSize;
      }
   }

   public static class Loki {
      public static final String PREFIX = "kuma.boot.logger.logging.loki";
      private boolean enabled = false;
      private LokiEncoder encoder;
      private HttpSender httpSender;
      private int batchMaxItems;
      private int batchMaxBytes;
      private long batchTimeoutMs;
      private long sendQueueMaxBytes;
      private boolean useDirectBuffers;
      private boolean drainOnStop;
      private boolean metricsEnabled;
      private boolean verbose;
      private String httpUrl;
      private long httpConnectionTimeoutMs;
      private long httpRequestTimeoutMs;
      private String httpAuthUsername;
      private String httpAuthPassword;
      private String httpTenantId;
      private String formatLabelPattern;
      private String formatLabelPatternExtend;
      private String formatLabelPairSeparator;
      private String formatLabelKeyValueSeparator;
      private boolean formatLabelNoPex;
      private String formatMessagePattern;
      private boolean formatStaticLabels;
      private boolean formatSortByTime;

      public Loki() {
         this.encoder = LoggingProperties.LokiEncoder.Json;
         this.batchMaxItems = 1000;
         this.batchMaxBytes = 4194304;
         this.batchTimeoutMs = 60000L;
         this.sendQueueMaxBytes = 41943040L;
         this.useDirectBuffers = true;
         this.drainOnStop = true;
         this.metricsEnabled = false;
         this.verbose = false;
         this.httpUrl = "http://127.0.0.1:3100/loki/api/v1/push";
         this.httpConnectionTimeoutMs = 30000L;
         this.httpRequestTimeoutMs = 5000L;
         this.formatLabelPattern = "appName=${appName},profile=${profile},host=${HOSTNAME},level=%level,traceId=%X{traceId:-NAN},requestId=%X{requestId:-NAN}";
         this.formatLabelPairSeparator = ",";
         this.formatLabelKeyValueSeparator = "=";
         this.formatLabelNoPex = true;
         this.formatStaticLabels = false;
         this.formatSortByTime = false;
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
      }

      public LokiEncoder getEncoder() {
         return this.encoder;
      }

      public void setEncoder(LokiEncoder encoder) {
         this.encoder = encoder;
      }

      public HttpSender getHttpSender() {
         return this.httpSender;
      }

      public void setHttpSender(HttpSender httpSender) {
         this.httpSender = httpSender;
      }

      public int getBatchMaxItems() {
         return this.batchMaxItems;
      }

      public void setBatchMaxItems(int batchMaxItems) {
         this.batchMaxItems = batchMaxItems;
      }

      public int getBatchMaxBytes() {
         return this.batchMaxBytes;
      }

      public void setBatchMaxBytes(int batchMaxBytes) {
         this.batchMaxBytes = batchMaxBytes;
      }

      public long getBatchTimeoutMs() {
         return this.batchTimeoutMs;
      }

      public void setBatchTimeoutMs(long batchTimeoutMs) {
         this.batchTimeoutMs = batchTimeoutMs;
      }

      public long getSendQueueMaxBytes() {
         return this.sendQueueMaxBytes;
      }

      public void setSendQueueMaxBytes(long sendQueueMaxBytes) {
         this.sendQueueMaxBytes = sendQueueMaxBytes;
      }

      public boolean isUseDirectBuffers() {
         return this.useDirectBuffers;
      }

      public void setUseDirectBuffers(boolean useDirectBuffers) {
         this.useDirectBuffers = useDirectBuffers;
      }

      public boolean isDrainOnStop() {
         return this.drainOnStop;
      }

      public void setDrainOnStop(boolean drainOnStop) {
         this.drainOnStop = drainOnStop;
      }

      public boolean isMetricsEnabled() {
         return this.metricsEnabled;
      }

      public void setMetricsEnabled(boolean metricsEnabled) {
         this.metricsEnabled = metricsEnabled;
      }

      public boolean isVerbose() {
         return this.verbose;
      }

      public void setVerbose(boolean verbose) {
         this.verbose = verbose;
      }

      public String getHttpUrl() {
         return this.httpUrl;
      }

      public void setHttpUrl(String httpUrl) {
         this.httpUrl = httpUrl;
      }

      public long getHttpConnectionTimeoutMs() {
         return this.httpConnectionTimeoutMs;
      }

      public void setHttpConnectionTimeoutMs(long httpConnectionTimeoutMs) {
         this.httpConnectionTimeoutMs = httpConnectionTimeoutMs;
      }

      public long getHttpRequestTimeoutMs() {
         return this.httpRequestTimeoutMs;
      }

      public void setHttpRequestTimeoutMs(long httpRequestTimeoutMs) {
         this.httpRequestTimeoutMs = httpRequestTimeoutMs;
      }

      public String getHttpAuthUsername() {
         return this.httpAuthUsername;
      }

      public void setHttpAuthUsername(String httpAuthUsername) {
         this.httpAuthUsername = httpAuthUsername;
      }

      public String getHttpAuthPassword() {
         return this.httpAuthPassword;
      }

      public void setHttpAuthPassword(String httpAuthPassword) {
         this.httpAuthPassword = httpAuthPassword;
      }

      public String getHttpTenantId() {
         return this.httpTenantId;
      }

      public void setHttpTenantId(String httpTenantId) {
         this.httpTenantId = httpTenantId;
      }

      public String getFormatLabelPattern() {
         return this.formatLabelPattern;
      }

      public void setFormatLabelPattern(String formatLabelPattern) {
         this.formatLabelPattern = formatLabelPattern;
      }

      public String getFormatLabelPatternExtend() {
         return this.formatLabelPatternExtend;
      }

      public void setFormatLabelPatternExtend(String formatLabelPatternExtend) {
         this.formatLabelPatternExtend = formatLabelPatternExtend;
      }

      public String getFormatLabelPairSeparator() {
         return this.formatLabelPairSeparator;
      }

      public void setFormatLabelPairSeparator(String formatLabelPairSeparator) {
         this.formatLabelPairSeparator = formatLabelPairSeparator;
      }

      public String getFormatLabelKeyValueSeparator() {
         return this.formatLabelKeyValueSeparator;
      }

      public void setFormatLabelKeyValueSeparator(String formatLabelKeyValueSeparator) {
         this.formatLabelKeyValueSeparator = formatLabelKeyValueSeparator;
      }

      public boolean isFormatLabelNoPex() {
         return this.formatLabelNoPex;
      }

      public void setFormatLabelNoPex(boolean formatLabelNoPex) {
         this.formatLabelNoPex = formatLabelNoPex;
      }

      public String getFormatMessagePattern() {
         return this.formatMessagePattern;
      }

      public void setFormatMessagePattern(String formatMessagePattern) {
         this.formatMessagePattern = formatMessagePattern;
      }

      public boolean isFormatStaticLabels() {
         return this.formatStaticLabels;
      }

      public void setFormatStaticLabels(boolean formatStaticLabels) {
         this.formatStaticLabels = formatStaticLabels;
      }

      public boolean isFormatSortByTime() {
         return this.formatSortByTime;
      }

      public void setFormatSortByTime(boolean formatSortByTime) {
         this.formatSortByTime = formatSortByTime;
      }
   }

   public static enum LokiEncoder {
      Json,
      ProtoBuf;

      private LokiEncoder() {
      }

      // $FF: synthetic method
      private static LokiEncoder[] $values() {
         return new LokiEncoder[]{Json, ProtoBuf};
      }
   }

   public static enum HttpSender {
      JAVA11("java.net.http.HttpClient"),
      OKHttp("okhttp3.OkHttpClient"),
      ApacheHttp("org.apache.http.impl.client.HttpClients");

      private final String senderClass;

      private HttpSender(String senderClass) {
         this.senderClass = senderClass;
      }

      public String getSenderClass() {
         return this.senderClass;
      }

      public boolean isAvailable() {
         return ClassUtils.isPresent(this.senderClass, (ClassLoader)null);
      }

      // $FF: synthetic method
      private static HttpSender[] $values() {
         return new HttpSender[]{JAVA11, OKHttp, ApacheHttp};
      }
   }
}
