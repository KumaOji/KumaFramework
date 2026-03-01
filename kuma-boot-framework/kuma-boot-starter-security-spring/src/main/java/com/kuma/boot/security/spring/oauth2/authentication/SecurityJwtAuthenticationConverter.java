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

package com.kuma.boot.security.spring.oauth2.authentication;

import com.kuma.boot.security.spring.constants.BaseConstants;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * <p>扩展的 JwtAuthenticationConverter
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 09:57:42
 */
public class SecurityJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public SecurityJwtAuthenticationConverter() {
        SecurityJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
                new SecurityJwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName(BaseConstants.AUTHORITIES);

        this.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    }
}
