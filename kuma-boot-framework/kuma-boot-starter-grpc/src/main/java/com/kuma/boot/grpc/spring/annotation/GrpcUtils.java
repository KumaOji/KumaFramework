package com.kuma.boot.grpc.spring.annotation;

import io.grpc.MethodDescriptor;

public final class GrpcUtils {
   public static final String DOMAIN_SOCKET_ADDRESS_SCHEME = "unix";
   public static final String DOMAIN_SOCKET_ADDRESS_PREFIX = "unix:";
   public static final String CLOUD_DISCOVERY_METADATA_PORT = "gRPC_port";
   public static final String CLOUD_DISCOVERY_METADATA_SERVICE_CONFIG = "gRPC_service_config";
   public static final int INTER_PROCESS_DISABLE = -1;

   public static String extractDomainSocketAddressPath(final String address) {
      if (!address.startsWith("unix:")) {
         throw new IllegalArgumentException(address + " is not a valid domain socket address.");
      } else {
         String path = address.substring("unix:".length());
         if (path.startsWith("//")) {
            path = path.substring(2);
         }

         return path;
      }
   }

   public static String extractServiceName(final MethodDescriptor method) {
      return MethodDescriptor.extractFullServiceName(method.getFullMethodName());
   }

   public static String extractMethodName(final MethodDescriptor method) {
      String fullMethodName = method.getFullMethodName();
      int index = fullMethodName.lastIndexOf(47);
      return index == -1 ? fullMethodName : fullMethodName.substring(index + 1);
   }

   private GrpcUtils() {
   }
}
