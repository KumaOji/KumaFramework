
package com.kuma.boot.job.xxl.aspect;

import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.support.mdc.MdcAttr;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
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


/**
 * <b>XxlJob 拦截器</b><br>
 */
@Aspect
@Order(XxlJobMdcInspector.ORDER)
public class XxlJobMdcInspector {

	public static final int SKIP_RESULT_CODE = 202;
	public static final int ORDER = 0;
	public static final String POINTCUT_PATTERN = "execution (public void *.*()) && "
		+ "@annotation(com.xxl.job.core.handler.annotation.XxlJob)";

	public static final String XXLJJOB_POINTCUT_PATTERN = "@annotation(com.xxl.job.core.handler.annotation.XxlJob)";

	private static final AspectHelper.AnnotationHolder<XxlJob> ANNOTATION_HOLDER = new AspectHelper.AnnotationHolder<>() {
	};

	//@Autowired(required = false)
	//private Executor asyncService;
	@Autowired(required = false)
	private XxlJobExceptionHandler exceptionHandler;

	@Autowired
	private Tracer tracer;

	@Pointcut(XXLJJOB_POINTCUT_PATTERN)
	protected void aroundMethod() {
	}

	@Around(value = "aroundMethod()")
	public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable {
		MdcAttr mdcAttr = MdcAttr.fromMdc();

		XxlJob job = ANNOTATION_HOLDER.findAnnotationByMethod(joinPoint);
		Assert.notNull(job, "@XxlJob annotation not found");

		String jobName = job.value();
		long jobId = XxlJobHelper.getJobId();
		String param = XxlJobHelper.getJobParam();

		try {
			mdcAttr.putMdc();

			String traceId = IdGeneratorUtils.getIdStr();
			TraceContextHolder.setTraceId(traceId);
			TraceUtils.setKmcTraceId(traceId);

			try {
				String tracedId = Objects.requireNonNull(tracer.nextSpan()).context().traceId();
				String spanId = Objects.requireNonNull(tracer.nextSpan()).context().spanId();

				TraceUtils.setOtlpTraceId(tracedId);
				TraceUtils.setOtlpSpanId(spanId);

				TraceUtils.setTraceId(tracedId);
				TraceUtils.setSpanId(spanId);
			} catch (Exception e) {
				TraceUtils.setOtlpTraceId(IdGeneratorUtils.getIdStr());
				TraceUtils.setOtlpSpanId(IdGeneratorUtils.getIdStr());

				TraceUtils.setTraceId(IdGeneratorUtils.getIdStr());
				TraceUtils.setSpanId(IdGeneratorUtils.getIdStr());
			}

			StopWatch sw = null;

			try {
				LogUtils.info(">>>>>>>>>> start xxl-job -> (name: {}, id: {}, param: '{}')",
					jobName, jobId, param);

				sw = new StopWatch(jobName);
				sw.start();

				return joinPoint.proceed();
			} catch (Exception e) {
				Exception cause = null;

				if (e instanceof InvocationTargetException) {
					cause = (Exception) e.getCause();
				}
				if (cause == null) {
					cause = e;
				}

				LogUtils.error("execute xxl-job exception -> ({}) : {}", jobName, cause.getMessage(),
					cause);

				throw cause;
			} finally {
				if (sw != null) {
					sw.stop();

					LogUtils.info("<<<<<<<<<< end xxl-job -> (name: {}, id: {}, costTime: {})", jobName,
						jobId, sw.getTotalTimeMillis());
				}
			}
		} catch (Exception e) {
			invokeExceptionHandler(jobName, jobId, param, e);

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
	}

	private void invokeExceptionHandler(String jobName, long jobId, String param, Exception e) {
		try {
			if (exceptionHandler == null) {
				return;
			}

			long timestamp = System.currentTimeMillis();

			exceptionHandler.handleException(jobName, jobId, param, timestamp, e);

			//if (asyncService == null) {
			//	exceptionHandler.handleException(jobName, jobId, param, timestamp, e);
			//}
			//else {
			//	asyncService.execute(
			//		() -> exceptionHandler.handleException(jobName, jobId, param, timestamp, e));
			//}
		} catch (Exception ex) {
			LogUtils.error("execute xxl-job Invoke-Exception-Handler exception -> ({}) : {}", jobName,
				ex.getMessage(), ex);
		}
	}


}
