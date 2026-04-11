package com.kuma.boot.flowengine.simpleflow.core.registry;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.flowengine.simpleflow.api.ServiceRegistry;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultServiceRegistry implements ServiceRegistry {
   private static final DefaultServiceRegistry INSTANCE = new DefaultServiceRegistry();
   private final Map<String, Object> servicesByName = new ConcurrentHashMap();
   private final Map<Class<?>, Set<Object>> servicesByType = new ConcurrentHashMap();
   private final Map<String, Class<?>> nameToTypeMapping = new ConcurrentHashMap();

   private DefaultServiceRegistry() {
   }

   public static DefaultServiceRegistry getInstance() {
      return INSTANCE;
   }

   public <T> void registerService(String name, T service) {
      if (name != null && !name.trim().isEmpty()) {
         if (service == null) {
            throw new IllegalArgumentException("Service instance cannot be null");
         } else {
            Class<?> serviceClass = service.getClass();
            this.servicesByName.put(name, service);
            this.nameToTypeMapping.put(name, serviceClass);
            ((Set)this.servicesByType.computeIfAbsent(serviceClass, (k) -> ConcurrentHashMap.newKeySet())).add(service);
            this.registerServiceInterfaces(service, serviceClass);
            LogUtils.info("Registered service: {} -> {} ({})", new Object[]{name, serviceClass.getSimpleName(), serviceClass.getName()});
         }
      } else {
         throw new IllegalArgumentException("Service name cannot be null or empty");
      }
   }

   public <T> void registerService(T service) {
      if (service == null) {
         throw new IllegalArgumentException("Service instance cannot be null");
      } else {
         Class<?> serviceClass = service.getClass();
         String serviceName = this.generateServiceName(serviceClass);
         this.registerService(serviceName, service);
      }
   }

   public <T> Optional<T> getService(String name) {
      Object service = this.servicesByName.get(name);
      return service != null ? Optional.of(service) : Optional.empty();
   }

   public <T> Optional<T> getService(Class<T> serviceClass) {
      Set<Object> services = (Set)this.servicesByType.get(serviceClass);
      return services != null && !services.isEmpty() ? Optional.of(services.iterator().next()) : Optional.empty();
   }

   public <T> Set<T> getServices(Class<T> serviceClass) {
      Set<Object> services = (Set)this.servicesByType.get(serviceClass);
      return services != null ? (Set)services.stream().map((service) -> service).collect(Collectors.toSet()) : Collections.emptySet();
   }

   public boolean hasService(String name) {
      return this.servicesByName.containsKey(name);
   }

   public boolean hasService(Class<?> serviceClass) {
      Set<Object> services = (Set)this.servicesByType.get(serviceClass);
      return services != null && !services.isEmpty();
   }

   public <T> Optional<T> removeService(String name) {
      Object service = this.servicesByName.remove(name);
      if (service != null) {
         Class<?> serviceClass = (Class)this.nameToTypeMapping.remove(name);
         if (serviceClass != null) {
            Set<Object> services = (Set)this.servicesByType.get(serviceClass);
            if (services != null) {
               services.remove(service);
               if (services.isEmpty()) {
                  this.servicesByType.remove(serviceClass);
               }
            }

            this.removeServiceFromInterfaces(service, serviceClass);
         }

         LogUtils.info("Removed service: {}", new Object[]{name});
         return Optional.of(service);
      } else {
         return Optional.empty();
      }
   }

   public Set<String> getServiceNames() {
      return new HashSet(this.servicesByName.keySet());
   }

   public void clear() {
      this.servicesByName.clear();
      this.servicesByType.clear();
      this.nameToTypeMapping.clear();
      LogUtils.info("Cleared all registered services", new Object[0]);
   }

   public int size() {
      return this.servicesByName.size();
   }

   private void registerServiceInterfaces(Object service, Class<?> serviceClass) {
      for(Class<?> interfaceClass : serviceClass.getInterfaces()) {
         ((Set)this.servicesByType.computeIfAbsent(interfaceClass, (k) -> ConcurrentHashMap.newKeySet())).add(service);
         this.registerServiceInterfaces(service, interfaceClass);
      }

      Class<?> superClass = serviceClass.getSuperclass();
      if (superClass != null && superClass != Object.class) {
         ((Set)this.servicesByType.computeIfAbsent(superClass, (k) -> ConcurrentHashMap.newKeySet())).add(service);
         this.registerServiceInterfaces(service, superClass);
      }

   }

   private void removeServiceFromInterfaces(Object service, Class<?> serviceClass) {
      for(Class<?> interfaceClass : serviceClass.getInterfaces()) {
         Set<Object> services = (Set)this.servicesByType.get(interfaceClass);
         if (services != null) {
            services.remove(service);
            if (services.isEmpty()) {
               this.servicesByType.remove(interfaceClass);
            }
         }

         this.removeServiceFromInterfaces(service, interfaceClass);
      }

      Class<?> superClass = serviceClass.getSuperclass();
      if (superClass != null && superClass != Object.class) {
         Set<Object> services = (Set)this.servicesByType.get(superClass);
         if (services != null) {
            services.remove(service);
            if (services.isEmpty()) {
               this.servicesByType.remove(superClass);
            }
         }

         this.removeServiceFromInterfaces(service, superClass);
      }

   }

   private String generateServiceName(Class<?> serviceClass) {
      String className = serviceClass.getSimpleName();
      char var10000 = Character.toLowerCase(className.charAt(0));
      return var10000 + className.substring(1);
   }
}
