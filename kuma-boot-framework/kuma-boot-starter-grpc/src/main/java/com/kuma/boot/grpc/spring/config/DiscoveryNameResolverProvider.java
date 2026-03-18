package com.kuma.boot.grpc.spring.config;

import com.kuma.boot.common.utils.log.LogUtils;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.event.EventListener;

public final class DiscoveryNameResolverProvider extends NameResolverProvider {
   private final DiscoveryClient discoveryClient;
   private final ExecutorService executorService;
   private final Set discoveryNameResolvers;

   public DiscoveryNameResolverProvider(DiscoveryClient discoveryClient, ExecutorService executorService) {
      this.discoveryClient = discoveryClient;
      this.executorService = executorService;
      this.discoveryNameResolvers = new HashSet();
   }

   protected boolean isAvailable() {
      return true;
   }

   protected int priority() {
      return 6;
   }

   public NameResolver newNameResolver(URI uri, NameResolver.Args args) {
      DiscoveryNameResolver discoveryNameResolver = new DiscoveryNameResolver(uri.getHost(), this.discoveryClient, this.executorService, args);
      this.discoveryNameResolvers.add(discoveryNameResolver);
      return discoveryNameResolver;
   }

   public String getDefaultScheme() {
      return "discovery";
   }

   @EventListener({HeartbeatEvent.class})
   public void onHeartbeatEvent(HeartbeatEvent event) {
      LogUtils.debug("Received HeartbeatEvent, refreshing DiscoveryNameResolvers, event: {}", new Object[]{event.getValue()});

      for(DiscoveryNameResolver discoveryNameResolver : this.discoveryNameResolvers) {
         discoveryNameResolver.refreshFromExternal();
      }

   }
}
