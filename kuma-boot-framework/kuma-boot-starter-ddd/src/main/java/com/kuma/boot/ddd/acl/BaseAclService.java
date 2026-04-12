package com.kuma.boot.ddd.acl;

import com.kuma.boot.ddd.gateway.exception.GatewayException;
import com.kuma.boot.ddd.gateway.model.GatewayRecord;
import com.kuma.boot.ddd.gateway.model.GatewayRequest;
import com.kuma.boot.ddd.gateway.model.GatewayResponse;
import com.kuma.boot.ddd.gateway.model.GatewayResponseStatus;

public class BaseAclService {
   protected Object getResult(GatewayResponse response) {
      if (GatewayResponseStatus.isSuccess(response)) {
         return response.getResult();
      } else {
         throw new GatewayException(response.getFailMsg());
      }
   }

   protected GatewayRequest makeRequest(Object param) {
      GatewayRequest<T> gatewayRequest = new GatewayRequest();
      GatewayRecord gatewayRecord = new GatewayRecord();
      gatewayRequest.setParam(param);
      gatewayRequest.setGatewayRecord(gatewayRecord);
      return gatewayRequest;
   }
}
