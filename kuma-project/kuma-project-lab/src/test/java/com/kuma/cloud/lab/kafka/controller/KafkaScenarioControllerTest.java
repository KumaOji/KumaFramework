package com.kuma.cloud.lab.kafka.controller;

import com.kuma.cloud.lab.kafka.domain.dto.KafkaScenarioDTO;
import com.kuma.cloud.lab.kafka.service.KafkaLabEventStore;
import com.kuma.cloud.lab.kafka.service.KafkaLabTestService;
import com.kuma.cloud.lab.kafka.service.KafkaScenarioService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

class KafkaScenarioControllerTest {
    @Test void httpStartPollInspectAndCleanupRoutesAreRegistered() throws Exception {
        var service=mock(KafkaScenarioService.class);
        var controller=new KafkaTestController(mock(KafkaLabTestService.class),mock(KafkaLabEventStore.class),service);
        var mvc=MockMvcBuilders.standaloneSetup(controller).build();
        var report=Map.<String,Object>of("experimentId","abc","status","RUNNING","steps",List.of());
        when(service.start(any(KafkaScenarioDTO.class))).thenReturn(report);
        when(service.get("abc")).thenReturn(report);when(service.latest()).thenReturn(List.of(report));
        when(service.cluster()).thenReturn(Map.of("brokers",List.of()));
        when(service.topic("lesson","group")).thenReturn(Map.of("topic","lesson"));
        when(service.cleanup("abc")).thenReturn(Map.of("cleaned",true));
        var response=mvc.perform(post("/lab/kafka/scenario").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(response).contains("\"experimentId\":\"abc\"");
        mvc.perform(get("/lab/kafka/experiments/abc")).andExpect(status().isOk());
        mvc.perform(get("/lab/kafka/experiments")).andExpect(status().isOk());
        mvc.perform(get("/lab/kafka/cluster")).andExpect(status().isOk());
        mvc.perform(get("/lab/kafka/topic").param("topic","lesson").param("groupId","group")).andExpect(status().isOk());
        mvc.perform(delete("/lab/kafka/experiments/abc")).andExpect(status().isOk());
        verify(service).cleanup("abc");
        mvc.perform(post("/lab/kafka/scenario").contentType(MediaType.APPLICATION_JSON).content("{\"partitions\":0}"))
                .andExpect(status().isBadRequest());
        verify(service,times(1)).start(any(KafkaScenarioDTO.class));
    }
}
