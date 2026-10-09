package com.kuma.boot.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import java.nio.charset.StandardCharsets;
import net.logstash.logback.encoder.LogstashEncoder;

/** JSON 日志保留追踪和结构化字段，不导出可能承载请求参数的其他 MDC 项。 */
public class ObservabilityJsonLayout extends LayoutBase<ILoggingEvent> {
    private final LogstashEncoder encoder = new LogstashEncoder();

    @Override
    public void start() {
        encoder.setContext(getContext());
        encoder.addIncludeMdcKeyName("traceId");
        encoder.addIncludeMdcKeyName("spanId");
        encoder.addIncludeMdcKeyName("kmc-trace-id");
        encoder.start();
        super.start();
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return new String(encoder.encode(event), StandardCharsets.UTF_8).stripTrailing();
    }

    @Override
    public void stop() {
        encoder.stop();
        super.stop();
    }
}
