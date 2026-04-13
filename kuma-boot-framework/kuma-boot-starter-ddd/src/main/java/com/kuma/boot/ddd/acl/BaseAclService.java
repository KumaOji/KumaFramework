//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.acl;

import com.kuma.boot.ddd.gateway.exception.GatewayException;
import com.kuma.boot.ddd.gateway.model.GatewayRecord;
import com.kuma.boot.ddd.gateway.model.GatewayRequest;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;
import com.kuma.boot.ddd.gateway.model.GatewayResponseStatus;

public class BaseAclService {
   public BaseAclService() {
   }

   protected <T> T getResult(GatewayResponse<T> response) {
      if (GatewayResponseStatus.isSuccess(response)) {
         return (T)response.getResult();
      } else {
         throw new GatewayException(response.getFailMsg());
      }
   }

   protected <T> GatewayRequest<T> makeRequest(T param) {
      GatewayRequest<T> gatewayRequest = new GatewayRequest();
      GatewayRecord gatewayRecord = new GatewayRecord();
      gatewayRequest.setParam(param);
      gatewayRequest.setGatewayRecord(gatewayRecord);
      return gatewayRequest;
   }
}
