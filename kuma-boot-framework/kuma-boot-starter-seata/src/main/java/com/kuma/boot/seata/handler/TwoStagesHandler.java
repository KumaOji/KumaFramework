package com.kuma.boot.seata.handler;

import java.util.Map;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.BusinessActionContextParameter;
import org.apache.seata.rm.tcc.api.LocalTCC;
import org.apache.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface TwoStagesHandler {
   @TwoPhaseBusinessAction(
      name = "prepare",
      commitMethod = "commit",
      rollbackMethod = "cancel",
      useTCCFence = true
   )
   Object prepare(@BusinessActionContextParameter(paramName = "params") Map params);

   boolean commit(BusinessActionContext context);

   boolean cancel(BusinessActionContext context);
}
