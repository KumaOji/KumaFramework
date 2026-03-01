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

package com.kuma.boot.security.spring.authorization;

import com.kuma.boot.security.spring.access.security.SecurityAttribute;
import com.kuma.boot.security.spring.access.security.SecurityConfigAttribute;
import com.kuma.boot.security.spring.access.security.SecurityRequest;
import com.kuma.boot.security.spring.constants.SymbolConstants;
import com.kuma.boot.security.spring.enums.Category;
import com.kuma.boot.security.spring.enums.PermissionExpression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>SecurityMetadata异步处理Service
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:01:38
 */
public class SecurityMetadataSourceAnalyzer {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(SecurityMetadataSourceAnalyzer.class);

    /**
     * 安全存储元数据来源
     */
    private final SecurityMetadataSourceStorage securityMetadataSourceStorage;

    /**
     * 安全匹配器配置
     */
    private final com.kuma.boot.security.spring.authorization.SecurityMatcherConfigurer securityMatcherConfigurer;

    /**
     * 安全元数据来源分析仪
     *
     * @param securityMetadataSourceStorage 安全存储元数据来源
     * @param securityMatcherConfigurer     安全匹配器配置
     * @since 2023-07-04 10:01:38
     */
    public SecurityMetadataSourceAnalyzer(
            SecurityMetadataSourceStorage securityMetadataSourceStorage,
            com.kuma.boot.security.spring.authorization.SecurityMatcherConfigurer securityMatcherConfigurer) {
        this.securityMetadataSourceStorage = securityMetadataSourceStorage;
        this.securityMatcherConfigurer = securityMatcherConfigurer;
    }

    /**
     * 直接使用
     * {@link
     * org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer}
     * 中的方法
     *
     * @param authority 权限
     * @return {@link String }
     * @since 2023-07-04 10:01:38
     */
    private String hasAuthority(String authority) {
        return "hasAuthority('" + authority + "')";
    }

    /**
     * 将解析后的数据添加对应的分组中
     *
     * @param container 分组结果数据容器
     * @param category  分组类别
     * @param resources 权限数据
     * @since 2023-07-04 10:01:38
     */
    private void appendToGroup(
            Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> container,
            Category category,
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> resources) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> value = new LinkedHashMap<>();

        if (container.containsKey(category)) {
            value = container.get(category);
        }

        value.putAll(resources);

        container.put(category, value);
    }

    /**
     * 将各个服务配置的静态权限数据分组
     *
     * @param securityMatchers 静态权限数据
     * @return {@link Map }<{@link Category }, {@link LinkedHashMap }<{@link SecurityRequest },
     * {@link List }<{@link SecurityConfigAttribute }>>>
     * @since 2023-07-04 10:01:38
     */
    private Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>>
    groupSecurityMatchers(
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>
                    securityMatchers) {

        Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> group =
                new LinkedHashMap<>();

        securityMatchers.forEach(
                (key, value) -> {
                    LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> resources =
                            new LinkedHashMap<>();
                    resources.put(key, value);
                    appendToGroup(group, Category.getCategory(key.getPattern()), resources);
                });

        log.info("Grouping security matcher by category.");
        return group;
    }

    /**
     * 解析并动态组装所需要的权限。
     * <p>
     * 1. 原 spring-security-oauth Spring Security
     * 基础权限规则，来源于org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl
     * OAuth2 权限规则来源于
     * org.springframework.security.oauth2.provider.expression.OAuth2SecurityExpressionMethods 2. 新
     * spring-authorization-server Spring Security
     * 基础权限规则，来源于{@link
     * org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer}
     * OAuth2 权限规则来源于 目前还没有
     * <p>
     * 具体解析采用的是 Security 的 {@link org.springframework.security.access.AccessDecisionVoter}
     * 方式，而不是采用自定义的 {@link org.springframework.security.access.AccessDecisionManager} 该方式会与默认的
     * httpsecurity 配置覆盖。 · 基本的权限验证采用的是：{@link org.springframework.security.access.vote.RoleVoter} ·
     * scope权限采用两种方式： 一种是：Spring Security
     * org.springframework.security.oauth2.provider.vote.ScopeVoter 目前已取消 另一种是：OAuth2
     * 'hasScope'和'hasAnyScope'方式
     * org.springframework.security.oauth2.provider.expression.OAuth2SecurityExpressionMethods#hasAnyScope(String...)
     * <p>
     * 如果实际应用不满足可以，自己扩展AccessDecisionVoter或者AccessDecisionManager
     *
     * @param securityAttribute {@link SecurityAttribute}
     * @return {@link List }<{@link SecurityConfigAttribute }>
     * @since 2023-07-04 10:01:38
     */
    private List<SecurityConfigAttribute> analysis(SecurityAttribute securityAttribute) {

        List<SecurityConfigAttribute> attributes = new ArrayList<>();

        if (StringUtils.isNotBlank(securityAttribute.getPermissions())) {
            String[] permissions =
                    org.springframework.util.StringUtils.commaDelimitedListToStringArray(
                            securityAttribute.getPermissions());
            Arrays.stream(permissions)
                    .forEach(
                            item ->
                                    attributes.add(
                                            new SecurityConfigAttribute(hasAuthority(item))));
        }

        if (StringUtils.isNotBlank(securityAttribute.getWebExpression())) {
            attributes.add(new SecurityConfigAttribute(securityAttribute.getWebExpression()));
        }

        return attributes;
    }

    /**
     * 创建请求和权限的映射数据
     *
     * @param url              请求url
     * @param methods          请求method
     * @param configAttributes Security权限
     * @return {@link LinkedHashMap }<{@link SecurityRequest },
     * {@link List }<{@link SecurityConfigAttribute }>>
     * @since 2023-07-04 10:01:39
     */
    private LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> convert(
            String url, String methods, List<SecurityConfigAttribute> configAttributes) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> result =
                new LinkedHashMap<>();
        if (StringUtils.isBlank(methods)) {
            result.put(new SecurityRequest(url), configAttributes);
        } else {
            // 如果methods是以逗号分隔的字符串，那么进行拆分处理
            if (StringUtils.contains(methods, SymbolConstants.COMMA)) {
                String[] multiMethod = StringUtils.split(methods, SymbolConstants.COMMA);
                for (String method : multiMethod) {
                    result.put(new SecurityRequest(url, method), configAttributes);
                }
            } else {
                result.put(new SecurityRequest(url, methods), configAttributes);
            }
        }

        return result;
    }

    /**
     * 将 UPMS 分发的 SecurityAttributes 数据进行权限转换并分组
     *
     * @param securityAttributes 权限数据
     * @return {@link Map }<{@link Category }, {@link LinkedHashMap }<{@link SecurityRequest },
     * {@link List }<{@link SecurityConfigAttribute }>>>
     * @since 2023-07-04 10:01:39
     */
    private Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>>
    groupingSecurityMetadata(List<SecurityAttribute> securityAttributes) {

        Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> group =
                new LinkedHashMap<>();

        securityAttributes.forEach(
                securityAttribute -> {
                    LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> resources =
                            convert(
                                    securityAttribute.getUrl(),
                                    securityAttribute.getRequestMethod(),
                                    analysis(securityAttribute));
                    appendToGroup(
                            group, Category.getCategory(securityAttribute.getUrl()), resources);
                });

        log.info("Grouping security metadata by category.");
        return group;
    }

    /**
     * 获取 SecurityFilterChain 中配置的 RequestMatchers 信息。
     * <p>
     * RequestMatchers 可以理解为“静态配置”，将其与平台后端的“动态配置”有机结合。同时，防止因动态配置导致的静态配置失效的问题。
     * <p>
     * 目前只处理了 permitAll 类型。其它内容根据后续使用情况再行添加。
     *
     * @return {@link LinkedHashMap }<{@link SecurityRequest },
     * {@link List }<{@link SecurityConfigAttribute }>>
     * @since 2023-07-04 10:01:39
     */
    private LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> getRequestMatchers() {

        List<String> permitAllMatcher = securityMatcherConfigurer.getPermitAllList();

        if (CollectionUtils.isNotEmpty(permitAllMatcher)) {
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> result =
                    new LinkedHashMap<>();
            permitAllMatcher.forEach(
                    item -> {
                        result.put(
                                new SecurityRequest(item),
                                List.of(
                                        new SecurityConfigAttribute(
                                                PermissionExpression.PERMIT_ALL.getValue())));
                    });
            return result;
        }
        return new LinkedHashMap<>();
    }

    /**
     * 各个服务静态化配置的权限过滤，通常为通配符型或者全路径型，很少有站位符型。即：大多数情况为 {@code Category.WILDCARD} 和
     * {@code Category.PLACEHOLDER}，很少有 {@code Category.FULL_PATH}
     * <p>
     * 此处的逻辑是： 1. 先处理各个服务静态化配置的权限，当前假设不会有{@code Category.FULL_PATH}类型的权限。后期如果该种权限较多再补充即可。
     * 同时，静态服务都是开发人员手工配置，假定手工配置时就会对是否冲突进行处理，当然也可能出现冲突，那么这个开发人员得多不负责。 2. 经过考虑，服务本地接口扫描完，就对所有的
     * RequestMapping 做一遍解析，现在感觉意义不大。 因为，RequestMapping 汇总至 UPMS 后，还会做一次统一的分发。所以当前的设计思路是不对
     * RequestMapping 进行处理。后续根据需要再补充即可。
     *
     * @since 2023-07-04 10:01:39
     */
    public void processRequestMatchers() {
        log.info("[3] Process local configured security metadata.");

        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> requestMatchers =
                getRequestMatchers();
        if (MapUtils.isNotEmpty(requestMatchers)) {
            Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> grouping =
                    groupSecurityMatchers(requestMatchers);

            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> wildcards =
                    grouping.get(Category.WILDCARD);
            securityMetadataSourceStorage.addToStorage(wildcards, false);

            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> fullPaths =
                    grouping.get(Category.FULL_PATH);
            securityMetadataSourceStorage.addToStorage(fullPaths, true);
        }
    }

    /**
     * 处理分发的 SecurityAttribute，将其转换、解析为表达式权限，并存入本地缓存，用于权限校验
     * <p>
     * 处理过程中，会根据规则对权限类型分组，然后进行去重的操作。
     *
     * @param securityAttributes 权限数据
     * @since 2023-07-04 10:01:39
     */
    public void processSecurityAttribute(List<SecurityAttribute> securityAttributes) {

        // 从缓存中获取全部带有特殊字符的匹配规则
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatibles =
                securityMetadataSourceStorage.getCompatible();
        // 创建一个临时的 Matcher 容器
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> matchers =
                new LinkedHashMap<>(compatibles);

        // 对分发的 SecurityAttribute 进行分组
        Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> grouping =
                groupingSecurityMetadata(securityAttributes);

        // 拿到带有通配符的分组数据
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> wildcards =
                grouping.get(Category.WILDCARD);
        if (MapUtils.isNotEmpty(wildcards)) {
            matchers.putAll(wildcards);
            securityMetadataSourceStorage.addToStorage(wildcards, false);
        }

        // 拿到带有占位符的分组数据，并检测是否存在冲突的匹配规则，然后将结果存入本地存储
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> placeholders =
                grouping.get(Category.PLACEHOLDER);
        log.info("Store placeholder type security attributes.");
        securityMetadataSourceStorage.addToStorage(matchers, placeholders, false);

        // 拿到全路径的分组数据，并检测是否存在冲突的匹配规则，然后将结果存入本地存储
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> fullPaths =
                grouping.get(Category.FULL_PATH);
        log.info("Store full path type security attributes.");
        securityMetadataSourceStorage.addToStorage(matchers, fullPaths, true);

        log.info("[7] Security attributes process has FINISHED!");
    }
}
