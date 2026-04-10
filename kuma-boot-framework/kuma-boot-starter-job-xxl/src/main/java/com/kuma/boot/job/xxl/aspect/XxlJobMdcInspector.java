package com.kuma.boot.job.xxl.aspect;

import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.support.mdc.MdcAttr;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

@Aspect
@Order(0)
public class XxlJobMdcInspector {
   public static final int SKIP_RESULT_CODE = 202;
   public static final int ORDER = 0;
   public static final String POINTCUT_PATTERN = "execution (public void *.*()) && @annotation(com.xxl.job.core.handler.annotation.XxlJob)";
   public static final String XXLJJOB_POINTCUT_PATTERN = "@annotation(com.xxl.job.core.handler.annotation.XxlJob)";
   private static final AspectHelper.AnnotationHolder<XxlJob> ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<XxlJob>() {
   };
   @Autowired(
      required = false
   )
   private XxlJobExceptionHandler exceptionHandler;
   @Autowired
   private Tracer tracer;

   public XxlJobMdcInspector() {
   }

   @Pointcut("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
   protected void aroundMethod() {
   }

   @Around("aroundMethod()")
   public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable {
      MdcAttr mdcAttr = MdcAttr.fromMdc();
      XxlJob job = ANNOTATION_HOLDER.findAnnotationByMethod(joinPoint);
      Assert.notNull(job, "@XxlJob annotation not found");
      String jobName = job.value();
      long jobId = XxlJobHelper.getJobId();
      String param = XxlJobHelper.getJobParam();

      String spanId;
      try {
         mdcAttr.putMdc();
         String traceId = IdGeneratorUtils.getIdStr();
         TraceContextHolder.setTraceId(traceId);
         TraceUtils.setKmcTraceId(traceId);

         try {
            String tracedId = ((Span)Objects.requireNonNull(this.tracer.nextSpan())).context().traceId();
            spanId = ((Span)Objects.requireNonNull(this.tracer.nextSpan())).context().spanId();
            TraceUtils.setOtlpTraceId(tracedId);
            TraceUtils.setOtlpSpanId(spanId);
            TraceUtils.setTraceId(tracedId);
            TraceUtils.setSpanId(spanId);
         } catch (Exception var24) {
            TraceUtils.setOtlpTraceId(IdGeneratorUtils.getIdStr());
            TraceUtils.setOtlpSpanId(IdGeneratorUtils.getIdStr());
            TraceUtils.setTraceId(IdGeneratorUtils.getIdStr());
            TraceUtils.setSpanId(IdGeneratorUtils.getIdStr());
         }

         StopWatch sw = null;

         try {
            LogUtils.info(">>>>>>>>>> start xxl-job -> (name: {}, id: {}, param: '{}')", new Object[]{jobName, jobId, param});
            sw = new StopWatch(jobName);
            sw.start();
            spanId = (String)joinPoint.proceed();
         } catch (Exception var25) {
            spanId = var25;
            Exception cause = null;
            if (var25 instanceof InvocationTargetException) {
               cause = (Exception)var25.getCause();
            }

            if (cause == null) {
               cause = var25;
            }

            LogUtils.error("execute xxl-job exception -> ({}) : {}", new Object[]{jobName, cause.getMessage(), cause});
            throw cause;
         } finally {
            if (sw != null) {
               sw.stop();
               LogUtils.info("<<<<<<<<<< end xxl-job -> (name: {}, id: {}, costTime: {})", new Object[]{jobName, jobId, sw.getTotalTimeMillis()});
            }

         }
      } catch (Exception e) {
         this.invokeExceptionHandler(jobName, jobId, param, e);
         throw e;
      } finally {
         TraceContextHolder.clear();
         TraceUtils.removeKmcTraceId();
         TraceUtils.removeOtlpTraceId();
         TraceUtils.removeOtlpSpanId();
         TraceUtils.removeTraceId();
         TraceUtils.removeSpanId();
         mdcAttr.removeMdc();
      }

      return spanId;
   }

   private void invokeExceptionHandler(String jobName, long jobId, String param, Exception e) {
      try {
         if (this.exceptionHandler == null) {
            return;
         }

         long timestamp = System.currentTimeMillis();
         this.exceptionHandler.handleException(jobName, jobId, param, timestamp, e);
      } catch (Exception ex) {
         LogUtils.error("execute xxl-job Invoke-Exception-Handler exception -> ({}) : {}", new Object[]{jobName, ex.getMessage(), ex});
      }

   }
}
