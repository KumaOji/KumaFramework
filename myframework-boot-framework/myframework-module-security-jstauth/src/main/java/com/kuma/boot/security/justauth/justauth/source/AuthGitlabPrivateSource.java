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

package com.kuma.boot.security.justauth.justauth.source;

import com.kuma.boot.security.justauth.justauth.request.AuthCustomizeRequest;
import me.zhyd.oauth.config.AuthSource;

/**
 * 抽象类, 实现此自定义的 {@link AuthGitlabPrivateSource} 且注入 ioc 容器的同时, 必须实现  {@link AuthCustomizeRequest} ,
 * 会自动集成进 OAuth2 Login 逻辑流程中,
 * 只需要像 JustAuth 默认实现的第三方登录一样, 配置相应的属性(ums.oauth.gitlabPrivate.[clientId|clientSecret]等属性)即可.
 *
 * @author YongWu zheng
 * @version V2.0  Created by 2020.11.29 13:14
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class AuthGitlabPrivateSource implements AuthSource {

    /**
     * 获取自定义 {@link AuthSource} 的字符串名字
     *
     * @return name
     */
    @Override
    public final String getName() {
        return "GITLAB_PRIVATE";
    }

    /**
     * 获取 {@link AuthCustomizeRequest} 的实现类的 Class.
     *
     * @return 返回 {@link AuthCustomizeRequest} 的实现类的 Class
     */
    public abstract Class<? extends AuthCustomizeRequest> getCustomizeRequestClass();

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AuthGitlabPrivateSource)) {
            return false;
        }
        return this.getName().equals(((AuthGitlabPrivateSource) obj).getName());
    }
}
