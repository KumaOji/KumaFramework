package com.kuma.boot.flowengine.state.retry;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.data.datasource.tx.TxWrapper;
import com.kuma.boot.flowengine.FlowContext;
import com.kuma.boot.flowengine.constant.Constants;
import com.kuma.boot.flowengine.delegate.DBPlugin;
import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.state.FlowTrace;
import com.kuma.boot.flowengine.state.FlowTraceRepository;
import com.xxl.tool.response.Response;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import cn.hutool.core.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RetryFlowProvider {

   private static final Logger logger = LoggerFactory.getLogger(RetryFlowProvider.class);

   private static final int DEFAULT_BATCH = 50;
   private FlowTraceRepository flowTraceRepository;
   private FlowContext flowContext;
   @Autowired(required = false)
   private DBPlugin dbPlugin;
   private ThreadPoolTaskExecutor commonTaskExecutor;
   @Resource
   private TxWrapper txWrapper;
   @Autowired(required = false)
   private FollowTraceCustomizer customizer;

   public RetryFlowProvider(FlowTraceRepository flowTraceRepository, FlowContext flowcontext,
                            ThreadPoolTaskExecutor commonTaskExecutor) {
      this.flowTraceRepository = flowTraceRepository;
      this.flowContext = flowcontext;
      this.commonTaskExecutor = commonTaskExecutor;
   }

   private JSONObject buildParamMap(String param) {
      if (StringUtils.hasLength(param)) {
         try {
            return JSON.parseObject(param);
         } catch (Exception e) {
            throw new FlowException("flowengine 重试任务无法解析json参数:" + param, e);
         }
      }
      throw new FlowException("xxl-job flowengine 重试任务参数不能为空");
   }


   @XxlJob("flowEngineRetryJobHandler")
   public Response<String> retryFlow(String param) {
      logger.info("收到流程定时重试请求:{}", param);
      JSONObject params = buildParamMap(param);

      commonTaskExecutor.execute(() -> {
         String failType = params.getString("failType");

         Assert.isTrue(null != failType && ("failRetreat".equals(failType) || "failBomb".equals(failType)));

         String orderBy = params.getString("orderBy");
         Assert.isTrue(null != orderBy && ("startTime".equals(orderBy) || "updateTime".equals(orderBy)));
         orderBy = orderBy.replaceAll("Time", "_time");

         String sort = params.getString("sort");
         String retryNodes = params.getString("retryNodes");
         String batch = params.getString("batch");

         int batchSize = null == batch ? DEFAULT_BATCH :
                 Integer.parseInt(batch);
         sort = null == sort ? "asc" : sort.toUpperCase();

         retry(RetryFailTypeEnum.getByCode(failType), retryNodes, orderBy.toUpperCase(), sort.
                         toUpperCase(),
                 batchSize);
      });
      return Response.ofSuccess();
   }


   public void retry(RetryFailTypeEnum failType, String retryNodes, String orderBy, String sort,
                     int batch) {
      try {
         List<FlowTrace> retryTraces;
         if (customizer != null) {
            retryTraces = customizer.list(failType, retryNodes, orderBy, sort, batch);
         } else {
            if (null == retryNodes) {
               retryTraces = flowTraceRepository.listFlowTracesWithLock(failType, null,
                       orderBy, sort, batch);
            } else {
               String[] nodesArray = retryNodes.split("");
               retryTraces = flowTraceRepository.listFlowTracesWithLock(failType,
                       Arrays.asList(nodesArray),
                       orderBy, sort, batch);
            }
         }

         for (FlowTrace flowTrace : retryTraces) {
            try {
               txWrapper.doInNewTransaction(() -> retrySingleTrace(flowTrace));
            } catch (Exception e) {
               logger.error("[调度重试流程轨迹执行异常].flowTrace:{}", flowTrace, e);

            }
         }
      } catch (Exception e) {
         // 保证无论如何,执行次数这些统计信息都会被更新
         logger.error("重试批次异常。", e);
      }
   }


   public void retrySingleTrace(FlowTrace flowTrace) {
      try {
         Object target = flowTrace.getRetryMeta().getExecutionTarget();
         Map<String, Object> args = flowTrace.getRetryMeta().getAttachment();
         Object fid = args.get(FlowContext.FID);
         if (fid instanceof String) {
            MDC.put(Constants.OID_KEY, fid.toString());
         }

         logger.info("[调度重试流程轨迹开始执行].flowTrace:{}", flowTrace);

         flowTrace.setFromSchedule(true);
         if (null != dbPlugin) {
            flowTrace.setEventTime(dbPlugin.currentTimestamp());
         } else {
            logger.warn("没有DbPlugin实现,使用应用时间(可能会导致时间不一致)");
            flowTrace.setEventTime(new Date());
         }

         args.put(FlowContext.FLOW_TRACE, flowTrace);
         args.put(FlowContext.ORDER_ID, flowTrace.getOrderId());

         flowContext.execute(flowTrace.getFlowName(), flowTrace.getNode(),
                 flowTrace.getVersion(), target, args);
         Consumer<Object> callback = RetryContext.FLOW_TERMINAL_CALLBACKS.
                 get(flowTrace.getFlowName());
         if (callback != null) {
            callback.accept(target);
         }
      } finally {
         MDC.remove(Constants.OID_KEY);
      }
   }


}
