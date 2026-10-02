package com.kuma.cloud.lab.kafka.service;

import com.kuma.cloud.lab.kafka.config.KafkaLabProperties;
import com.kuma.cloud.lab.kafka.domain.dto.KafkaScenarioDTO;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class KafkaScenarioServiceTest {
    @Test void commitMeansNextProcessedOffsetPerPartition() {
        var result=KafkaScenarioService.commits(List.of(Map.of("partition",0,"offset",3L),
                Map.of("partition",1,"offset",9L),Map.of("partition",0,"offset",1L)),"test");
        assertThat(result.get(new TopicPartition("test",0)).offset()).isEqualTo(4);
        assertThat(result.get(new TopicPartition("test",1)).offset()).isEqualTo(10);
    }
    @Test void recordComparisonChecksPayloadAndRejectsDuplicateConsumption() {
        var a=Map.<String,Object>of("partition",0,"offset",0L,"key","a","value","first");
        var b=Map.<String,Object>of("partition",0,"offset",1L,"key","b","value","second");
        assertThat(KafkaScenarioService.sameRecords(List.of(a,b),List.of(b,a))).isTrue();
        assertThat(KafkaScenarioService.sameRecords(List.of(a,b),List.of(a,a))).isFalse();
        assertThat(KafkaScenarioService.sameRecords(List.of(a),List.of(Map.of("partition",0,"offset",0L,"key","a","value","wrong")))).isFalse();
    }
    @Test void disabledLabAndInvalidParametersCannotCreateExperiments() {
        var disabled=new KafkaScenarioService(new KafkaLabProperties(false,"localhost:9092","lab","group",100));
        try {assertThatThrownBy(()->disabled.start(new KafkaScenarioDTO())).isInstanceOf(IllegalStateException.class);}
        finally {disabled.close();}
        var enabled=new KafkaScenarioService(new KafkaLabProperties(true,"localhost:9092","lab","group",100));
        var dto=new KafkaScenarioDTO();dto.setMessageCount(100);
        try {assertThatThrownBy(()->enabled.start(dto)).isInstanceOf(IllegalArgumentException.class);assertThat(enabled.latest()).isEmpty();}
        finally {enabled.close();}
    }
    @Test
    @EnabledIfEnvironmentVariable(named="KAFKA_LAB_IT_BOOTSTRAP",matches=".+")
    void realBrokerLifecycleReportsApiEvidenceAndCleansOnlyItsOwnTopic() throws Exception {
        var service=new KafkaScenarioService(new KafkaLabProperties(true,System.getenv("KAFKA_LAB_IT_BOOTSTRAP"),"kuma-lab-it","unused-business-group",100));
        String id=null;
        try {
            var response=service.start(new KafkaScenarioDTO());id=response.get("experimentId").toString();
            Map<String,Object> report=response;long deadline=System.nanoTime()+java.util.concurrent.TimeUnit.SECONDS.toNanos(100);
            while(report.get("status").equals("RUNNING")&&System.nanoTime()<deadline) {Thread.sleep(200);report=service.get(id);}
            System.out.println("Kafka real experiment: "+report);
            assertThat(report.get("status")).as("report: %s",report).isEqualTo("SUCCEEDED");
            assertThat((Map<String,Boolean>)report.get("checks")).hasSize(8).allSatisfy((key,value)->assertThat(value).as(key).isTrue());
            assertThat((List<Map<String,Object>>)report.get("steps")).extracting(s->s.get("stage"))
                    .contains("BROKER_ACK","UNCOMMITTED","REDELIVERY","COMMIT_ACK","SEEK_REPLAY","RESUME","INDEPENDENT_GROUP","GROUP_ASSIGNMENT","FINAL_OFFSETS");
        } finally {
            try {if(id!=null&&!service.get(id).get("status").equals("RUNNING"))assertThat(service.cleanup(id).get("cleaned")).isEqualTo(true);}
            finally {service.close();}
        }
    }
}
