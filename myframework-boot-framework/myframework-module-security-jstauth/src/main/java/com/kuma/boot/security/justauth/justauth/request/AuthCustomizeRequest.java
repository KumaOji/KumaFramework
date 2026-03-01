/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.justauth.request;

import com.kuma.boot.security.justauth.justauth.source.AuthCustomizeSource;
import com.kuma.boot.security.justauth.justauth.source.AuthGitlabPrivateSource;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.request.AuthDefaultRequest;

/**
 * 抽象类, 实现此自定义的 {@link AuthCustomizeRequest} 同时, 必须实现 {@link AuthCustomizeSource} 或 {@link AuthGitlabPrivateSource} 且注入 ioc 容器,
 * 会自动集成进 OAuth2 Login 逻辑流程中,
 * 只需要像 JustAuth 默认实现的第三方登录一样, 配置相应的属性(ums.oauth.customize.[clientId|clientSecret]等属性)即可.
 * @author YongWu zheng
 * @version V2.0  Created by 2020.11.29 13:33
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class AuthCustomizeRequest extends AuthDefaultRequest {

    public AuthCustomizeRequest(AuthConfig config, AuthSource source) {
        super(config, source);
    }

    public AuthCustomizeRequest(
            AuthConfig config, AuthSource source, AuthStateCache authStateCache) {
        super(config, source, authStateCache);
    }
}
