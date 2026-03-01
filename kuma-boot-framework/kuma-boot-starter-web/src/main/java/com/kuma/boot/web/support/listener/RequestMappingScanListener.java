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

package com.kuma.boot.web.support.listener;

import com.google.common.collect.Maps;
import com.kuma.boot.actuator.endpoint.requst.RequestMappingEndPoint;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.constant.RedisConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
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

import java.util.*;

import static com.kuma.boot.common.constant.CommonConstants.*;

/**
 * 请求资源扫描监听器
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-26 16:38:30
 */
public class RequestMappingScanListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final AntPathMatcher pathMatch = new AntPathMatcher();
    private final Set<String> ignoreApi = new HashSet<>();
    private final RedisRepository redisService;

    public RequestMappingScanListener(RedisRepository redisService) {
        this.redisService = redisService;
        this.ignoreApi.add("/error");
        this.ignoreApi.add("/swagger-resources/**");
        this.ignoreApi.add("/v2/api-docs-ext/**");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ConfigurableApplicationContext applicationContext = event.getApplicationContext();
            Environment env = applicationContext.getEnvironment();

            // 获取微服务模块名称
            String microService =
                    env.getProperty(CommonConstants.SPRING_APP_NAME_KEY, "application");
            if (redisService == null
                    || applicationContext.containsBean("resourceServerConfiguration")) {
                LogUtils.warn("[{}]忽略接口资源扫描", microService);
                return;
            }

            // 所有接口映射
            RequestMappingHandlerMapping mapping =
                    applicationContext.getBean(
                            "requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            // 获取url与类和方法的对应信息
            Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
            List<Map<String, String>> list = new ArrayList<>();
            for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
                RequestMappingInfo info = m.getKey();
                HandlerMethod method = m.getValue();

                // 忽略的接口不扫描
                Operation methodAnnotation = method.getMethodAnnotation(Operation.class);
                if (methodAnnotation != null) {
                    if (methodAnnotation.hidden()) {
                        continue;
                    }
                }

                // 请求路径
                PathPatternsRequestCondition p = info.getPathPatternsCondition();
                String urls = "";
                if (Objects.nonNull(p)) {
                    urls = getUrls(p.getPatterns());
                    if (isIgnore(urls)) {
                        continue;
                    }
                }

                Set<MediaType> mediaTypeSet = info.getProducesCondition().getProducibleMediaTypes();
                for (MethodParameter params : method.getMethodParameters()) {
                    if (params.hasParameterAnnotation(RequestBody.class)) {
                        mediaTypeSet.add(MediaType.APPLICATION_JSON);
                        break;
                    }
                }

                String mediaTypes = getMediaTypes(mediaTypeSet);
                // 请求类型
                RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
                String methods = getMethods(methodsCondition.getMethods());
                Map<String, String> api = Maps.newHashMap();
                // 类名
                String className = method.getMethod().getDeclaringClass().getName();
                // 方法名
                String methodName = method.getMethod().getName();
                String fullName = className + "." + methodName;
                // md5码
                String md5 = DigestUtils.md5DigestAsHex((microService + urls).getBytes());
                String name = "";
                String notes = "";
                String auth = "0";

                Operation apiOperation = method.getMethodAnnotation(Operation.class);
                if (apiOperation != null) {
                    name = apiOperation.summary();
                    notes = apiOperation.description();
                }

                // 判断是否需要权限校验
                PreAuthorize preAuth = method.getMethodAnnotation(PreAuthorize.class);
                if (preAuth != null) {
                    auth = "1";
                }

                name = StringUtils.isBlank(name) ? methodName : name;
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

            // 放入redis缓存
            Map<String, Object> res = Maps.newHashMap();
            res.put("serviceId", microService);
            res.put("size", list.size());
            res.put("list", list);

            String property = microService.replace(DASH, UNDER).toUpperCase();

            redisService.hset(
                    property + UNDER + RedisConstants.API_RESOURCE,
                    microService,
                    res,
                    RESOURCE_EXPIRE);
            redisService.sSetAndTime(
                    property + UNDER + RedisConstants.SERVICE_RESOURCE,
                    RESOURCE_EXPIRE,
                    microService);
            LogUtils.info(
                    "资源扫描结果:serviceId=[{}] size=[{}] redis缓存key=[{}]",
                    microService,
                    list.size(),
                    property + UNDER + RedisConstants.API_RESOURCE);

            RequestMappingEndPoint.requestMappingHandlerMapping = res;
        } catch (Exception e) {
            LogUtils.error("error: {}", e.getMessage());
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

    /**
     * 是否是忽略的Api
     *
     * @param requestPath 请求地址
     * @return boolean
     */
    private boolean isIgnore(String requestPath) {
        for (String path : ignoreApi) {
            if (pathMatch.match(path, requestPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取媒体类型
     *
     * @param mediaTypes 类型SET集
     * @return String
     */
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

    /**
     * 获取方法
     *
     * @param requestMethods 请求方法
     * @return String
     */
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
