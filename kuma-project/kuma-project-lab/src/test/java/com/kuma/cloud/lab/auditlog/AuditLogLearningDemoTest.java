package com.kuma.cloud.lab.auditlog;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuditLogLearningDemoTest {
    @Test
    void starterCapturesOperationsWithoutChangingBusinessSemantics() {
        var report = AuditLogLearningDemo.run("all");
        assertEquals(5, report.steps().size());
        assertEquals(5, report.records().size());
        assertTrue(report.checks().values().stream().allMatch(Boolean::booleanValue));
        assertEquals(1, report.records().stream().filter(record -> !record.success()).count());
        assertTrue(report.records().stream().noneMatch(record -> String.valueOf(record.params()).contains("LAB-ONLY-SECRET")
                || String.valueOf(record.result()).contains("LAB-ONLY-SECRET")));
    }
    @Test
    void runsHaveIndependentStoresAndStorageFailuresAreVisible() {
        assertEquals(1, AuditLogLearningDemo.run("success").records().size());
        assertEquals(1, AuditLogLearningDemo.run("success").records().size());
        var storage = AuditLogLearningDemo.run("storage");
        assertTrue(storage.records().isEmpty());
        assertTrue(storage.checks().get("auditStorageFailureDoesNotFailBusiness"));
    }
    @Test
    void controllerReturnsTheSelectedLessonAndUnknownLessonIsRejected() {
        assertNotNull(new AuditLogLabController().proxy());
        assertThrows(IllegalArgumentException.class, () -> AuditLogLearningDemo.run("unknown"));
    }
    @Test
    void hostComponentScanDoesNotInstallTheTeachingStoreOrIdentity() {
        try (var context = new org.springframework.context.annotation.AnnotationConfigApplicationContext()) {
            context.scan("com.kuma.cloud.lab.auditlog");
            context.refresh();
            assertNotNull(context.getBean(AuditLogLabController.class));
            assertTrue(context.getBeansOfType(AuditLogLearningDemo.RecordingStore.class).isEmpty());
            assertTrue(context.getBeansOfType(com.kuma.boot.auditlog.core.AuditOperatorProvider.class).isEmpty());
        }
    }
}
