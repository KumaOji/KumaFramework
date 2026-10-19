package com.kuma.cloud.lab.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Timeout(30)
class SessionLearningDemoTest {
    @ParameterizedTest
    @ValueSource(strings = {"all", "lifecycle", "isolation", "expiry"})
    void validatesRealServletCookiesAndSessions(String lesson) throws Exception {
        var report = SessionLearningDemo.run(lesson);
        assertThat(report.lesson()).isEqualTo(lesson);
        assertThat(report.checks().values()).isNotEmpty().containsOnly(true);
        assertThat(report.checks()).containsEntry("containerAndFilesCleaned", true);
        assertThat(report.steps()).allSatisfy(step -> assertThat(step.status()).isEqualTo(200));
        int port = report.steps().getFirst().response().path("serverPort").asInt();
        try (var socket = new java.net.Socket()) {
            assertThatThrownBy(() -> socket.connect(new java.net.InetSocketAddress("127.0.0.1", port), 1000))
                    .isInstanceOf(java.io.IOException.class);
        }
        if (lesson.equals("all") || lesson.equals("isolation")) {
            assertThat(report.checks()).containsEntry("sessionSurvivesConnectionChange", true).containsEntry("clientsIsolated", true);
        }
        if (lesson.equals("all") || lesson.equals("lifecycle")) {
            assertThat(report.checks()).containsEntry("oldIdRejected", true).containsEntry("logoutInvalidatesServerSession", true);
        }
        if (lesson.equals("all") || lesson.equals("expiry")) {
            assertThat(report.checks()).containsEntry("idleSessionExpired", true).containsEntry("newSessionAfterExpiry", true);
        }
    }

    @Test void rejectsUnknownLessonBeforeStartingContainer() {
        assertThatThrownBy(() -> SessionLearningDemo.run("unknown")).isInstanceOf(IllegalArgumentException.class);
    }
}
