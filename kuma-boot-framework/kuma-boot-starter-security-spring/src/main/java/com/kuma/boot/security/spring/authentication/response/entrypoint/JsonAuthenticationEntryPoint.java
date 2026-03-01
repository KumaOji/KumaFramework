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

package com.kuma.boot.security.spring.authentication.response.entrypoint;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * <p>自定义未认证处理  未登录异常
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:08:10
 */
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 开始
     *
     * @param request       请求
     * @param response      响应
     * @param authException 身份验证异常
     * @since 2023-07-04 10:08:10
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        LogUtils.error("用户未登录认证失败", authException);
        ResponseUtils.fail(response, ResultEnum.UNAUTHORIZED);
    }
}
