package com.kuma.boot.grpc.spring.config;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import io.grpc.StatusOr;
import io.grpc.Attributes.Key;
import io.grpc.NameResolver.ResolutionResult;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

final class DiscoveryNameResolver extends NameResolver {
   private final String serviceId;
   private final DiscoveryClient discoveryClient;
   private final ExecutorService executorService;
   private final NameResolver.ServiceConfigParser serviceConfigParser;
   private final AtomicReference<List<ServiceInstance>> serviceInstanceReference;
   private final AtomicBoolean resolving;
   private NameResolver.Listener2 listener;

   public DiscoveryNameResolver(String serviceId, DiscoveryClient discoveryClient, ExecutorService executorService, NameResolver.Args args) {
      this.serviceId = serviceId;
      this.discoveryClient = discoveryClient;
      this.executorService = executorService;
      this.serviceInstanceReference = new AtomicReference<>(Collections.emptyList());
      this.serviceConfigParser = args.getServiceConfigParser();
      this.resolving = new AtomicBoolean(false);
   }

   public String getServiceAuthority() {
      return this.serviceId;
   }

   public void shutdown() {
      this.serviceInstanceReference.set(null);
   }

   public void start(NameResolver.Listener2 listener) {
      this.listener = listener;
      this.resolve();
   }

   public void refresh() {
      this.resolve();
   }

   public void refreshFromExternal() {
      this.executorService.execute(() -> {
         if (ObjectUtils.isNotNull(this.listener)) {
            this.resolve();
         }

      });
   }

   private void resolve() {
      if (this.resolving.compareAndSet(false, true)) {
         this.executorService.execute(() -> {
            this.serviceInstanceReference.set(this.resolveInternal());
            this.resolving.set(false);
         });
      }

   }

   private List<ServiceInstance> resolveInternal() {
      List<ServiceInstance> serviceInstances = this.serviceInstanceReference.get();
      List<ServiceInstance> newServiceInstanceList = this.discoveryClient.getInstances(this.serviceId);
      if (StringUtils.isEmpty(newServiceInstanceList)) {
         this.listener.onError(Status.UNAVAILABLE.withDescription("No servers found for " + this.serviceId));
         return Collections.emptyList();
      } else if (!this.isUpdateServiceInstance(serviceInstances, newServiceInstanceList)) {
         return serviceInstances;
      } else {
         this.listener.onResult(ResolutionResult.newBuilder().setAddressesOrError(StatusOr.fromValue(this.toAddresses(newServiceInstanceList))).setServiceConfig(this.resolveServiceConfig(newServiceInstanceList)).build());
         return newServiceInstanceList;
      }
   }

   private boolean isUpdateServiceInstance(List<ServiceInstance> serviceInstances, List<ServiceInstance> newServiceInstanceList) {
      if (serviceInstances.size() != newServiceInstanceList.size()) {
         return true;
      } else {
         Set<String> oldSet = serviceInstances.stream().map(this::getAddressStr).collect(Collectors.toSet());
         Set<String> newSet = newServiceInstanceList.stream().map(this::getAddressStr).collect(Collectors.toSet());
         return !ObjectUtils.equals(oldSet, newSet);
      }
   }

   private NameResolver.ConfigOrError resolveServiceConfig(List<ServiceInstance> newServiceInstanceList) {
      String serviceConfig = this.getServiceConfig(newServiceInstanceList);
      return StringUtils.hasText(serviceConfig) ? this.serviceConfigParser.parseServiceConfig(JacksonUtils.toMap(serviceConfig)) : null;
   }

   private String getServiceConfig(List<ServiceInstance> newServiceInstanceList) {
      return StringUtils.isEmpty(newServiceInstanceList) ? "" : (String)newServiceInstanceList.stream().map((item) -> (String)((Map<String, String>)((ServiceInstance)item).getMetadata()).getOrDefault("grpc_service_config", "")).collect(Collectors.joining(","));
   }

   private List<EquivalentAddressGroup> toAddresses(List<ServiceInstance> newServiceInstanceList) {
      List<EquivalentAddressGroup> addresses = new ArrayList<>(newServiceInstanceList.size());

      for(ServiceInstance serviceInstance : newServiceInstanceList) {
         addresses.add(this.toAddress(serviceInstance));
      }

      return addresses;
   }

   private EquivalentAddressGroup toAddress(ServiceInstance serviceInstance) {
      String host = serviceInstance.getHost();
      int port = this.getGrpcPost(serviceInstance);
      return new EquivalentAddressGroup(new InetSocketAddress(host, port), this.getAttributes(serviceInstance));
   }

   private Attributes getAttributes(ServiceInstance serviceInstance) {
      return Attributes.newBuilder().set(Key.create("serviceId"), this.serviceId).set(Key.create("instanceId"), serviceInstance.getInstanceId()).build();
   }

   private String getAddressStr(ServiceInstance serviceInstance) {
      String var10000 = serviceInstance.getHost();
      return var10000 + ":" + this.getGrpcPost(serviceInstance);
   }

   private int getGrpcPost(ServiceInstance serviceInstance) {
      Map<String, String> metadata = serviceInstance.getMetadata();
      return ObjectUtils.isNotNull(metadata) && !metadata.isEmpty() ? Integer.parseInt((String)metadata.getOrDefault("grpc_port", "9090")) : 9090;
   }
}
