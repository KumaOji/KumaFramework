package com.kuma.boot.grpc.spring.annotation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kuma.boot.common.utils.log.LogUtils;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import io.grpc.SynchronizationContext;
import io.grpc.NameResolver.ConfigOrError;
import io.grpc.NameResolver.ResolutionResult;
import io.grpc.internal.SharedResourceHolder;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;

public class DiscoveryClientNameResolver extends NameResolver {
   /** @deprecated */
   @Deprecated
   private static final String LEGACY_CLOUD_DISCOVERY_METADATA_PORT = "gRPC.port";
   private static final List<ServiceInstance> KEEP_PREVIOUS = null;
   private static final Gson GSON = new Gson();
   private final String name;
   private final DiscoveryClient client;
   private final SynchronizationContext syncContext;
   private final Consumer<DiscoveryClientNameResolver> shutdownHook;
   private final SharedResourceHolder.Resource<Executor> executorResource;
   private final boolean usingExecutorResource;
   private final NameResolver.ServiceConfigParser serviceConfigParser;
   private NameResolver.Listener2 listener;
   private Executor executor;
   private boolean resolving;
   private List<ServiceInstance> instanceList = Lists.newArrayList();

   public DiscoveryClientNameResolver(final String name, final DiscoveryClient client, final NameResolver.Args args, final SharedResourceHolder.Resource executorResource, final Consumer shutdownHook) {
      this.name = name;
      this.client = client;
      this.syncContext = (SynchronizationContext)Objects.requireNonNull(args.getSynchronizationContext(), "syncContext");
      this.shutdownHook = shutdownHook;
      this.executor = args.getOffloadExecutor();
      this.usingExecutorResource = this.executor == null;
      this.executorResource = executorResource;
      this.serviceConfigParser = args.getServiceConfigParser();
   }

   protected final String getName() {
      return this.name;
   }

   protected final boolean isActive() {
      return this.listener != null;
   }

   public final String getServiceAuthority() {
      return this.name;
   }

   public void start(final NameResolver.Listener2 listener) {
      Preconditions.checkState(!this.isActive(), "already started");
      if (this.usingExecutorResource) {
         this.executor = SharedResourceHolder.get(this.executorResource);
      }

      this.listener = (NameResolver.Listener2)Preconditions.checkNotNull(listener, "listener");
      this.resolve();
   }

   public void refresh() {
      Preconditions.checkState(this.isActive(), "not started");
      this.resolve();
   }

   public void refreshFromExternal() {
      this.syncContext.execute(() -> {
         if (this.isActive()) {
            this.resolve();
         }

      });
   }

   protected List<ServiceInstance> discoverServers() {
      return this.client.getInstances(this.name);
   }

   protected int getGrpcPort(final ServiceInstance instance) {
      Map<String, String> metadata = instance.getMetadata();
      if (metadata != null && !metadata.isEmpty()) {
         String portString = (String)metadata.get("gRPC_port");
         if (portString == null) {
            portString = (String)metadata.get("gRPC.port");
            if (portString == null) {
               return instance.getPort();
            }

            LogUtils.warn("Found legacy grpc port metadata '{}' for client '{}' use '{}' instead", new Object[]{"gRPC.port", this.getName(), "gRPC_port"});
         }

         try {
            return Integer.parseInt(portString);
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse gRPC port information from: " + String.valueOf(instance), e);
         }
      } else {
         return instance.getPort();
      }
   }

   private NameResolver.ConfigOrError resolveServiceConfig(List<ServiceInstance> instances) {
      String serviceConfig = this.getServiceConfig(instances);
      if (serviceConfig == null) {
         return null;
      } else {
         LogUtils.debug("Found service config for {}", new Object[]{this.getName()});
         if (LogUtils.isTraceEnabled()) {
            String logStr = serviceConfig.replace("\r", "\\r").replace("\n", "\\n");
            LogUtils.trace("Service config for {}: {}", new Object[]{this.getName(), logStr});
         }

         try {
            Map<String, ?> parsedServiceConfig = (Map)GSON.fromJson(serviceConfig, Map.class);
            return this.serviceConfigParser.parseServiceConfig(parsedServiceConfig);
         } catch (JsonSyntaxException e) {
            return ConfigOrError.fromError(Status.UNKNOWN.withDescription("Failed to parse grpc service config").withCause(e));
         }
      }
   }

   protected String getServiceConfig(final List<ServiceInstance> instances) {
      for(ServiceInstance inst : instances) {
         Map<String, String> metadata = inst.getMetadata();
         if (metadata != null && !metadata.isEmpty()) {
            String metaValue = (String)metadata.get("gRPC_service_config");
            if (metaValue != null && !metaValue.isEmpty()) {
               return metaValue;
            }
         }
      }

      return null;
   }

   protected Attributes getAttributes(final ServiceInstance serviceInstance) {
      Attributes.Builder builder = Attributes.newBuilder();
      builder.set(DiscoveryClientResolverFactory.DISCOVERY_SERVICE_NAME_KEY, this.name);
      builder.set(DiscoveryClientResolverFactory.DISCOVERY_INSTANCE_ID_KEY, serviceInstance.getInstanceId());
      return builder.build();
   }

   protected boolean needsToUpdateConnections(final List<ServiceInstance> newInstanceList) {
      if (this.instanceList.size() != newInstanceList.size()) {
         return true;
      } else {
         for(ServiceInstance instance : this.instanceList) {
            int port = this.getGrpcPort(instance);
            boolean isSame = false;

            for(ServiceInstance newInstance : newInstanceList) {
               int newPort = this.getGrpcPort(newInstance);
               if (newInstance.getHost().equals(instance.getHost()) && port == newPort) {
                  isSame = true;
                  break;
               }
            }

            if (!isSame) {
               return true;
            }
         }

         return false;
      }
   }

   private void resolve() {
      LogUtils.debug("Scheduled resolve for {}", new Object[]{this.name});
      if (!this.resolving) {
         this.resolving = true;
         this.executor.execute(new Resolve(this.listener));
      }
   }

   public void shutdown() {
      this.listener = null;
      if (this.executor != null && this.usingExecutorResource) {
         this.executor = SharedResourceHolder.release(this.executorResource, this.executor);
      }

      this.instanceList = Lists.newArrayList();
      if (this.shutdownHook != null) {
         this.shutdownHook.accept(this);
      }

   }

   public String toString() {
      String var10000 = this.name;
      return "DiscoveryClientNameResolver [name=" + var10000 + ", discoveryClient=" + String.valueOf(this.client) + "]";
   }

   private final class Resolve implements Runnable {
      private final NameResolver.Listener2 savedListener;

      Resolve(final NameResolver.Listener2 listener) {
         this.savedListener = (NameResolver.Listener2)Objects.requireNonNull(listener, "listener");
      }

      public void run() {
         AtomicReference<List<ServiceInstance>> resultContainer = new AtomicReference<>(DiscoveryClientNameResolver.KEEP_PREVIOUS);

         try {
            resultContainer.set(this.resolveInternal());
         } catch (Exception e) {
            this.savedListener.onError(Status.UNAVAILABLE.withCause(e).withDescription("Failed to update server list for " + DiscoveryClientNameResolver.this.getName()));
            resultContainer.set(Lists.newArrayList());
         } finally {
            DiscoveryClientNameResolver.this.syncContext.execute(() -> {
               DiscoveryClientNameResolver.this.resolving = false;
               List<ServiceInstance> result = (List)resultContainer.get();
               if (result != DiscoveryClientNameResolver.KEEP_PREVIOUS && DiscoveryClientNameResolver.this.isActive()) {
                  DiscoveryClientNameResolver.this.instanceList = result;
               }

            });
         }

      }

      private List<ServiceInstance> resolveInternal() {
         List<ServiceInstance> newInstanceList = DiscoveryClientNameResolver.this.discoverServers();
         if (CollectionUtils.isEmpty(newInstanceList)) {
            LogUtils.error("No servers found for {}", new Object[]{DiscoveryClientNameResolver.this.getName()});
            this.savedListener.onError(Status.UNAVAILABLE.withDescription("No servers found for " + DiscoveryClientNameResolver.this.getName()));
            return Lists.newArrayList();
         } else {
            LogUtils.debug("Got {} candidate servers for {}", new Object[]{newInstanceList.size(), DiscoveryClientNameResolver.this.getName()});
            if (!DiscoveryClientNameResolver.this.needsToUpdateConnections(newInstanceList)) {
               LogUtils.debug("Nothing has changed... skipping update for {}", new Object[]{DiscoveryClientNameResolver.this.getName()});
               return DiscoveryClientNameResolver.KEEP_PREVIOUS;
            } else {
               LogUtils.debug("Ready to update server list for {}", new Object[]{DiscoveryClientNameResolver.this.getName()});
               this.savedListener.onResult(ResolutionResult.newBuilder().setAddresses(this.toTargets(newInstanceList)).setServiceConfig(DiscoveryClientNameResolver.this.resolveServiceConfig(newInstanceList)).build());
               LogUtils.info("Done updating server list for {}", new Object[]{DiscoveryClientNameResolver.this.getName()});
               return newInstanceList;
            }
         }
      }

      private List<EquivalentAddressGroup> toTargets(final List<ServiceInstance> newInstanceList) {
         List<EquivalentAddressGroup> targets = Lists.newArrayList();

         for(ServiceInstance instance : newInstanceList) {
            targets.add(this.toTarget(instance));
         }

         return targets;
      }

      private EquivalentAddressGroup toTarget(final ServiceInstance instance) {
         String host = instance.getHost();
         int port = DiscoveryClientNameResolver.this.getGrpcPort(instance);
         Attributes attributes = DiscoveryClientNameResolver.this.getAttributes(instance);
         LogUtils.debug("Found gRPC server {}:{} for {}", new Object[]{host, port, DiscoveryClientNameResolver.this.getName()});
         return new EquivalentAddressGroup(new InetSocketAddress(host, port), attributes);
      }
   }
}
