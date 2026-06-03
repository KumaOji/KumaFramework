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

package com.kuma.boot.job.quartz.aspect;

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
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StopWatch;

import java.util.Objects;

/**
 * Quartz Job MDC / Trace 拦截器。
 * <p>拦截所有 {@link org.quartz.Job#execute} 调用，注入 TraceId 并统计耗时。</p>
 */
@Aspect
@Order(QuartzMdcInspector.ORDER)
public class QuartzMdcInspector {

    public static final int ORDER = 0;

    public static final String POINTCUT_PATTERN =
            "execution(* org.quartz.Job+.execute(org.quartz.JobExecutionContext))";

    @Autowired(required = false)
    private QuartzExceptionHandler exceptionHandler;

    @Autowired(required = false)
    private Tracer tracer;

    @Pointcut(POINTCUT_PATTERN)
    protected void aroundMethod() {}

    @Around("aroundMethod()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable {
        MdcAttr mdcAttr = MdcAttr.fromMdc();

        JobExecutionContext context = (JobExecutionContext) joinPoint.getArgs()[0];
        JobKey jobKey = context.getJobDetail().getKey();
        String jobName = jobKey.getName();
        String jobGroup = jobKey.getGroup();

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

            StopWatch sw = new StopWatch(jobName);
            try {
                LogUtils.info(">>>>>>>>>> start quartz -> (group: {}, name: {})", jobGroup, jobName);
                sw.start();
                return joinPoint.proceed();
            } catch (Exception e) {
                LogUtils.error("execute quartz exception -> ({}/{}) : {}", jobGroup, jobName, e.getMessage(), e);
                throw e;
            } finally {
                sw.stop();
                LogUtils.info("<<<<<<<<<< end quartz -> (group: {}, name: {}, costTime: {}ms)",
                        jobGroup, jobName, sw.getTotalTimeMillis());
            }
        } catch (Exception e) {
            invokeExceptionHandler(jobName, jobGroup, e);
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

    private void invokeExceptionHandler(String jobName, String jobGroup, Exception e) {
        try {
            if (exceptionHandler == null) {
                return;
            }
            exceptionHandler.handleException(jobName, jobGroup, System.currentTimeMillis(), e);
        } catch (Exception ex) {
            LogUtils.error("execute quartz invoke-exception-handler exception -> ({}/{}) : {}",
                    jobGroup, jobName, ex.getMessage(), ex);
        }
    }
}
