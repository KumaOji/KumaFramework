package com.kuma.boot.skywalking;

public class SkyWalkingClusterTrace implements ClusterTrace {
   private static final ThreadLocal<String> TRACE_ID_STORAGE = new ThreadLocal();

   public SkyWalkingClusterTrace() {
   }

   public void setTraceId(String traceId) {
      TRACE_ID_STORAGE.set(traceId);
   }

   public void removeTraceId() {
      TRACE_ID_STORAGE.remove();
   }

   public String getTraceId() {
      return (String)TRACE_ID_STORAGE.get();
   }
}
