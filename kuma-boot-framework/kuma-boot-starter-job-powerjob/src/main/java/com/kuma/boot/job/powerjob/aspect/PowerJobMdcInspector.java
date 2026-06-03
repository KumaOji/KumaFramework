/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.job.powerjob.aspect;

import com.kuma.boot.common.holder.TraceContextHolder;
import com.kuma.boot.common.support.mdc.MdcAttr;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import io.micrometer.tracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StopWatch;
import tech.powerjob.worker.core.processor.TaskContext;

import java.util.Objects;

/**
 * PowerJob MDC 拦截器
 *
 * @author kuma
 * @version 2024.1
 * @since 2024-01-01
 */
@Aspect
@Order(PowerJobMdcInspector.ORDER)
public class PowerJobMdcInspector {

    public static final int ORDER = 0;

    public static final String POINTCUT_PATTERN =
            "execution(* tech.powerjob.worker.core.processor.sdk.BasicProcessor+.process(..))";

    @Autowired(required = false)
    private PowerJobExceptionHandler exceptionHandler;

    @Autowired(required = false)
    private Tracer tracer;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}

    @Around(value = "aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable {
        MdcAttr mdcAttr = MdcAttr.fromMdc();

        String processorName = joinPoint.getTarget().getClass().getSimpleName();
        TaskContext context = (TaskContext) joinPoint.getArgs()[0];
        Long jobId = context.getJobId();
        String jobParams = context.getJobParams();

        try {
            mdcAttr.putMdc();

            String traceId = IdGeneratorUtils.getIdStr();
            TraceContextHolder.setTraceId(traceId);
            TraceUtils.setKmcTraceId(traceId);

            try {
                if (tracer != null) {
                    String tracedId = Objects.requireNonNull(tracer.nextSpan()).context().traceId();
                    String spanId = Objects.requireNonNull(tracer.nextSpan()).context().spanId();
                    TraceUtils.setOtlpTraceId(tracedId);
                    TraceUtils.setOtlpSpanId(spanId);
                    TraceUtils.setTraceId(tracedId);
                    TraceUtils.setSpanId(spanId);
                }
            } catch (Exception e) {
                TraceUtils.setOtlpTraceId(IdGeneratorUtils.getIdStr());
                TraceUtils.setOtlpSpanId(IdGeneratorUtils.getIdStr());
                TraceUtils.setTraceId(IdGeneratorUtils.getIdStr());
                TraceUtils.setSpanId(IdGeneratorUtils.getIdStr());
            }

            StopWatch sw = null;
            try {
                LogUtils.info(">>>>>>>>>> start powerjob -> (processor: {}, jobId: {}, params: '{}')",
                        processorName, jobId, jobParams);

                sw = new StopWatch(processorName);
                sw.start();

                return joinPoint.proceed();
            } catch (Exception e) {
                LogUtils.error("execute powerjob exception -> ({}) : {}", processorName, e.getMessage(), e);
                throw e;
            } finally {
                if (sw != null) {
                    sw.stop();
                    LogUtils.info("<<<<<<<<<< end powerjob -> (processor: {}, jobId: {}, costTime: {})",
                            processorName, jobId, sw.getTotalTimeMillis());
                }
            }
        } catch (Exception e) {
            invokeExceptionHandler(processorName, jobId, jobParams, e);
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

    private void invokeExceptionHandler(String processorName, Long jobId, String jobParams, Exception e) {
        try {
            if (exceptionHandler == null) {
                return;
            }
            exceptionHandler.handleException(processorName, jobId, jobParams, System.currentTimeMillis(), e);
        } catch (Exception ex) {
            LogUtils.error("execute powerjob invoke-exception-handler exception -> ({}) : {}",
                    processorName, ex.getMessage(), ex);
        }
    }
}
