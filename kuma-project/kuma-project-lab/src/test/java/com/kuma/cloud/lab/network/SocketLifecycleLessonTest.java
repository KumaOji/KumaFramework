package com.kuma.cloud.lab.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.assertj.core.api.Assertions.assertThat;

@Timeout(30)
class SocketLifecycleLessonTest {
    @Test void validatesRealConnectionsAndCanRunAgainAfterCleanup() throws Exception {
        for (int run = 0; run < 2; run++) {
            var report = SocketLifecycleLesson.run();
            assertThat(report.checks().values()).containsOnly(true);
            assertThat(report.checks()).containsEntry("twoConnectionsAccepted", true)
                    .containsEntry("closedSocketCannotReconnect1", true).containsEntry("halfClose2", true);
            assertThat(report.steps()).hasSize(8);
            assertThat(report.steps().get(1).evidence().get("replies")).isEqualTo(java.util.List.of("1:1:消息1", "1:2:消息2", "1:3:消息3"));
            assertThat(report.steps().get(5).evidence().get("replies")).isEqualTo(java.util.List.of("2:1:消息1"));
        }
    }

    @Test void combinesExistingFramingTimeoutAndUdpLessons() throws Exception {
        assertThat(SocketLifecycleLesson.all().checks()).containsEntry("framingTimeoutAndUdp", true);
    }
}
