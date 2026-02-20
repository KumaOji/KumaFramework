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

package com.kuma.boot.web.request.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.constant.StrPoolConstants;
import com.kuma.boot.common.enums.LogOperateTypeEnum;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.ip.IpUtils;
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
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static cn.hutool.core.util.URLUtil.getPath;
import static com.kuma.boot.common.model.DatePattern.NORM_DATETIME_PATTERN;

/**
 * 日志切面
 *
 * <p>①切面注解得到请求数据 -> ②发布监听事件 -> ③异步监听日志入库
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/6/3 11:47
 */
@Aspect
public class RequestLoggerAspect {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String DEFAULT_SOURCE = "kuma_cloud_request_log";
    private static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";

    @Autowired private RequestLoggerProperties requestLoggerProperties;

    @Autowired private ApplicationEventPublisher publisher;

    /** 用于SpEL表达式解析. */
    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    /** 用于获取方法参数定义名字. */
    private final DefaultParameterNameDiscoverer nameDiscoverer =
            new DefaultParameterNameDiscoverer();

    /** log实体类 */
    private final TransmittableThreadLocal<RequestLog> REQUEST_LOG_THREAD_LOCAL =
            new TransmittableThreadLocal<>();

    public RequestLoggerAspect() {}

    /***
     * 定义controller切入点拦截规则：拦截标记SysLog注解和指定包下的方法
     * 2个表达式加起来才能拦截所有Controller 或者继承了BaseController的方法
     *
     * execution(public * com.kuma.cloud.*.biz.api.controller.*(..)) 解释：
     *
     * 第一个* 任意返回类型
     * 第二个* com.kuma.cloud.*.biz.controller包下的所有类
     * 第三个* 类下的所有方法
     * ()中间的.. 任意参数
     *
     * &#064;annotation(com.kuma.boot.logger.annotation.RequestLogger)  解释：
     */
    @Pointcut("@within(com.kuma.boot.web.request.annotation.RequestLogger) || "
            + "@annotation(com.kuma.boot.web.request.annotation.RequestLogger)")
    public void requestLogAspect() {}

    @Before(value = "requestLogAspect()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        if (requestLoggerProperties.getEnabled()) {
            tryCatch(
                    val -> {
                        RequestLogger requestLogger = getTargetAnnotation(joinPoint);
                        if (check(joinPoint, requestLogger)) {
                            return;
                        }

                        RequestLog requestLog = buildRequestLog(joinPoint, requestLogger);
                        REQUEST_LOG_THREAD_LOCAL.set(requestLog);
                    });
        }
    }

    @AfterReturning(returning = "ret", pointcut = "requestLogAspect()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        tryCatch(
                p -> {
                    RequestLogger requestLogger = getTargetAnnotation(joinPoint);
                    if (check(joinPoint, requestLogger)) {
                        return;
                    }

                    RequestLog requestLog = getRequestLogger();
                    if (Objects.nonNull(ret) && ret instanceof Result) {
                        try {
                            Result<?> r = Convert.convert(Result.class, ret);
                            if (r.getCode() == ResultEnum.SUCCESS.codeDesc()) {
                                requestLog.setOperateType(
                                        LogOperateTypeEnum.OPERATE_RECORD.getCode());
                            } else {
                                requestLog.setOperateType(
                                        LogOperateTypeEnum.EXCEPTION_RECORD.getCode());
                                requestLog.setExDetail(r.getMessage());
                            }
                        } catch (ConvertException e) {
                            LogUtils.error(e);
                        }
                    }

                    requestLog.setTenantId(TenantContextHolder.getTenant());
                    long endTime = System.currentTimeMillis();
                    requestLog.setEndTime(endTime);
                    requestLog.setConsumingTime(endTime - requestLog.getStartTime());

                    if (requestLogger.response()) {
                        requestLog.setResult(
                                getText(
                                        ret == null
                                                ? StrPoolConstants.EMPTY
                                                : JacksonUtils.toJSONString(ret)));
                    }

                    publisher.publishEvent(new RequestLoggerEvent(requestLog));
                    REQUEST_LOG_THREAD_LOCAL.remove();
                });
    }

    @AfterThrowing(pointcut = "requestLogAspect()", throwing = "e")
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        tryCatch(
                p -> {
                    RequestLogger requestLogger = getTargetAnnotation(joinPoint);
                    if (check(joinPoint, requestLogger)) {
                        return;
                    }

                    RequestLog requestLog = getRequestLogger();
                    requestLog.setOperateType(LogOperateTypeEnum.EXCEPTION_RECORD.getCode());
                    String stackTrace = LogUtils.getStackTrace(e);
                    requestLog.setExDetail(stackTrace.replaceAll("\"", "'").replace("\n", ""));
                    requestLog.setExDesc(e.getMessage().replaceAll("\"", "'").replace("\n", ""));

                    if (!requestLogger.request()
                            && requestLogger.requestByError()
                            && StrUtil.isEmpty(requestLog.getParams())) {
                        Object[] args = joinPoint.getArgs();
                        HttpServletRequest request =
                                ((ServletRequestAttributes)
                                        Objects.requireNonNull(
                                                RequestContextHolder
                                                        .getRequestAttributes()))
                                        .getRequest();
                        String strArgs = getArgs(args, request);
                        requestLog.setParams(getText(strArgs));
                    }

                    publisher.publishEvent(new RequestLoggerEvent(requestLog));
                    REQUEST_LOG_THREAD_LOCAL.remove();
                });
    }

    private String getArgs(Object[] args, HttpServletRequest request) {
        String strArgs = StrPoolConstants.EMPTY;
        Object[] params =
                Arrays.stream(args)
                        .filter(
                                item ->
                                        !(item instanceof ServletRequest
                                                || item instanceof ServletResponse))
                        .toArray();

        try {
            if (!request.getContentType().contains(FORM_DATA_CONTENT_TYPE)) {
                strArgs = JacksonUtils.toJSONString(params);
            }
        } catch (Exception e) {
            try {
                strArgs = Arrays.toString(params);
            } catch (Exception ex) {
                LogUtils.error("解析参数异常", ex);
            }
        }
        return strArgs;
    }

    @NonNull
    private RequestLog buildRequestLog(JoinPoint joinPoint, RequestLogger requestLogger) {
        RequestLog requestLog = new RequestLog();
        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        RequestContextHolder.setRequestAttributes(attributes, true);
        HttpServletRequest request = attributes.getRequest();

        requestLog.setTraceId(MdcUtils.get(CommonConstants.TTC_TRACE_ID));
        requestLog.setApplicationName(applicationName);
        requestLog.setUsername(SecurityUtils.getUsernameWithAnonymous());
        requestLog.setUserId(String.valueOf(SecurityUtils.getUserIdWithAnonymous()));
        requestLog.setClientId(SecurityUtils.getClientId());
        String ip = RequestUtils.getRemoteAddr(request);
        requestLog.setIp(ip);
        requestLog.setStartTime(System.currentTimeMillis());
        requestLog.setUrl(getPath(request.getRequestURI()));
        requestLog.setMethod(request.getMethod());

        Object[] args = joinPoint.getArgs();
        List<String> argsList = new ArrayList<>();
        if (ArrayUtil.isNotEmpty(args)) {
            for (Object arg : args) {
                try {
                    argsList.add(JacksonUtils.toJSONString(arg));
                } catch (Exception e) {
                    LogUtils.error("请求参数转换失败");
                }
            }
        }
        requestLog.setArgs(argsList.toString().replaceAll("\"", "'").replace("\n", ""));
        requestLog.setBrowser(
                request.getHeader("user-agent").replaceAll("\"", "'").replace("\n", ""));
        requestLog.setClasspath(
                joinPoint.getTarget().getClass().getName().replaceAll("\"", "'").replace("\n", ""));

        String name = joinPoint.getSignature().getName();
        requestLog.setMethodName(name);
        requestLog.setParams(
                JacksonUtils.toJSONString(RequestUtils.getAllRequestParam(request))
                        .replaceAll("\"", "'")
                        .replace("\n", ""));
        requestLog.setHeaders(JacksonUtils.toJSONString(RequestUtils.getAllRequestHeaders(request)));
        requestLog.setRequestType(LogUtils.getRequestType(name));
        requestLog.setSource(DEFAULT_SOURCE);
        requestLog.setCtime(DateUtils.format(LocalDateTime.now(), NORM_DATETIME_PATTERN));
        requestLog.setLogday(DateUtils.getCurrentDate());

        Ip2regionSearcher ip2regionSearcher = ContextUtils.getBean(Ip2regionSearcher.class, true);
        if (ip2regionSearcher != null) {
            requestLog.setLocation(ip2regionSearcher.getAddressAndIsp(ip));
        } else {
            requestLog.setLocation(IpUtils.getCityInfo(ip));
        }

        String uaStr = request.getHeader("user-agent");
        UserAgent userAgent = UserAgentUtil.parse(uaStr);
        requestLog.setOs(JacksonUtils.toJSONString(userAgent));

        setDescription(joinPoint, requestLogger, requestLog);
        return requestLog;
    }

    /**
     * 监测是否需要记录日志
     *
     * @param joinPoint 端点
     * @param requestLogger 操作日志
     * @return true 表示需要记录日志
     */
    private boolean check(JoinPoint joinPoint, RequestLogger requestLogger) {
        if (requestLogger == null || !requestLogger.enabled()) {
            return true;
        }

        // 读取目标类上的注解
        RequestLogger targetClass =
                joinPoint.getTarget().getClass().getAnnotation(RequestLogger.class);

        // 加上 RequestLogger == null 会导致父类上的方法永远需要记录日志
        return targetClass != null && !targetClass.enabled();
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            LogUtils.error("记录操作日志异常", e);
            REQUEST_LOG_THREAD_LOCAL.remove();
        }
    }

    /**
     * 优先从子类获取 @RequestLogger： 1，若子类重写了该方法，有标记就记录日志，没标记就忽略日志 2，若子类没有重写该方法，就从父类获取，父类有标记就记录日志，没标记就忽略日志
     */
    public static RequestLogger getTargetAnnotation(JoinPoint point) {
        try {
            RequestLogger annotation = null;
            if (point.getSignature() instanceof MethodSignature) {
                Method method = ((MethodSignature) point.getSignature()).getMethod();
                if (method != null) {
                    annotation = method.getAnnotation(RequestLogger.class);
                }
            }

            if(annotation==null){
                annotation =
                        point.getTarget().getClass().getAnnotation(RequestLogger.class);
            }

            return annotation;
        } catch (Exception e) {
            LogUtils.error(
                    "获取 {}.{} 的 @RequestLogger 注解失败",
                    e,
                    point.getSignature().getDeclaringTypeName(),
                    point.getSignature().getName());
            return null;
        }
    }

    private RequestLog getRequestLogger() {
        RequestLog requestLog = REQUEST_LOG_THREAD_LOCAL.get();
        if (requestLog == null) {
            return new RequestLog();
        }
        return requestLog;
    }

    /**
     * 截取指定长度的字符串
     *
     * @param val 参数
     * @return 截取文本
     */
    private String getText(String val) {
        return StrUtil.sub(val, 0, 65535);
    }

    private void setDescription(
            JoinPoint joinPoint, RequestLogger requestLogger, RequestLog requestLog) {
        StringBuilder controllerDescription = new StringBuilder();
        Operation operation =
                AnnotationUtils.findAnnotation(
                        ((MethodSignature) joinPoint.getSignature()).getMethod(), Operation.class);
        if (operation != null) {
            String summary = operation.summary();
            if (StringUtils.isNotBlank(summary)) {
                controllerDescription.append("-").append(summary);
            }

            String[] tags = operation.tags();
            if (ArrayUtil.isNotEmpty(tags)) {
                controllerDescription.append("-").append(tags[0]);
            }

            String description = operation.description();
            if (StringUtils.isNotBlank(description)) {
                controllerDescription.append("-").append(description);
            }
        }

        String controllerMethodDescription = getDescribe(requestLogger);
        if (StrUtil.isNotBlank(controllerMethodDescription)
                && StrUtil.contains(controllerMethodDescription, StrPoolConstants.HASH)) {
            // 获取方法参数值
            Object[] args = joinPoint.getArgs();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            controllerMethodDescription =
                    getValBySpEl(controllerMethodDescription, methodSignature, args);
        }

        if (requestLogger.controllerApiValue() && StringUtils.isNotBlank(controllerDescription)) {
            requestLog.setDescription(controllerDescription + "-" + controllerMethodDescription);
        } else {
            requestLog.setDescription(controllerDescription.toString());
        }
    }

    /** 解析spEL表达式 */
    private String getValBySpEl(String spEl, MethodSignature methodSignature, Object[] args) {
        try {
            // 获取方法形参名数组
            String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
            if (paramNames != null && paramNames.length > 0) {
                Expression expression = SPEL_EXPRESSION_PARSER.parseExpression(spEl);
                // spring的表达式上下文对象
                EvaluationContext context = new StandardEvaluationContext();
                // 给上下文赋值
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                    context.setVariable("p" + i, args[i]);
                }
                Object value = expression.getValue(context);
                return value == null ? spEl : value.toString();
            }
        } catch (Exception e) {
            LogUtils.error("解析操作日志的el表达式出错", e);
        }
        return spEl;
    }

    public static String getDescribe(JoinPoint point) {
        RequestLogger annotation = getTargetAnnotation(point);
        if (annotation == null) {
            return StrPoolConstants.EMPTY;
        }
        return annotation.value();
    }

    public static String getDescribe( RequestLogger annotation) {
        if (annotation == null) {
            return StrPoolConstants.EMPTY;
        }
        return annotation.value();
    }
}
