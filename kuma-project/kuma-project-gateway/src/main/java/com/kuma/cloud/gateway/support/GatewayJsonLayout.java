package com.kuma.cloud.gateway.support;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import java.nio.charset.StandardCharsets;
import net.logstash.logback.encoder.LogstashEncoder;

/** Loki 与控制台使用同一 JSON 编码器，保留 SLF4J key-value 关联字段。 */
public class GatewayJsonLayout extends LayoutBase<ILoggingEvent> {
    private final LogstashEncoder encoder = new LogstashEncoder();

    @Override
    public void start() {
        encoder.setContext(getContext());
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
