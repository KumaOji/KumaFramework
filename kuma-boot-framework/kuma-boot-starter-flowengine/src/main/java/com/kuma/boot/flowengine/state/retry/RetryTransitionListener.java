package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.core.utils.bean.BeanUtils;
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
   @Autowired(required = false)
   private DBPlugin dbPlugin;

   @Listen(eventExpression = RetryNode.RETRY_FROM_TARGET_EVENT)
   public void listenRetryFromTarget(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         throwErrorIfPossible(execution, RetryNode.RETRY_FROM_TARGET_EVENT);

         FlowTrace flowTrace = execution.getFlowTrace();

         if (null == flowTrace) {
            RetryNode retryNode = execution.getCurrentNodeExecution().currentNode()
                    .getRetryNode();
            flowTrace = new FlowTrace(UUID.randomUUID().toString());
            BeanUtils.copy(retryNode, flowTrace);
            flowTrace.setTraceId(UUID.randomUUID().toString());
            flowTrace.setOrderId(execution.orderId());
            if (null != dbPlugin) {
               flowTrace.setStartTime(dbPlugin.currentTimestamp());
            }
            else {
               logger.warn("没有DbPlugin实现,使用应用时间(可能会导致时间不一致)");
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

            if (needPersist(flowTrace)) {
               final FlowTrace finalFlowTrace = flowTrace;
               txWrapper.doInNewTransaction(()-> flowTraceRepository.store(finalFlowTrace));

               if (!flowTraceRepository.lock(flowTrace)) {
                  throw new FlowException(String.format("锁定trace异常.%s", flowTrace));
               }
            }

            flowTrace.setFlag(-1);
            logger.warn(
                    "[流转监听一目标到重试,RETRY_FROM_TARGET_EVENT] traceId:{},orderId:{},transition:{}->{}",
                    flowTrace.getTraceId(), flowTrace.getOrderId(),
                    flowTrace.getRetryMeta().getTarget(), flowTrace.getRetryMeta().getNodeName(),
                    execution.getError());
         }
      }
   }


   @Listen(eventExpression = RetryNode.RETRY_TO_TARGET_EVENT)
   public void listenRetryToTarget(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         RetryNode retryNode = (RetryNode) execution.getCurrentNodeExecution().currentNode();
         logger.warn(
                 "[流转监听一重试到目标,RETRY_TO_TARGET_EVENT] traceId:{} ,orderId:{},transition:{}->{}",
                 execution.getFlowTrace().getTraceId(),
                 execution.getFlowTrace().getOrderId(), retryNode.getTarget(), retryNode.getName());

         if (needPersist(retryNode.getRetryFailType())) {
            flowTraceRepository.restore(execution.getFlowTrace());
         }
      }
   }

   @Listen(eventExpression = RetryNode.RETRY_EXIT_EVENT)
   public void listenRetryExit(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         FlowTrace currentTrace = execution.getFlowTrace();

         logger.debug(
                 "[流转监听一重试退出,RETRY_EXIT_EVENT] traceId:{},orderId:{},transition:{}->{}",
                 currentTrace.getTraceId(), currentTrace.getOrderId(),
                 execution.getCurrentNodeExecution().currentNode().getName(), "?");

         if (needPersist(currentTrace)) {
            FlowHistoryTrace historyTrace = currentTrace.end(
                    String.format("重试过出[RETRY_EXIT_EVENT] .max:%s,times:%s",
                            currentTrace.getRetryMeta().getRetryMax(),
                            currentTrace.getRetryTimes()));
            flowTraceRepository.remove(currentTrace);
            flowHistoryTraceRepository.store(historyTrace);
         }
         execution.setFlowTrace(null);
      }
   }


   @Listen(eventExpression = RetryNode.RETRY_MAX_LIMIT_EVENT)
   public void listenRetryMaxLimitTo(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {

         FlowTrace currentTrace = execution.getFlowTrace();
         logger.info(
                 "[流转监听一重试到超限节点,RETRY_MAX_LIMIT_EVENT].traceId:{},orderId:{},transition:{}->{}",
                 currentTrace.getTraceId(), currentTrace.getOrderId(), execution.
                         getCurrentNodeExecution().currentNode().getName(),
                 currentTrace.getRetryMeta().getRetryMaxLimitNode());

         if (needPersist(currentTrace)) {
            FlowHistoryTrace historyTrace = currentTrace.end(String.
                    format("重试主超限节点[RETRY MAX LIMIT EVENT] max:%s,tines:%s,limitNode:%s",
                            currentTrace.getRetryMeta().getRetryMax(), currentTrace.getRetryTimes(),
                            currentTrace.getRetryMeta().getRetryMaxLimitNode()));

            flowTraceRepository.remove(currentTrace);
            flowHistoryTraceRepository.store(historyTrace);
         }
         execution.setFlowTrace(null);
      }

   }


   @Listen(eventExpression = RetryNode.RETRY_END_EVENT)
   public void listenRetryEnd(Execution execution, String eventName) {
      if (execution.isRetryEnable()) {
         FlowTrace currentTrace = execution.getFlowTrace();
         logger.warn(
                 "[流转监听一重试结束,RETRY END EVENT].traceId:{},orderId:{},transition: {}->{}",
                 currentTrace.getTraceId(), currentTrace.getOrderId(),
                 currentTrace.getRetryTimes(), execution.getCurrentFlow().getEndNode().getName());

         if (needPersist(currentTrace)) {
            FlowHistoryTrace historyTrace = currentTrace.end(
                    String.format("重试结束IRETRY_END_EVENT] max:%s ,times:%s", currentTrace.
                            getRetryMeta().getRetryMax(), currentTrace.getRetryTimes()));
            if (currentTrace.limitMax()) {
               flowTraceRepository.remove(currentTrace);
               flowHistoryTraceRepository.store(historyTrace);
            }
         }
         execution.setFlowTrace(null);
      }
   }

   private void throwErrorIfPossible(Execution execution, String event) {
      if (null != execution.getError()) {
         if (execution.getError() instanceof FlowEngineNestException) {
            logger.warn("重试满因.flow:{},version:{},node:{},cause:{}",
                    execution.getCurrentFlow().getName(), execution.getCurrentFlow().getVersion(),
                    execution.getCurrentNodeExecution().currentNode().getName(),
                    execution.getError().getMessage());
         }
         else {
            logger.error("重试透因,flow:{} version:{} node:{}",
                    execution.getCurrentFlow().getName(), execution.getCurrentFlow().getVersion(),
                    execution.getCurrentNodeExecution().currentNode().getName(),
                    execution.getError());
         }
      }
   }

   private boolean needPersist(FlowTrace flowTrace) {
      return needPersist(flowTrace.getRetryMeta().getRetryFailType());
   }

   private boolean needPersist(RetryFailTypeEnum type) {
      return RetryFailTypeEnum.FAIL_FAST != type;
   }


}
