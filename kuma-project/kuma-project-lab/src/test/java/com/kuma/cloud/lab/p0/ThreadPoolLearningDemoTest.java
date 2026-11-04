package com.kuma.cloud.lab.p0;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.MDC;
import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolLearningDemoTest {
    @Test @Timeout(30)
    void runsAllLessonsAndRestoresCallerContextAcrossRepeatedRuns() throws Exception {
        MDC.put("labRequest", "caller");
        try {
            for (int i = 0; i < 2; i++) {
                var report = ThreadPoolLearningDemo.run("all");
                assertEquals(6, report.steps().size());
                assertTrue(report.checks().size() >= 13);
                assertTrue(report.checks().values().stream().allMatch(Boolean::booleanValue));
                assertEquals("caller", MDC.get("labRequest"));
                assertTrue(report.checks().get("separateBulkheadPreservesHealthyDependency"));
                assertTrue(report.checks().get("futureDeadlineDoesNotStopUnderlyingWork"));
            }
        } finally { MDC.remove("labRequest"); }
    }
    @Test void rejectsInvalidLessonBeforeAllocatingWorkers() {
        assertThrows(IllegalArgumentException.class, () -> ThreadPoolLearningDemo.run("unknown"));
    }
}
