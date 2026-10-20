package com.kuma.boot.logger.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.skywalking.apm.toolkit.log.logback.v1.x.LogbackPatternConverter;

/** Keeps a captured Agent trace ID available when completion logging switches threads. */
public class CorrelatedSkyWalkingTraceIdConverter extends ClassicConverter {
    private final LogbackPatternConverter delegate = new LogbackPatternConverter();

    @Override
    public String convert(ILoggingEvent event) {
        String captured = event.getMDCPropertyMap().get("tid");
        return captured != null && !captured.isBlank() ? "TID:" + captured : delegate.convert(event);
    }
}
