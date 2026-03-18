package com.kuma.boot.grpc.spring.annotation;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.Attributes.Key;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.SharedResourceHolder;
import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.event.EventListener;

public class DiscoveryClientResolverFactory extends NameResolverProvider {
   public static final String DISCOVERY_SCHEME = "discovery";
   public static final Attributes.Key<String> DISCOVERY_SERVICE_NAME_KEY = Key.create("serviceName");
   public static final Attributes.Key<String> DISCOVERY_INSTANCE_ID_KEY = Key.create("instanceId");
   private final Set discoveryClientNameResolvers = ConcurrentHashMap.newKeySet();
   private final HeartbeatMonitor monitor = new HeartbeatMonitor();
   private final DiscoveryClient client;

   public DiscoveryClientResolverFactory(final DiscoveryClient client) {
      this.client = (DiscoveryClient)Objects.requireNonNull(client, "client");
   }

   @Nullable
   public NameResolver newNameResolver(final URI targetUri, final NameResolver.Args args) {
      if ("discovery".equals(targetUri.getScheme())) {
         String serviceName = targetUri.getPath();
         if (serviceName != null && serviceName.length() > 1 && serviceName.startsWith("/")) {
            DiscoveryClientNameResolver nameResolver = this.newNameResolver(serviceName.substring(1), args);
            this.discoveryClientNameResolvers.add(nameResolver);
            return nameResolver;
         } else {
            throw new IllegalArgumentException("Incorrectly formatted target uri; expected: 'discovery:[//]/<service-name>'; but was '" + targetUri.toString() + "'");
         }
      } else {
         return null;
      }
   }

   protected DiscoveryClientNameResolver newNameResolver(final String serviceName, final NameResolver.Args args) {
      DiscoveryClient var10003 = this.client;
      SharedResourceHolder.Resource var10005 = GrpcUtil.SHARED_CHANNEL_EXECUTOR;
      Set var10006 = this.discoveryClientNameResolvers;
      Objects.requireNonNull(var10006);
      return new DiscoveryClientNameResolver(serviceName, var10003, args, var10005, var10006::remove);
   }

   public String getDefaultScheme() {
      return "discovery";
   }

   protected boolean isAvailable() {
      return true;
   }

   protected int priority() {
      return 6;
   }

   @EventListener({HeartbeatEvent.class})
   public void heartbeat(final HeartbeatEvent event) {
      if (this.monitor.update(event.getValue())) {
         for(DiscoveryClientNameResolver discoveryClientNameResolver : this.discoveryClientNameResolvers) {
            discoveryClientNameResolver.refreshFromExternal();
         }
      }

   }

   @PreDestroy
   public void destroy() {
      this.discoveryClientNameResolvers.clear();
   }

   public String toString() {
      String var10000 = this.getDefaultScheme();
      return "DiscoveryClientResolverFactory [scheme=" + var10000 + ", discoveryClient=" + String.valueOf(this.client) + "]";
   }
}
