package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.common.utils.bean.BeanUtils;
import com.kuma.boot.data.datasource.tx.TxWrapper;
import com.kuma.boot.flowengine.annotation.Listen;
import com.kuma.boot.flowengine.delegate.DBPlugin;
import com.kuma.boot.flowengine.engine.Execution;
import com.kuma.boot.flowengine.exception.FlowEngineNestException;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.module.RetryNode;
import com.kuma.boot.flowengine.state.FlowHistoryTrace;
import com.kuma.boot.flowengine.state.FlowHistoryTraceRepository;
import com.kuma.boot.flowengine.state.FlowTrace;
import com.kuma.boot.flowengine.state.FlowTraceRepository;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RetryTransitionListener {
   private static final Logger logger = LoggerFactory.getLogger(RetryTransitionListener.class);
   @Autowired
   private FlowTraceRepository flowTraceRepository;
   @Autowired
   private FlowHistoryTraceRepository flowHistoryTraceRepository;
   @Autowired
   private TxWrapper txWrapper;
   @Autowired(
      required = false
   )
   private DBPlugin dbPlugin;

   public RetryTransitionListener() {
   }

   @Listen(
      eventExpression = "retry_from_target"
   )
   public void listenRetryFromTarget(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         this.throwErrorIfPossible(execution, "retry_from_target");
         FlowTrace flowTrace = execution.getFlowTrace();
         if (null == flowTrace) {
            RetryNode retryNode = execution.getCurrentNodeExecution().currentNode().getRetryNode();
            flowTrace = new FlowTrace(UUID.randomUUID().toString());
            BeanUtils.copy(retryNode, flowTrace);
            flowTrace.setTraceId(UUID.randomUUID().toString());
            flowTrace.setOrderId(execution.orderId());
            if (null != this.dbPlugin) {
               flowTrace.setStartTime(this.dbPlugin.currentTimestamp());
            } else {
               logger.warn("\u6ca1\u6709DbPlugin\u5b9e\u73b0,\u4f7f\u7528\u5e94\u7528\u65f6\u95f4(\u53ef\u80fd\u4f1a\u5bfc\u81f4\u65f6\u95f4\u4e0d\u4e00\u81f4)");
               flowTrace.setStartTime(new Date());
            }

            flowTrace.setEventTime(flowTrace.getStartTime());
            flowTrace.setNextRetryTime(flowTrace.getEventTime());
            flowTrace.setFlowName(execution.getCurrentFlow().getName());
            flowTrace.setVersion(execution.getCurrentFlow().getVersion());
            flowTrace.setNode(retryNode.getName());
            BeanUtils.copy(retryNode, flowTrace.getRetryMeta());
            flowTrace.getRetryMeta().setNodeName(retryNode.getName());
            flowTrace.retreatNextTime();
            flowTrace.getRetryMeta().setFailFastTimeMills(retryNode.getFailFastTimeMills());
            execution.setFlowTrace(flowTrace);
            if (this.needPersist(flowTrace)) {
               this.txWrapper.doInNewTransaction(() -> this.flowTraceRepository.store(flowTrace));
               if (!this.flowTraceRepository.lock(flowTrace)) {
                  throw new FlowException(String.format("\u9501\u5b9atrace\u5f02\u5e38.%s", flowTrace));
               }
            }

            flowTrace.setFlag(-1);
            logger.warn("[\u6d41\u8f6c\u76d1\u542c\u4e00\u76ee\u6807\u5230\u91cd\u8bd5,RETRY_FROM_TARGET_EVENT] traceId:{},orderId:{},transition:{}->{}", new Object[]{flowTrace.getTraceId(), flowTrace.getOrderId(), flowTrace.getRetryMeta().getTarget(), flowTrace.getRetryMeta().getNodeName(), execution.getError()});
         }
      }

   }

   @Listen(
      eventExpression = "retry_to_target"
   )
   public void listenRetryToTarget(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         RetryNode retryNode = (RetryNode)execution.getCurrentNodeExecution().currentNode();
         logger.warn("[\u6d41\u8f6c\u76d1\u542c\u4e00\u91cd\u8bd5\u5230\u76ee\u6807,RETRY_TO_TARGET_EVENT] traceId:{} ,orderId:{},transition:{}->{}", new Object[]{execution.getFlowTrace().getTraceId(), execution.getFlowTrace().getOrderId(), retryNode.getTarget(), retryNode.getName()});
         if (this.needPersist(retryNode.getRetryFailType())) {
            this.flowTraceRepository.restore(execution.getFlowTrace());
         }
      }

   }

   @Listen(
      eventExpression = "retry_exit"
   )
   public void listenRetryExit(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         FlowTrace currentTrace = execution.getFlowTrace();
         logger.debug("[\u6d41\u8f6c\u76d1\u542c\u4e00\u91cd\u8bd5\u9000\u51fa,RETRY_EXIT_EVENT] traceId:{},orderId:{},transition:{}->{}", new Object[]{currentTrace.getTraceId(), currentTrace.getOrderId(), execution.getCurrentNodeExecution().currentNode().getName(), "?"});
         if (this.needPersist(currentTrace)) {
            FlowHistoryTrace historyTrace = currentTrace.end(String.format("\u91cd\u8bd5\u8fc7\u51fa[RETRY_EXIT_EVENT] .max:%s,times:%s", currentTrace.getRetryMeta().getRetryMax(), currentTrace.getRetryTimes()));
            this.flowTraceRepository.remove(currentTrace);
            this.flowHistoryTraceRepository.store(historyTrace);
         }

         execution.setFlowTrace((FlowTrace)null);
      }

   }

   @Listen(
      eventExpression = "retry_max_limit_event"
   )
   public void listenRetryMaxLimitTo(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         FlowTrace currentTrace = execution.getFlowTrace();
         logger.info("[\u6d41\u8f6c\u76d1\u542c\u4e00\u91cd\u8bd5\u5230\u8d85\u9650\u8282\u70b9,RETRY_MAX_LIMIT_EVENT].traceId:{},orderId:{},transition:{}->{}", new Object[]{currentTrace.getTraceId(), currentTrace.getOrderId(), execution.getCurrentNodeExecution().currentNode().getName(), currentTrace.getRetryMeta().getRetryMaxLimitNode()});
         if (this.needPersist(currentTrace)) {
            FlowHistoryTrace historyTrace = currentTrace.end(String.format("\u91cd\u8bd5\u4e3b\u8d85\u9650\u8282\u70b9[RETRY MAX LIMIT EVENT] max:%s,tines:%s,limitNode:%s", currentTrace.getRetryMeta().getRetryMax(), currentTrace.getRetryTimes(), currentTrace.getRetryMeta().getRetryMaxLimitNode()));
            this.flowTraceRepository.remove(currentTrace);
            this.flowHistoryTraceRepository.store(historyTrace);
         }

         execution.setFlowTrace((FlowTrace)null);
      }

   }

   @Listen(
      eventExpression = "retry_end"
   )
   public void listenRetryEnd(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         FlowTrace currentTrace = execution.getFlowTrace();
         logger.warn("[\u6d41\u8f6c\u76d1\u542c\u4e00\u91cd\u8bd5\u7ed3\u675f,RETRY END EVENT].traceId:{},orderId:{},transition: {}->{}", new Object[]{currentTrace.getTraceId(), currentTrace.getOrderId(), currentTrace.getRetryTimes(), execution.getCurrentFlow().getEndNode().getName()});
         if (this.needPersist(currentTrace)) {
            FlowHistoryTrace historyTrace = currentTrace.end(String.format("\u91cd\u8bd5\u7ed3\u675fIRETRY_END_EVENT] max:%s ,times:%s", currentTrace.getRetryMeta().getRetryMax(), currentTrace.getRetryTimes()));
            if (currentTrace.limitMax()) {
               this.flowTraceRepository.remove(currentTrace);
               this.flowHistoryTraceRepository.store(historyTrace);
            }
         }

         execution.setFlowTrace((FlowTrace)null);
      }

   }

   private void throwErrorIfPossible(Execution execution, String event) {
      if (null != execution.getError()) {
         if (execution.getError() instanceof FlowEngineNestException) {
            logger.warn("\u91cd\u8bd5\u6ee1\u56e0.flow:{},version:{},node:{},cause:{}", new Object[]{execution.getCurrentFlow().getName(), execution.getCurrentFlow().getVersion(), execution.getCurrentNodeExecution().currentNode().getName(), execution.getError().getMessage()});
         } else {
            logger.error("\u91cd\u8bd5\u900f\u56e0,flow:{} version:{} node:{}", new Object[]{execution.getCurrentFlow().getName(), execution.getCurrentFlow().getVersion(), execution.getCurrentNodeExecution().currentNode().getName(), execution.getError()});
         }
      }

   }

   private boolean needPersist(FlowTrace flowTrace) {
      return this.needPersist(flowTrace.getRetryMeta().getRetryFailType());
   }

   private boolean needPersist(RetryFailTypeEnum type) {
      return RetryFailTypeEnum.FAIL_FAST != type;
   }
}
