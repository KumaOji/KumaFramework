package com.kuma.boot.skywalking.interceptor;

import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.servlet.MdcUtils;
import com.kuma.boot.skywalking.ClusterTrace;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class TraceInterceptor implements HandlerInterceptor {
   private static final Logger LOGGER = LoggerFactory.getLogger(TraceInterceptor.class);
   public static final String TRACE_ID_HEADER = "Trace-Id";
   private final ClusterTrace clusterTrace;
   private static final String ERROR_STR = "Ignored_Trace,N/A";

   public TraceInterceptor(ClusterTrace clusterTrace) {
      this.clusterTrace = clusterTrace;
   }

   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      String traceId = response.getHeader("Trace-Id") != null ? response.getHeader("Trace-Id") : "";
      if (!StringUtils.hasLength(traceId)) {
         traceId = TraceContext.traceId();
      }

      if (!StringUtils.hasLength(traceId) || "Ignored_Trace,N/A".contains(traceId)) {
         traceId = IdGeneratorUtils.getIdStr();
      }

      response.setHeader("Trace-Id", traceId);
      MdcUtils.put("tid", traceId);
      this.clusterTrace.setTraceId(traceId);
      LOGGER.info("Trace-Id:{},PATH:{}", traceId, request.getRequestURI());
      return true;
   }

   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
      MdcUtils.remove("tid");
      this.clusterTrace.removeTraceId();
   }
}
