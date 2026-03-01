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

package com.kuma.boot.security.spring.oauth2;

import com.kuma.boot.security.spring.constants.BaseConstants;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * <p>自定义 Grant Type 类型
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:02:47
 */
public interface KmcAuthorizationGrantType {

    /**
     * 社会
     */
    AuthorizationGrantType SOCIAL = new AuthorizationGrantType(BaseConstants.SOCIAL_CREDENTIALS);

    /**
     * 密码
     */
    AuthorizationGrantType PASSWORD = new AuthorizationGrantType(BaseConstants.PASSWORD);
}
