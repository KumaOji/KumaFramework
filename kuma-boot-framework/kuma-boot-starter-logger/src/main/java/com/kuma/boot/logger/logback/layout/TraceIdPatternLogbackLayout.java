package com.kuma.boot.logger.logback.layout;

import ch.qos.logback.classic.PatternLayout;
import org.apache.skywalking.apm.toolkit.log.logback.v1.x.LogbackPatternConverter;
import org.apache.skywalking.apm.toolkit.log.logback.v1.x.LogbackSkyWalkingContextPatternConverter;
import org.apache.skywalking.apm.toolkit.log.logback.v1.x.mdc.LogbackMDCPatternConverter;

public class TraceIdPatternLogbackLayout extends PatternLayout {
   public TraceIdPatternLogbackLayout() {
   }

   static {
      DEFAULT_CONVERTER_SUPPLIER_MAP.put("tid", LogbackPatternConverter::new);
      DEFAULT_CONVERTER_SUPPLIER_MAP.put("sw_ctx", LogbackSkyWalkingContextPatternConverter::new);
      DEFAULT_CONVERTER_SUPPLIER_MAP.put("X", LogbackMDCPatternConverter::new);
      DEFAULT_CONVERTER_SUPPLIER_MAP.put("mdc", LogbackMDCPatternConverter::new);
   }
}
