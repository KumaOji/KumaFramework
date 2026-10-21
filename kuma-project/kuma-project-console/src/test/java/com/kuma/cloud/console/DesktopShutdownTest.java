package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.assertThat;

class DesktopShutdownTest {
    @Test void stopsOnDesktopQuitWithoutExecutingOtherInput() {
        var stopped = new AtomicInteger();
        DesktopShutdown.await(new ByteArrayInputStream("ignored\nkuma-console:quit\n".getBytes(StandardCharsets.UTF_8)), stopped::incrementAndGet);
        assertThat(stopped).hasValue(1);
    }

    @Test void stopsWhenTheDesktopPipeCloses() {
        var stopped = new AtomicInteger();
        DesktopShutdown.await(new ByteArrayInputStream(new byte[0]), stopped::incrementAndGet);
        assertThat(stopped).hasValue(1);
    }
}
