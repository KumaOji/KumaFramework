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

package com.kuma.boot.security.spring.authentication.login.extension.sms.service;

import com.kuma.boot.common.enums.LoginTypeEnum;
import com.kuma.boot.security.spring.core.userdetails.KmcUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultSmsUserDetailsService implements SmsUserDetailsService {
    //
    //    @Autowired
    //    private IFeignUserApi userApi;
    //
    //    @Autowired
    //    private IFeignMemberApi memberApi;

    @Override
    public UserDetails loadUserByPhone(String phone, String type) throws UsernameNotFoundException {

        if (LoginTypeEnum.B_PC_PHONE.getType().equals(type)) {}

        if (LoginTypeEnum.C_PC_PHONE.getType().equals(type)) {}

        if (LoginTypeEnum.C_MIMI_PHONE.getType().equals(type)) {}

        if (LoginTypeEnum.C_APP_PHONE.getType().equals(type)) {}

        return new KmcUser();
    }
}
