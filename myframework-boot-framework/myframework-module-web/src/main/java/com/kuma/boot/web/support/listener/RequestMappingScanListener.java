/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.kuma.boot.actuator.endpoint.requst.RequestMappingEndPoint
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.constant.CommonConstants
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  io.swagger.v3.oas.annotations.Operation
 *  org.springframework.boot.context.event.ApplicationReadyEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.env.ConfigurableEnvironment
 *  org.springframework.http.MediaType
 *  org.springframework.security.access.prepost.PreAuthorize
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.DigestUtils
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.method.HandlerMethod
 *  org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition
 *  org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition
 *  org.springframework.web.servlet.mvc.method.RequestMappingInfo
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 *  org.springframework.web.util.pattern.PathPattern
 */
package com.kuma.boot.web.support.listener;

import com.google.common.collect.Maps;
import com.kuma.boot.actuator.endpoint.requst.RequestMappingEndPoint;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

public class RequestMappingScanListener
implements ApplicationListener<ApplicationReadyEvent> {
    private static final AntPathMatcher pathMatch = new AntPathMatcher();
    private final Set<String> ignoreApi = new HashSet<String>();
    private final RedisRepository redisService;

    public RequestMappingScanListener(RedisRepository redisService) {
        this.redisService = redisService;
        this.ignoreApi.add("/error");
        this.ignoreApi.add("/swagger-resources/**");
        this.ignoreApi.add("/v2/api-docs-ext/**");
    }

    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ConfigurableApplicationContext applicationContext = event.getApplicationContext();
            ConfigurableEnvironment env = applicationContext.getEnvironment();
            String microService = env.getProperty(CommonConstants.SPRING_APP_NAME_KEY, "application");
            if (this.redisService == null || applicationContext.containsBean("resourceServerConfiguration")) {
                LogUtils.warn((String)"[{}]\u5ffd\u7565\u63a5\u53e3\u8d44\u6e90\u626b\u63cf", (Object[])new Object[]{microService});
                return;
            }
            RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping)applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            Map map = mapping.getHandlerMethods();
            ArrayList<HashMap> list = new ArrayList<HashMap>();
            for (Map.Entry m : map.entrySet()) {
                PreAuthorize preAuth;
                RequestMappingInfo info = (RequestMappingInfo)m.getKey();
                HandlerMethod method = (HandlerMethod)m.getValue();
                Operation methodAnnotation = (Operation)method.getMethodAnnotation(Operation.class);
                if (methodAnnotation != null && methodAnnotation.hidden()) continue;
                PathPatternsRequestCondition p = info.getPathPatternsCondition();
                String urls = "";
                if (Objects.nonNull(p) && this.isIgnore(urls = this.getUrls(p.getPatterns()))) continue;
                Set mediaTypeSet = info.getProducesCondition().getProducibleMediaTypes();
                for (MethodParameter params : method.getMethodParameters()) {
                    if (!params.hasParameterAnnotation(RequestBody.class)) continue;
                    mediaTypeSet.add(MediaType.APPLICATION_JSON);
                    break;
                }
                String mediaTypes = this.getMediaTypes(mediaTypeSet);
                RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
                String methods = this.getMethods(methodsCondition.getMethods());
                HashMap api = Maps.newHashMap();
                String className = method.getMethod().getDeclaringClass().getName();
                String methodName = method.getMethod().getName();
                String fullName = className + "." + methodName;
                String md5 = DigestUtils.md5DigestAsHex((byte[])(microService + urls).getBytes());
                String name = "";
                String notes = "";
                String auth = "0";
                Operation apiOperation = (Operation)method.getMethodAnnotation(Operation.class);
                if (apiOperation != null) {
                    name = apiOperation.summary();
                    notes = apiOperation.description();
                }
                if ((preAuth = (PreAuthorize)method.getMethodAnnotation(PreAuthorize.class)) != null) {
                    auth = "1";
                }
                name = StringUtils.isBlank((String)name) ? methodName : name;
                api.put("name", name);
                api.put("notes", notes);
                api.put("path", urls);
                api.put("code", md5);
                api.put("className", className);
                api.put("methodName", methodName);
                api.put("fullName", fullName);
                api.put("method", methods);
                api.put("serviceId", microService);
                api.put("contentType", mediaTypes);
                api.put("auth", auth);
                list.add(api);
            }
            HashMap res = Maps.newHashMap();
            res.put("serviceId", microService);
            res.put("size", list.size());
            res.put("list", list);
            String property = microService.replace("-", "_").toUpperCase();
            this.redisService.hset(property + "_API:RESOURCE", microService, (Object)res, CommonConstants.RESOURCE_EXPIRE);
            this.redisService.sSetAndTime(property + "_SERVICE:RESOURCE", CommonConstants.RESOURCE_EXPIRE, new Object[]{microService});
            LogUtils.info((String)"\u8d44\u6e90\u626b\u63cf\u7ed3\u679c:serviceId=[{}] size=[{}] redis\u7f13\u5b58key=[{}]", (Object[])new Object[]{microService, list.size(), property + "_API:RESOURCE"});
            RequestMappingEndPoint.requestMappingHandlerMapping = res;
        }
        catch (Exception e) {
            LogUtils.error((String)"error: {}", (Object[])new Object[]{e.getMessage()});
        }
    }

    private String getUrls(Set<PathPattern> urls) {
        StringBuilder stringBuilder = new StringBuilder();
        for (PathPattern url : urls) {
            stringBuilder.append(url.getPatternString()).append(",");
        }
        if (!urls.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    private boolean isIgnore(String requestPath) {
        for (String path : this.ignoreApi) {
            if (!pathMatch.match(path, requestPath)) continue;
            return true;
        }
        return false;
    }

    private String getMediaTypes(Set<MediaType> mediaTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (MediaType mediaType : mediaTypes) {
            stringBuilder.append(mediaType.toString()).append(",");
        }
        if (!mediaTypes.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    private String getMethods(Set<RequestMethod> requestMethods) {
        StringBuilder stringBuilder = new StringBuilder();
        for (RequestMethod requestMethod : requestMethods) {
            stringBuilder.append(requestMethod.toString()).append(",");
        }
        if (!requestMethods.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}

