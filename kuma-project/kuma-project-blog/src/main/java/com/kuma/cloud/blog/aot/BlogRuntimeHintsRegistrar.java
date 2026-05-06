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

package com.kuma.cloud.blog.aot;

import static org.springframework.aot.hint.MemberCategory.ACCESS_DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;

import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.boot.security.spring.access.expression.AuthorizeCheckService;
import com.kuma.boot.security.spring.access.expression.AuthorizeExpressionHandler;
import com.kuma.boot.security.spring.access.expression.RootObject;
import com.kuma.cloud.blog.config.AdminPasswordInitRunner;
import com.kuma.cloud.blog.config.BlogServletWebServerConfiguration;
import com.kuma.cloud.blog.config.SecurityConfig;
import com.kuma.cloud.blog.config.WebMvcConfig;
import com.kuma.cloud.blog.domain.entity.Article;
import com.kuma.cloud.blog.domain.entity.Category;
import com.kuma.cloud.blog.domain.entity.Music;
import com.kuma.cloud.blog.domain.entity.ReadyItem;
import com.kuma.cloud.blog.domain.entity.Source;
import com.kuma.cloud.blog.domain.entity.SysPermission;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.domain.vo.ArticleQueryVO;
import com.kuma.cloud.blog.domain.vo.ArticleVO;
import com.kuma.cloud.blog.domain.vo.CategoryArticleCountVO;
import com.kuma.cloud.blog.domain.vo.CategoryVO;
import com.kuma.cloud.blog.domain.vo.CommandRequest;
import com.kuma.cloud.blog.domain.vo.CommandResponse;
import com.kuma.cloud.blog.domain.vo.FileUploadResponse;
import com.kuma.cloud.blog.domain.vo.GrantPermissionRequest;
import com.kuma.cloud.blog.domain.vo.LoginRequest;
import com.kuma.cloud.blog.domain.vo.LoginResponse;
import com.kuma.cloud.blog.domain.vo.MusicQueryVO;
import com.kuma.cloud.blog.domain.vo.MusicVO;
import com.kuma.cloud.blog.domain.vo.ReadyQueryVO;
import com.kuma.cloud.blog.domain.vo.ReadyVO;
import com.kuma.cloud.blog.domain.vo.RepkgRequest;
import com.kuma.cloud.blog.domain.vo.RevokePermissionRequest;
import com.kuma.cloud.blog.domain.vo.TotpStatusResponse;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.security.BlogUserDetails;
import com.kuma.cloud.blog.security.JsonAccessDeniedHandler;
import com.kuma.cloud.blog.security.JsonAuthenticationEntryPoint;
import com.kuma.cloud.blog.security.LoginRateLimiter;
import com.kuma.cloud.blog.security.TokenAuthenticationFilter;
import com.kuma.cloud.blog.security.TotpAttemptLimiter;
import com.kuma.cloud.blog.service.TokenService;
import com.kuma.cloud.blog.service.impl.TokenServiceImpl;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;

/**
 * GraalVM / Spring AOT：博客领域模型、VO、安全组件与 {@link Authorize} 相关类型的反射 hints。
 */
public class BlogRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        for (Class<?> type :
                new Class<?>[] {
                    // entities
                    Article.class,
                    Category.class,
                    Music.class,
                    ReadyItem.class,
                    Source.class,
                    SysPermission.class,
                    User.class,
                    // vo
                    ArticleQueryVO.class,
                    ArticleVO.class,
                    CategoryArticleCountVO.class,
                    CategoryVO.class,
                    CommandRequest.class,
                    CommandResponse.class,
                    FileUploadResponse.class,
                    GrantPermissionRequest.class,
                    LoginRequest.class,
                    LoginResponse.class,
                    MusicQueryVO.class,
                    MusicVO.class,
                    ReadyQueryVO.class,
                    ReadyVO.class,
                    RepkgRequest.class,
                    RevokePermissionRequest.class,
                    TotpStatusResponse.class,
                    // @Authorize 与表达式
                    Authorize.class,
                    AuthorizeCheckService.class,
                    AuthorizeExpressionHandler.class,
                    RootObject.class,
                    AuthorizeCheckService.UserEntity.class,
                    // blog security & config
                    BlogPermissions.class,
                    BlogUserDetails.class,
                    JsonAccessDeniedHandler.class,
                    JsonAuthenticationEntryPoint.class,
                    LoginRateLimiter.class,
                    TokenAuthenticationFilter.class,
                    TotpAttemptLimiter.class,
                    SecurityConfig.class,
                    WebMvcConfig.class,
                    AdminPasswordInitRunner.class,
                    BlogServletWebServerConfiguration.class,
                    TokenService.class,
                    TokenServiceImpl.class,
                }) {
            hints.reflection()
                    .registerType(
                            type,
                            INVOKE_PUBLIC_METHODS,
                            INVOKE_DECLARED_CONSTRUCTORS,
                            ACCESS_DECLARED_FIELDS);
        }
        hints.reflection()
                .registerType(
                        TomcatServletWebServerFactory.class,
                        INVOKE_PUBLIC_METHODS,
                        INVOKE_DECLARED_CONSTRUCTORS,
                        ACCESS_DECLARED_FIELDS);

        hints.resources().registerPattern("banner/kmc-banner.txt");
        hints.resources().registerPattern("**/banner/kmc-banner.txt");

        hints.resources().registerPattern("sql/*.sql");
        hints.resources().registerPattern("mapper/*.xml");
        hints.resources().registerPattern("logback-spring.xml");
    }
}
