package com.kuma.boot.flowengine.state.retry;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.data.datasource.tx.TxWrapper;
import com.kuma.boot.flowengine.FlowContext;
import com.kuma.boot.flowengine.delegate.DBPlugin;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.state.FlowTrace;
import com.kuma.boot.flowengine.state.FlowTraceRepository;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

public class RetryFlowProvider {
   private static final Logger logger = LoggerFactory.getLogger(RetryFlowProvider.class);
   private static final int DEFAULT_BATCH = 50;
   private FlowTraceRepository flowTraceRepository;
   private FlowContext flowContext;
   @Autowired(
      required = false
   )
   private DBPlugin dbPlugin;
   private ThreadPoolTaskExecutor commonTaskExecutor;
   @Resource
   private TxWrapper txWrapper;
   @Autowired(
      required = false
   )
   private FollowTraceCustomizer customizer;

   public RetryFlowProvider(FlowTraceRepository flowTraceRepository, FlowContext flowcontext, ThreadPoolTaskExecutor commonTaskExecutor) {
      this.flowTraceRepository = flowTraceRepository;
      this.flowContext = flowcontext;
      this.commonTaskExecutor = commonTaskExecutor;
   }

   private JSONObject buildParamMap(String param) {
      if (StringUtils.hasLength(param)) {
         try {
            return JSON.parseObject(param);
         } catch (Exception e) {
            throw new FlowException("flowengine \u91cd\u8bd5\u4efb\u52a1\u65e0\u6cd5\u89e3\u6790json\u53c2\u6570:" + param, e);
         }
      } else {
         throw new FlowException("xxl-job flowengine \u91cd\u8bd5\u4efb\u52a1\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a");
      }
   }

   @XxlJob("flowEngineRetryJobHandler")
   public Response<String> retryFlow(String param) {
      logger.info("\u6536\u5230\u6d41\u7a0b\u5b9a\u65f6\u91cd\u8bd5\u8bf7\u6c42:{}", param);
      JSONObject params = this.buildParamMap(param);
      this.commonTaskExecutor.execute(() -> {
         String failType = params.getString("failType");
         Assert.isTrue(null != failType && ("failRetreat".equals(failType) || "failBomb".equals(failType)));
         String orderBy = params.getString("orderBy");
         Assert.isTrue(null != orderBy && ("startTime".equals(orderBy) || "updateTime".equals(orderBy)));
         orderBy = orderBy.replaceAll("Time", "_time");
         String sort = params.getString("sort");
         String retryNodes = params.getString("retryNodes");
         String batch = params.getString("batch");
         int batchSize = null == batch ? 50 : Integer.parseInt(batch);
         sort = null == sort ? "asc" : sort.toUpperCase();
         this.retry(RetryFailTypeEnum.getByCode(failType), retryNodes, orderBy.toUpperCase(), sort.toUpperCase(), batchSize);
      });
      return Response.ofSuccess();
   }

   public void retry(RetryFailTypeEnum failType, String retryNodes, String orderBy, String sort, int batch) {
      try {
         List<FlowTrace> retryTraces;
         if (this.customizer != null) {
            retryTraces = this.customizer.list(failType, retryNodes, orderBy, sort, batch);
         } else if (null == retryNodes) {
            retryTraces = this.flowTraceRepository.listFlowTracesWithLock(failType, (List)null, orderBy, sort, batch);
         } else {
            String[] nodesArray = retryNodes.split("");
            retryTraces = this.flowTraceRepository.listFlowTracesWithLock(failType, Arrays.asList(nodesArray), orderBy, sort, batch);
         }

         for(FlowTrace flowTrace : retryTraces) {
            try {
               this.txWrapper.doInNewTransaction(() -> this.retrySingleTrace(flowTrace));
            } catch (Exception e) {
               logger.error("[\u8c03\u5ea6\u91cd\u8bd5\u6d41\u7a0b\u8f68\u8ff9\u6267\u884c\u5f02\u5e38].flowTrace:{}", flowTrace, e);
            }
         }
      } catch (Exception e) {
         logger.error("\u91cd\u8bd5\u6279\u6b21\u5f02\u5e38\u3002", e);
      }

   }

   public void retrySingleTrace(FlowTrace flowTrace) {
      try {
         Object target = flowTrace.getRetryMeta().getExecutionTarget();
         Map<String, Object> args = flowTrace.getRetryMeta().getAttachment();
         Object fid = args.get("flowengineOid");
         if (fid instanceof String) {
            MDC.put("oid", fid.toString());
         }

         logger.info("[\u8c03\u5ea6\u91cd\u8bd5\u6d41\u7a0b\u8f68\u8ff9\u5f00\u59cb\u6267\u884c].flowTrace:{}", flowTrace);
         flowTrace.setFromSchedule(true);
         if (null != this.dbPlugin) {
            flowTrace.setEventTime(this.dbPlugin.currentTimestamp());
         } else {
            logger.warn("\u6ca1\u6709DbPlugin\u5b9e\u73b0,\u4f7f\u7528\u5e94\u7528\u65f6\u95f4(\u53ef\u80fd\u4f1a\u5bfc\u81f4\u65f6\u95f4\u4e0d\u4e00\u81f4)");
            flowTrace.setEventTime(new Date());
         }

         args.put("flowTrace", flowTrace);
         args.put("orderId", flowTrace.getOrderId());
         this.flowContext.execute(flowTrace.getFlowName(), flowTrace.getNode(), flowTrace.getVersion(), target, args);
         Consumer<Object> callback = (Consumer)RetryContext.FLOW_TERMINAL_CALLBACKS.get(flowTrace.getFlowName());
         if (callback != null) {
            callback.accept(target);
         }
      } finally {
         MDC.remove("oid");
      }

   }
}
