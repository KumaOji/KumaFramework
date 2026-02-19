/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.convert.Convert
 *  cn.hutool.core.convert.ConvertException
 *  cn.hutool.core.util.ArrayUtil
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.core.util.URLUtil
 *  cn.hutool.http.useragent.UserAgent
 *  cn.hutool.http.useragent.UserAgentUtil
 *  com.alibaba.ttl.TransmittableThreadLocal
 *  com.kuma.boot.common.enums.LogOperateTypeEnum
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.holder.TenantContextHolder
 *  com.kuma.boot.common.model.result.Result
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.date.DateUtils
 *  com.kuma.boot.common.utils.ip.IpUtils
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.MdcUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  com.kuma.boot.ip2region.model.Ip2regionSearcher
 *  com.kuma.boot.security.spring.utils.SecurityUtils
 *  io.swagger.v3.oas.annotations.Operation
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.annotation.AfterReturning
 *  org.aspectj.lang.annotation.AfterThrowing
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Before
 *  org.aspectj.lang.annotation.Pointcut
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.jspecify.annotations.NonNull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.web.context.request.RequestAttributes
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.kuma.boot.web.request.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.kuma.boot.common.enums.LogOperateTypeEnum;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.ip.IpUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.MdcUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.ip2region.model.Ip2regionSearcher;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import com.kuma.boot.web.request.annotation.RequestLogger;
import com.kuma.boot.web.request.event.RequestLoggerEvent;
import com.kuma.boot.web.request.model.RequestLog;
import com.kuma.boot.web.request.properties.RequestLoggerProperties;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
public class RequestLoggerAspect {
    @Value(value="${spring.application.name}")
    private String applicationName;
    private static final String DEFAULT_SOURCE = "kuma_cloud_request_log";
    private static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";
    @Autowired
    private RequestLoggerProperties requestLoggerProperties;
    @Autowired
    private ApplicationEventPublisher publisher;
    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    private final TransmittableThreadLocal<RequestLog> REQUEST_LOG_THREAD_LOCAL = new TransmittableThreadLocal();

    @Pointcut(value="@within(com.kuma.boot.web.request.annotation.RequestLogger) || @annotation(com.kuma.boot.web.request.annotation.RequestLogger)")
    public void requestLogAspect() {
    }

    @Before(value="requestLogAspect()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        if (this.requestLoggerProperties.getEnabled().booleanValue()) {
            this.tryCatch(val -> {
                RequestLogger requestLogger = RequestLoggerAspect.getTargetAnnotation(joinPoint);
                if (this.check(joinPoint, requestLogger)) {
                    return;
                }
                RequestLog requestLog = this.buildRequestLog(joinPoint, requestLogger);
                this.REQUEST_LOG_THREAD_LOCAL.set((Object)requestLog);
            });
        }
    }

    @AfterReturning(returning="ret", pointcut="requestLogAspect()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        this.tryCatch(p -> {
            RequestLogger requestLogger = RequestLoggerAspect.getTargetAnnotation(joinPoint);
            if (this.check(joinPoint, requestLogger)) {
                return;
            }
            RequestLog requestLog = this.getRequestLogger();
            if (Objects.nonNull(ret) && ret instanceof Result) {
                try {
                    Result r = (Result)Convert.convert(Result.class, (Object)ret);
                    if (r.getCode() == ResultEnum.SUCCESS.codeDesc()) {
                        requestLog.setOperateType(LogOperateTypeEnum.OPERATE_RECORD.getCode());
                    } else {
                        requestLog.setOperateType(LogOperateTypeEnum.EXCEPTION_RECORD.getCode());
                        requestLog.setExDetail(r.getMessage());
                    }
                }
                catch (ConvertException e) {
                    LogUtils.error((Throwable)e);
                }
            }
            requestLog.setTenantId(TenantContextHolder.getTenant());
            long endTime = System.currentTimeMillis();
            requestLog.setEndTime(endTime);
            requestLog.setConsumingTime(endTime - requestLog.getStartTime());
            if (requestLogger.response()) {
                requestLog.setResult(this.getText(ret == null ? "" : JacksonUtils.toJSONString((Object)ret)));
            }
            this.publisher.publishEvent((ApplicationEvent)new RequestLoggerEvent(requestLog));
            this.REQUEST_LOG_THREAD_LOCAL.remove();
        });
    }

    @AfterThrowing(pointcut="requestLogAspect()", throwing="e")
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        this.tryCatch(p -> {
            RequestLogger requestLogger = RequestLoggerAspect.getTargetAnnotation(joinPoint);
            if (this.check(joinPoint, requestLogger)) {
                return;
            }
            RequestLog requestLog = this.getRequestLogger();
            requestLog.setOperateType(LogOperateTypeEnum.EXCEPTION_RECORD.getCode());
            String stackTrace = LogUtils.getStackTrace((Throwable)e);
            requestLog.setExDetail(stackTrace.replaceAll("\"", "'").replace("\n", ""));
            requestLog.setExDesc(e.getMessage().replaceAll("\"", "'").replace("\n", ""));
            if (!requestLogger.request() && requestLogger.requestByError() && StrUtil.isEmpty((CharSequence)requestLog.getParams())) {
                Object[] args = joinPoint.getArgs();
                HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                String strArgs = this.getArgs(args, request);
                requestLog.setParams(this.getText(strArgs));
            }
            this.publisher.publishEvent((ApplicationEvent)new RequestLoggerEvent(requestLog));
            this.REQUEST_LOG_THREAD_LOCAL.remove();
        });
    }

    private String getArgs(Object[] args, HttpServletRequest request) {
        String strArgs = "";
        Object[] params = Arrays.stream(args).filter(item -> !(item instanceof ServletRequest) && !(item instanceof ServletResponse)).toArray();
        try {
            if (!request.getContentType().contains(FORM_DATA_CONTENT_TYPE)) {
                strArgs = JacksonUtils.toJSONString((Object)params);
            }
        }
        catch (Exception e) {
            try {
                strArgs = Arrays.toString(params);
            }
            catch (Exception ex) {
                LogUtils.error((String)"\u89e3\u6790\u53c2\u6570\u5f02\u5e38", (Object[])new Object[]{ex});
            }
        }
        return strArgs;
    }

    private @NonNull RequestLog buildRequestLog(JoinPoint joinPoint, RequestLogger requestLogger) {
        RequestLog requestLog = new RequestLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        RequestContextHolder.setRequestAttributes((RequestAttributes)attributes, (boolean)true);
        HttpServletRequest request = attributes.getRequest();
        requestLog.setTraceId(MdcUtils.get((String)"kmc-trace-id"));
        requestLog.setApplicationName(this.applicationName);
        requestLog.setUsername(SecurityUtils.getUsernameWithAnonymous());
        requestLog.setUserId(String.valueOf(SecurityUtils.getUserIdWithAnonymous()));
        requestLog.setClientId(SecurityUtils.getClientId());
        String ip = RequestUtils.getRemoteAddr((HttpServletRequest)request);
        requestLog.setIp(ip);
        requestLog.setStartTime(System.currentTimeMillis());
        requestLog.setUrl(URLUtil.getPath((String)request.getRequestURI()));
        requestLog.setMethod(request.getMethod());
        Object[] args = joinPoint.getArgs();
        ArrayList<String> argsList = new ArrayList<String>();
        if (ArrayUtil.isNotEmpty((Object[])args)) {
            for (Object arg : args) {
                try {
                    argsList.add(JacksonUtils.toJSONString((Object)arg));
                }
                catch (Exception e) {
                    LogUtils.error((String)"\u8bf7\u6c42\u53c2\u6570\u8f6c\u6362\u5931\u8d25", (Object[])new Object[0]);
                }
            }
        }
        requestLog.setArgs(((Object)argsList).toString().replaceAll("\"", "'").replace("\n", ""));
        requestLog.setBrowser(request.getHeader("user-agent").replaceAll("\"", "'").replace("\n", ""));
        requestLog.setClasspath(joinPoint.getTarget().getClass().getName().replaceAll("\"", "'").replace("\n", ""));
        String name = joinPoint.getSignature().getName();
        requestLog.setMethodName(name);
        requestLog.setParams(JacksonUtils.toJSONString((Object)RequestUtils.getAllRequestParam((HttpServletRequest)request)).replaceAll("\"", "'").replace("\n", ""));
        requestLog.setHeaders(JacksonUtils.toJSONString((Object)RequestUtils.getAllRequestHeaders((HttpServletRequest)request)));
        requestLog.setRequestType(LogUtils.getRequestType((String)name));
        requestLog.setSource(DEFAULT_SOURCE);
        requestLog.setCtime(DateUtils.format((LocalDateTime)LocalDateTime.now(), (String)"yyyy-MM-dd HH:mm:ss"));
        requestLog.setLogday(DateUtils.getCurrentDate());
        Ip2regionSearcher ip2regionSearcher = (Ip2regionSearcher)ContextUtils.getBean(Ip2regionSearcher.class, (boolean)true);
        if (ip2regionSearcher != null) {
            requestLog.setLocation(ip2regionSearcher.getAddressAndIsp(ip));
        } else {
            requestLog.setLocation(IpUtils.getCityInfo((String)ip));
        }
        String uaStr = request.getHeader("user-agent");
        UserAgent userAgent = UserAgentUtil.parse((String)uaStr);
        requestLog.setOs(JacksonUtils.toJSONString((Object)userAgent));
        this.setDescription(joinPoint, requestLogger, requestLog);
        return requestLog;
    }

    private boolean check(JoinPoint joinPoint, RequestLogger requestLogger) {
        if (requestLogger == null || !requestLogger.enabled()) {
            return true;
        }
        RequestLogger targetClass = joinPoint.getTarget().getClass().getAnnotation(RequestLogger.class);
        return targetClass != null && !targetClass.enabled();
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        }
        catch (Exception e) {
            LogUtils.error((String)"\u8bb0\u5f55\u64cd\u4f5c\u65e5\u5fd7\u5f02\u5e38", (Object[])new Object[]{e});
            this.REQUEST_LOG_THREAD_LOCAL.remove();
        }
    }

    public static RequestLogger getTargetAnnotation(JoinPoint point) {
        try {
            Method method;
            RequestLogger annotation = null;
            if (point.getSignature() instanceof MethodSignature && (method = ((MethodSignature)point.getSignature()).getMethod()) != null) {
                annotation = method.getAnnotation(RequestLogger.class);
            }
            if (annotation == null) {
                annotation = point.getTarget().getClass().getAnnotation(RequestLogger.class);
            }
            return annotation;
        }
        catch (Exception e) {
            LogUtils.error((String)"\u83b7\u53d6 {}.{} \u7684 @RequestLogger \u6ce8\u89e3\u5931\u8d25", (Object[])new Object[]{e, point.getSignature().getDeclaringTypeName(), point.getSignature().getName()});
            return null;
        }
    }

    private RequestLog getRequestLogger() {
        RequestLog requestLog = (RequestLog)this.REQUEST_LOG_THREAD_LOCAL.get();
        if (requestLog == null) {
            return new RequestLog();
        }
        return requestLog;
    }

    private String getText(String val) {
        return StrUtil.sub((CharSequence)val, (int)0, (int)65535);
    }

    private void setDescription(JoinPoint joinPoint, RequestLogger requestLogger, RequestLog requestLog) {
        String controllerMethodDescription;
        StringBuilder controllerDescription = new StringBuilder();
        Operation operation = (Operation)AnnotationUtils.findAnnotation((Method)((MethodSignature)joinPoint.getSignature()).getMethod(), Operation.class);
        if (operation != null) {
            String description;
            Object[] tags;
            String summary = operation.summary();
            if (StringUtils.isNotBlank((String)summary)) {
                controllerDescription.append("-").append(summary);
            }
            if (ArrayUtil.isNotEmpty((Object[])(tags = operation.tags()))) {
                controllerDescription.append("-").append((String)tags[0]);
            }
            if (StringUtils.isNotBlank((String)(description = operation.description()))) {
                controllerDescription.append("-").append(description);
            }
        }
        if (StrUtil.isNotBlank((CharSequence)(controllerMethodDescription = RequestLoggerAspect.getDescribe(requestLogger))) && StrUtil.contains((CharSequence)controllerMethodDescription, (CharSequence)"#")) {
            Object[] args = joinPoint.getArgs();
            MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
            controllerMethodDescription = this.getValBySpEl(controllerMethodDescription, methodSignature, args);
        }
        if (requestLogger.controllerApiValue() && StringUtils.isNotBlank((CharSequence)controllerDescription)) {
            requestLog.setDescription(String.valueOf(controllerDescription) + "-" + controllerMethodDescription);
        } else {
            requestLog.setDescription(controllerDescription.toString());
        }
    }

    private String getValBySpEl(String spEl, MethodSignature methodSignature, Object[] args) {
        try {
            String[] paramNames = this.nameDiscoverer.getParameterNames(methodSignature.getMethod());
            if (paramNames != null && paramNames.length > 0) {
                Expression expression = SPEL_EXPRESSION_PARSER.parseExpression(spEl);
                StandardEvaluationContext context = new StandardEvaluationContext();
                for (int i = 0; i < args.length; ++i) {
                    context.setVariable(paramNames[i], args[i]);
                    context.setVariable("p" + i, args[i]);
                }
                Object value = expression.getValue((EvaluationContext)context);
                return value == null ? spEl : value.toString();
            }
        }
        catch (Exception e) {
            LogUtils.error((String)"\u89e3\u6790\u64cd\u4f5c\u65e5\u5fd7\u7684el\u8868\u8fbe\u5f0f\u51fa\u9519", (Object[])new Object[]{e});
        }
        return spEl;
    }

    public static String getDescribe(JoinPoint point) {
        RequestLogger annotation = RequestLoggerAspect.getTargetAnnotation(point);
        if (annotation == null) {
            return "";
        }
        return annotation.value();
    }

    public static String getDescribe(RequestLogger annotation) {
        if (annotation == null) {
            return "";
        }
        return annotation.value();
    }
}

