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

package com.kuma.boot.security.spring.authentication.response.denied;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * <p>访问拒绝处理器 未授权异常
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:08:07
 */
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 处理
     *
     * @param request               请求
     * @param response              响应
     * @param accessDeniedException 拒绝访问异常
     * @since 2023-07-04 10:08:07
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        LogUtils.error("用户权限不足", accessDeniedException);
        ResponseUtils.fail(response, ResultEnum.FORBIDDEN);
    }
}
