package com.kuma.cloud.lab.p0;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class P0ControllerTest {
    @Test
    void httpScenarioSerializesEvidenceAndChecks() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new ThreadPoolLabController()).build();
        var response = mvc.perform(post("/lab/threadpool/scenario")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var document = JsonMapper.builder().build().readTree(response);
        // Use recursive lookup because the shared Result envelope owns its nesting.
        assertNotNull(document.findValue("checks"));
        assertEquals(6, document.findValue("steps").size());
        assertTrue(document.findValue("checks").path("workerContextRestored").asBoolean());
    }
    @Test
    void isolatedTransactionConfigurationCannotLeakIntoBootComponentScan() {
        var scanner = new ClassPathScanningCandidateComponentProvider(true);
        var candidates = scanner.findCandidateComponents("com.kuma.cloud.lab.p0");
        assertEquals(3, candidates.size());
        assertTrue(candidates.stream().allMatch(bean -> bean.getBeanClassName().endsWith("LabController")));
    }
}
