package com.kuma.boot.prometheus.api.core;

import com.kuma.boot.prometheus.api.pojo.AlertMessage;
import com.kuma.boot.prometheus.api.pojo.TargetGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Endpoint(
   id = "kmcprometheusoperation"
)
public class PrometheusApi {
   private final String activeProfile;
   private final DiscoveryClient discoveryClient;
   private final ApplicationEventPublisher eventPublisher;

   public PrometheusApi(@Value("${spring.profiles.active}") String activeProfile, DiscoveryClient discoveryClient, ApplicationEventPublisher eventPublisher) {
      this.activeProfile = activeProfile;
      this.discoveryClient = discoveryClient;
      this.eventPublisher = eventPublisher;
   }

   @ReadOperation
   public List<TargetGroup> getList() {
      List<String> serviceIdList = this.discoveryClient.getServices();
      if (serviceIdList != null && !serviceIdList.isEmpty()) {
         List<TargetGroup> targetGroupList = new ArrayList<>();

         for(String serviceId : serviceIdList) {
            List<ServiceInstance> instanceList = this.discoveryClient.getInstances(serviceId);
            List<String> targets = new ArrayList<>();

            for(ServiceInstance instance : instanceList) {
               targets.add(String.format("%s:%d", instance.getHost(), instance.getPort()));
            }

            Map<String, String> labels = new HashMap<>(4);
            if (StringUtils.hasText(this.activeProfile)) {
               labels.put("profile", this.activeProfile);
            }

            labels.put("__meta_prometheus_job", serviceId);
            targetGroupList.add(new TargetGroup(targets, labels));
         }

         return targetGroupList;
      } else {
         return Collections.emptyList();
      }
   }

   @WriteOperation
   public ResponseEntity<Object> postAlerts(AlertMessage message) {
      this.eventPublisher.publishEvent(message);
      return ResponseEntity.ok().build();
   }
}
