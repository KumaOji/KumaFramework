package com.kuma.boot.prometheus.api.core;

import com.kuma.boot.prometheus.api.pojo.AlertMessage;
import com.kuma.boot.prometheus.api.pojo.TargetGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
   public Flux getList() {
      Flux var10000 = this.discoveryClient.getServices();
      ReactiveDiscoveryClient var10001 = this.discoveryClient;
      Objects.requireNonNull(var10001);
      return var10000.flatMap(var10001::getInstances).groupBy(ServiceInstance::getServiceId, (instance) -> String.format("%s:%d", instance.getHost(), instance.getPort())).flatMap((instanceGrouped) -> {
         Map<String, String> labels = new HashMap(4);
         if (StringUtils.hasText(this.activeProfile)) {
            labels.put("profile", this.activeProfile);
         }

         String serviceId = (String)instanceGrouped.key();
         labels.put("__meta_prometheus_job", serviceId);
         return instanceGrouped.collect(Collectors.toList()).map((targets) -> new TargetGroup(targets, labels));
      });
   }

   @WriteOperation
   public ResponseEntity postAlerts(AlertMessage message) {
      this.eventPublisher.publishEvent(message);
      return ResponseEntity.ok().build();
   }
}
