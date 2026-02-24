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

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.LRUCache;
import com.kuma.boot.security.spring.access.security.SecurityConfigAttribute;
import com.kuma.boot.security.spring.access.security.SecurityRequest;
import com.kuma.boot.security.spring.access.security.SecurityRequestMatcher;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * <p>SecurityAttribute 本地存储
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:01:45
 */
public class SecurityMetadataSourceStorage {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(SecurityMetadataSourceStorage.class);

    /**
     * 模式匹配权限缓存。主要存储 包含 "*"、"?" 和 "{"、"}" 等特殊字符的路径权限。 该种权限，需要通过遍历，利用 PathPatternRequestMatcher 机制进行匹配
     */
    private final Cache<String, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>>
            compatible;

    /**
     * 直接索引权限缓存，主要存储全路径权限 该种权限，直接通过 Map Key 进行获取
     */
    private final Cache<SecurityRequest, List<SecurityConfigAttribute>> indexable;

    /**
     * 安全存储元数据来源
     *
     * @since 2023-07-04 10:01:45
     */
    public SecurityMetadataSourceStorage() {
        this.compatible = new LRUCache<>(100);
        this.indexable = new LRUCache<>(100);
        //        this.compatible =
        // JetCacheUtils.create(OAuth2Constants.CACHE_NAME_SECURITY_METADATA_COMPATIBLE,
        // CacheType.BOTH, null, true, true);
        //        this.indexable =
        // JetCacheUtils.create(OAuth2Constants.CACHE_NAME_SECURITY_METADATA_INDEXABLE,
        // CacheType.BOTH, null, true, true);
    }

    /**
     * 关键兼容
     */
    private static final String KEY_COMPATIBLE = "COMPATIBLE";

    /**
     * 从 compatible 缓存中读取数据。
     *
     * @return {@link LinkedHashMap }<{@link SecurityRequest },
     * {@link List }<{@link SecurityConfigAttribute }>>
     * @since 2023-07-04 10:01:45
     */
    private LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> readFromCompatible() {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatible =
                this.compatible.get(KEY_COMPATIBLE);
        if (MapUtils.isNotEmpty(compatible)) {
            return compatible;
        }
        return new LinkedHashMap<>();
    }

    /**
     * 写入 compatible 缓存
     *
     * @param compatible 请求路径和权限配置属性映射Map
     * @since 2023-07-04 10:01:45
     */
    private void writeToCompatible(
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatible) {
        this.compatible.put(KEY_COMPATIBLE, compatible);
    }

    /**
     * 从 indexable 缓存中读取数据
     *
     * @param securityRequest 自定义扩展的 PathPatternRequestMatchers
     * @return {@link List }<{@link SecurityConfigAttribute }>
     * @since 2023-07-04 10:01:45
     */
    private List<SecurityConfigAttribute> readFromIndexable(SecurityRequest securityRequest) {
        return this.indexable.get(securityRequest);
    }

    /**
     * 写入 indexable 缓存
     *
     * @param securityRequest  自定义扩展的 PathPatternRequestMatchers
     * @param configAttributes 权限配置属性
     * @since 2023-07-04 10:01:45
     */
    private void writeToIndexable(
            SecurityRequest securityRequest, List<SecurityConfigAttribute> configAttributes) {
        this.indexable.put(securityRequest, configAttributes);
    }

    /**
     * 根据请求的 url 和 method 获取权限对象
     *
     * @param url    请求 URL
     * @param method 请求 method
     * @return {@link List }<{@link SecurityConfigAttribute }>
     * @since 2023-07-04 10:01:45
     */
    public List<SecurityConfigAttribute> getConfigAttribute(String url, String method) {
        SecurityRequest securityRequest = new SecurityRequest(url, method);
        return readFromIndexable(securityRequest);
    }

    /**
     * 从 compatible 缓存中获取全部不需要路径匹配的（包含*号的url）请求权限映射Map
     *
     * @return {@link LinkedHashMap }<{@link SecurityRequest },
     * {@link List }<{@link SecurityConfigAttribute }>>
     * @since 2023-07-04 10:01:45
     */
    public LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> getCompatible() {
        return readFromCompatible();
    }

    /**
     * 向 compatible 缓存中添加需要路径匹配的（包含*号的url）请求权限映射Map。
     * <p>
     * 如果缓存中不存在以{@link RequestMatcher}为Key的数据，那么添加数据 如果缓存中存在以{@link RequestMatcher}为Key的数据，那么合并数据
     *
     * @param securityRequest  请求匹配对象
     * @param configAttributes 权限配置
     * @since 2023-07-04 10:01:45
     */
    private void appendToCompatible(
            SecurityRequest securityRequest, List<SecurityConfigAttribute> configAttributes) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatible =
                this.getCompatible();
        //        compatible.merge(requestMatcher, configAttributes, (oldConfigAttributes,
        // newConfigAttributes) -> {
        //            newConfigAttributes.addAll(oldConfigAttributes);
        //            return newConfigAttributes;
        //        });

        // 使用merge会让整个功能的设计更加复杂，暂时改为直接覆盖已有数据，后续视情况再做变更。
        compatible.put(securityRequest, configAttributes);
        log.info(
                "Append [{}] to Compatible cache, current size is [{}]",
                securityRequest,
                compatible.size());
        writeToCompatible(compatible);
    }

    /**
     * 向 compatible 缓存中添加需要路径匹配的（包含*号的url）请求权限映射Map。
     * <p>
     * 如果缓存中不存在以{@link RequestMatcher}为Key的数据，那么添加数据 如果缓存中存在以{@link RequestMatcher}为Key的数据，那么合并数据
     *
     * @param configAttributes 请求权限映射Map
     * @since 2023-07-04 10:01:45
     */
    private void appendToCompatible(
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes) {
        configAttributes.forEach(this::appendToCompatible);
    }

    /**
     * 向 indexable 缓存中添加需请求权限映射。
     * <p>
     * 如果缓存中不存在以{@link SecurityRequest}为Key的数据，那么添加数据 如果缓存中存在以{@link SecurityRequest}为Key的数据，那么合并数据
     *
     * @param securityRequest  请求匹配对象
     * @param configAttributes 权限配置
     * @since 2023-07-04 10:01:45
     */
    private void appendToIndexable(
            SecurityRequest securityRequest, List<SecurityConfigAttribute> configAttributes) {
        writeToIndexable(securityRequest, configAttributes);
    }

    /**
     * 向 indexable 缓存中添加请求权限映射Map。
     *
     * @param configAttributes 请求权限映射Map
     * @since 2023-07-04 10:01:45
     */
    private void appendToIndexable(
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes) {
        configAttributes.forEach(this::appendToIndexable);
    }

    /**
     * 将权限数据添加至本地存储
     *
     * @param configAttributes 权限数据
     * @param isIndexable      true 存入 indexable cache；false 存入 compatible cache
     * @since 2023-07-04 10:01:45
     */
    public void addToStorage(
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes,
            boolean isIndexable) {
        if (MapUtils.isNotEmpty(configAttributes)) {
            if (isIndexable) {
                appendToIndexable(configAttributes);
            } else {
                appendToCompatible(configAttributes);
            }
        }
    }

    /**
     * 将权限数据添加至本地存储，存储之前进行规则冲突校验
     *
     * @param matchers         校验资源
     * @param configAttributes 权限数据
     * @param isIndexable      true 存入 indexable cache；false 存入 compatible cache
     * @since 2023-07-04 10:01:45
     */
    public void addToStorage(
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> matchers,
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes,
            boolean isIndexable) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> result =
                new LinkedHashMap<>();
        if (MapUtils.isNotEmpty(matchers) && MapUtils.isNotEmpty(configAttributes)) {
            result = checkConflict(matchers, configAttributes);
        }

        addToStorage(result, isIndexable);
    }

    /**
     * 规则冲突校验
     * <p>
     * 如存在规则冲突，则保留可支持最大化范围规则，冲突的其它规则则不保存
     *
     * @param matchers         校验资源
     * @param configAttributes 权限数据
     * @return {@link LinkedHashMap }<{@link SecurityRequest },
     * {@link List }<{@link SecurityConfigAttribute }>>
     * @since 2023-07-04 10:01:45
     */
    private LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> checkConflict(
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> matchers,
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> configAttributes) {

        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> result =
                new LinkedHashMap<>(configAttributes);

        for (SecurityRequest matcher : matchers.keySet()) {
            for (SecurityRequest item : configAttributes.keySet()) {
                SecurityRequestMatcher requestMatcher = new SecurityRequestMatcher(matcher);
                if (requestMatcher.matches(item)) {
                    result.remove(item);
                    log.info(
                            "Pattern [{}] is conflict with [{}], so remove it.",
                            item.getPattern(),
                            matcher.getPattern());
                }
            }
        }

        return result;
    }
}
