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

import static com.kuma.boot.security.spring.utils.SecurityUtils.toRequestMatchers;

import com.kuma.boot.security.spring.constants.WebResources;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.utils.ListUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * <p>安全过滤配置处理器
 * <p>
 * 对静态资源、开放接口等静态配置进行处理。整合默认配置和配置文件中的配置
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:01:03
 */
public class SecurityMatcherConfigurer {

    /**
     * 静态资源
     */
    private List<String> staticResources;

    /**
     * 允许所有资源
     */
    private List<String> permitAllResources;

    /**
     * 有认证资源
     */
    private List<String> hasAuthenticatedResources;

    /**
     * 授权属性
     */
    private final OAuth2AuthorizationProperties authorizationProperties;

    /**
     * 安全匹配器配置
     *
     * @param authorizationProperties 授权属性
     * @since 2023-07-04 10:01:03
     */
    public SecurityMatcherConfigurer(OAuth2AuthorizationProperties authorizationProperties) {
        this.authorizationProperties = authorizationProperties;
        this.staticResources = new ArrayList<>();
        this.permitAllResources = new ArrayList<>();
        this.hasAuthenticatedResources = new ArrayList<>();
    }

    /**
     * 得到静态资源列表
     *
     * @return {@link List }<{@link String }>
     * @since 2023-07-04 10:01:03
     */
    public List<String> getStaticResourceList() {
        if (CollectionUtils.isEmpty(this.staticResources)) {
            this.staticResources =
                    ListUtils.merge(
                            authorizationProperties.getMatcher().getStaticResources(),
                            WebResources.DEFAULT_IGNORED_STATIC_RESOURCES);
        }
        return this.staticResources;
    }

    /**
     * 得到允许列表
     *
     * @return {@link List }<{@link String }>
     * @since 2023-07-04 10:01:03
     */
    public List<String> getPermitAllList() {
        if (CollectionUtils.isEmpty(this.permitAllResources)) {
            this.permitAllResources =
                    ListUtils.merge(
                            authorizationProperties.getMatcher().getPermitAll(),
                            WebResources.DEFAULT_PERMIT_ALL_RESOURCES);
        }
        return this.permitAllResources;
    }

    /**
     * 得到了验证列表
     *
     * @return {@link List }<{@link String }>
     * @since 2023-07-04 10:01:03
     */
    public List<String> getHasAuthenticatedList() {
        if (CollectionUtils.isEmpty(this.hasAuthenticatedResources)) {
            this.hasAuthenticatedResources =
                    ListUtils.merge(
                            authorizationProperties.getMatcher().getHasAuthenticated(),
                            WebResources.DEFAULT_HAS_AUTHENTICATED_RESOURCES);
        }
        return this.hasAuthenticatedResources;
    }

    /**
     * 得到数组静态资源
     *
     * @return {@link String[] }
     * @since 2023-07-04 10:01:04
     */
    public RequestMatcher[] getStaticResourceArray() {
        return toRequestMatchers(getStaticResourceList());
    }

    /**
     * 得到允许所有数组
     *
     * @return {@link String[] }
     * @since 2023-07-04 10:01:04
     */
    public RequestMatcher[] getPermitAllArray() {
        return toRequestMatchers(getStaticResourceList());
    }

    /**
     * 获取授权属性
     *
     * @return {@link OAuth2AuthorizationProperties }
     * @since 2023-07-04 10:01:04
     */
    public OAuth2AuthorizationProperties getAuthorizationProperties() {
        return authorizationProperties;
    }
}
