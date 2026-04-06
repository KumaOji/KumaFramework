package com.kuma.boot.seata.spi;

import org.apache.seata.core.protocol.RpcMessage;
import org.apache.seata.core.rpc.hook.RpcHook;

public class KmcStatusRpcHook implements RpcHook {
   public void doBeforeRequest(String remoteAddr, RpcMessage request) {
   }

   public void doAfterResponse(String remoteAddr, RpcMessage request, Object response) {
   }
}
