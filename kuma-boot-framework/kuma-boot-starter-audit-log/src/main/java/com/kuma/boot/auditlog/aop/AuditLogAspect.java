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

package com.kuma.boot.auditlog.aop;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.auditlog.annotation.AuditLog;
import com.kuma.boot.auditlog.autoconfigure.properties.AuditLogProperties;
import com.kuma.boot.auditlog.core.AuditLogStore;
import com.kuma.boot.auditlog.core.AuditOperatorProvider;
import com.kuma.boot.auditlog.model.AuditLogEntry;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.Executor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * 操作审计切面.
 *
 * <p>环绕 {@link AuditLog} 标注的方法，采集操作人、入参 / 出参、IP、耗时、成功与否，
 * 经 SpEL 求值描述与业务主键后交由 {@link AuditLogStore} 持久化。
 * 审计记录的采集 / 落库失败不影响业务方法的正常执行。
 *
 * @author kuma
 */
@Aspect
public class AuditLogAspect {

    private final AuditLogStore store;
    private final AuditOperatorProvider operatorProvider;
    private final AuditLogProperties properties;
    /** 异步执行器，为 null 时同步落库. */
    private final Executor executor;

    private final SpelExpressionParser spelParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public AuditLogAspect(AuditLogStore store, AuditOperatorProvider operatorProvider,
            AuditLogProperties properties, Executor executor) {
        this.store = store;
        this.operatorProvider = operatorProvider;
        this.properties = properties;
        this.executor = executor;
    }

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint point, AuditLog auditLog) throws Throwable {
        long start = System.currentTimeMillis();
        Throwable error = null;
        Object result = null;
        try {
            result = point.proceed();
            return result;
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            try {
                AuditLogEntry entry = buildEntry(point, auditLog, result, error, start);
                dispatch(entry);
            } catch (Exception e) {
                // 审计采集失败绝不影响业务
                LogUtils.error("审计日志采集失败", e);
            }
        }
    }

    private AuditLogEntry buildEntry(ProceedingJoinPoint point, AuditLog auditLog,
            Object result, Throwable error, long start) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Object[] args = point.getArgs();

        AuditLogEntry entry = new AuditLogEntry();
        entry.setType(auditLog.type());
        entry.setModule(auditLog.module());
        entry.setDescription(evalSpel(auditLog.description(), signature, args));
        entry.setBizId(evalSpel(auditLog.bizId(), signature, args));
        entry.setMethod(signature.getDeclaringTypeName() + "#" + signature.getName());
        entry.setOperatorId(operatorProvider.currentOperatorId());
        entry.setOperatorName(operatorProvider.currentOperatorName());
        entry.setSuccess(error == null);
        if (error != null) {
            entry.setErrorMsg(truncate(error.getMessage()));
        }
        if (auditLog.recordParams()) {
            entry.setParams(truncate(toJson(filterArgs(args))));
        }
        if (auditLog.recordResult() && error == null) {
            entry.setResult(truncate(toJson(result)));
        }
        entry.setOperateTime(LocalDateTime.now());
        entry.setCostMillis(System.currentTimeMillis() - start);
        fillRequestInfo(entry);
        return entry;
    }

    private void dispatch(AuditLogEntry entry) {
        if (executor != null) {
            executor.execute(() -> {
                try {
                    store.save(entry);
                } catch (Exception e) {
                    LogUtils.error("审计日志异步落库失败", e);
                }
            });
        } else {
            store.save(entry);
        }
    }

    private void fillRequestInfo(AuditLogEntry entry) {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return;
        }
        entry.setClientIp(clientIp(request));
        entry.setUserAgent(request.getHeader("User-Agent"));
        entry.setRequestUri(request.getRequestURI());
        entry.setHttpMethod(request.getMethod());
    }

    private String evalSpel(String expr, MethodSignature signature, Object[] args) {
        if (expr == null || expr.isBlank()) {
            return null;
        }
        // 不含 SpEL 占位符，按纯文本返回
        if (!expr.contains("#") && !expr.contains("'")) {
            return expr;
        }
        try {
            String[] paramNames = nameDiscoverer.getParameterNames(signature.getMethod());
            EvaluationContext context = new StandardEvaluationContext();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length && i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }
            Object value = spelParser.parseExpression(expr).getValue(context);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            // SpEL 解析失败时退回原始表达式文本，不阻断审计
            return expr;
        }
    }

    /** 过滤掉无法 / 不应序列化的参数（请求、响应、文件流等）. */
    private Object[] filterArgs(Object[] args) {
        if (args == null) {
            return new Object[0];
        }
        return Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest)
                        && !(arg instanceof jakarta.servlet.http.HttpServletResponse)
                        && !(arg instanceof MultipartFile)
                        && !(arg instanceof java.io.InputStream)
                        && !(arg instanceof java.io.OutputStream))
                .toArray();
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            return "<unserializable: " + obj.getClass().getSimpleName() + ">";
        }
    }

    private String truncate(String text) {
        if (text == null) {
            return null;
        }
        int max = properties.getMaxContentLength();
        return text.length() > max ? text.substring(0, max) + "...(truncated)" : text;
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest();
        }
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            int comma = ip.indexOf(',');
            return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }
}
