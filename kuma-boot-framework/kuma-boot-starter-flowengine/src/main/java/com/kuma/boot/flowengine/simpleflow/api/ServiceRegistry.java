package com.kuma.boot.flowengine.simpleflow.api;

import java.util.Optional;
import java.util.Set;

public interface ServiceRegistry {
   <T> void registerService(String name, T service);

   <T> void registerService(T service);

   <T> Optional<T> getService(String name);

   <T> Optional<T> getService(Class<T> serviceClass);

   <T> Set<T> getServices(Class<T> serviceClass);

   boolean hasService(String name);

   boolean hasService(Class<?> serviceClass);

   <T> Optional<T> removeService(String name);

   Set<String> getServiceNames();

   void clear();

   int size();
}
