package com.kuma.boot.prometheus.api.core;

import com.kuma.boot.prometheus.api.pojo.AlertMessage;
import com.kuma.boot.prometheus.api.pojo.TargetGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Component
@Endpoint(
   id = "kmcreactiveprometheusoperation"
)
public class ReactivePrometheusApi {
   private final String activeProfile;
   private final ReactiveDiscoveryClient discoveryClient;
   private final ApplicationEventPublisher eventPublisher;

   public ReactivePrometheusApi(@Value("${spring.profiles.active}") String activeProfile, ReactiveDiscoveryClient discoveryClient, ApplicationEventPublisher eventPublisher) {
      this.activeProfile = activeProfile;
      this.discoveryClient = discoveryClient;
      this.eventPublisher = eventPublisher;
   }

   @ReadOperation
   public Flux<TargetGroup> getList() {
      return this.discoveryClient.getServices()
            .flatMap(this.discoveryClient::getInstances)
            .groupBy(ServiceInstance::getServiceId,
                     instance -> String.format("%s:%d", instance.getHost(), instance.getPort()))
            .flatMap(instanceGrouped -> {
               Map<String, String> labels = new HashMap<>(4);
               if (StringUtils.hasText(this.activeProfile)) {
                  labels.put("profile", this.activeProfile);
               }
               String serviceId = instanceGrouped.key();
               labels.put("__meta_prometheus_job", serviceId);
               return instanceGrouped.collect(Collectors.toList())
                     .map(targets -> new TargetGroup(targets, labels));
            });
   }

   @WriteOperation
   public ResponseEntity<Object> postAlerts(AlertMessage message) {
      this.eventPublisher.publishEvent(message);
      return ResponseEntity.ok().build();
   }
}
