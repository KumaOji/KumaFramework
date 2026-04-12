package com.kuma.boot.ddd.gateway.model;

public enum GatewayResponseStatus {
   S,
   F,
   R,
   P,
   CM,
   N,
   L;

   public static boolean isSuccess(GatewayResponse gatewayResponse) {
      return S == gatewayResponse.getStatus();
   }

   public static boolean isFinish(GatewayResponse gatewayResponse) {
      return S == gatewayResponse.getStatus() || F == gatewayResponse.getStatus();
   }

   // $FF: synthetic method
   private static GatewayResponseStatus[] $values() {
      return new GatewayResponseStatus[]{S, F, R, P, CM, N, L};
   }
}
