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

package com.kuma.boot.security.spring.authentication.login.form;

import com.kuma.boot.security.spring.utils.SymmetricUtils;
import com.kuma.boot.security.spring.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * <p>表单登录 Details 定义
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:02:54
 */
public class FormLoginWebAuthenticationDetails extends WebAuthenticationDetails {

    /**
     * 验证码是否关闭
     */
    private final Boolean closed;

    /**
     * 请求中，验证码对应的表单参数名。对应UI Properties 里面的 captcha parameter
     */
    private final String parameterName;

    /**
     * 验证码分类
     */
    private final String category;

    /**
     * 代码
     */
    private String code = null;

    /**
     * 身份
     */
    private String identity = null;

    /**
     * 表单登录web身份验证细节
     *
     * @param remoteAddress 远程地址
     * @param sessionId     会话id
     * @param closed        关闭
     * @param parameterName 参数名称
     * @param category      类别
     * @param code          代码
     * @param identity      身份
     * @since 2023-07-04 10:02:55
     */
    public FormLoginWebAuthenticationDetails(
            String remoteAddress,
            String sessionId,
            Boolean closed,
            String parameterName,
            String category,
            String code,
            String identity) {
        super(remoteAddress, sessionId);
        this.closed = closed;
        this.parameterName = parameterName;
        this.category = category;
        this.code = code;
        this.identity = identity;
    }

    /**
     * 表单登录web身份验证细节
     *
     * @param request       请求
     * @param closed        关闭
     * @param parameterName 参数名称
     * @param category      类别
     * @since 2023-07-04 10:02:55
     */
    public FormLoginWebAuthenticationDetails(
            HttpServletRequest request, boolean closed, String parameterName, String category) {
        super(request);
        this.closed = closed;
        this.parameterName = parameterName;
        this.category = category;
        this.init(request);
    }

    /**
     * 初始化
     *
     * @param request 请求
     * @since 2023-07-04 10:02:55
     */
    private void init(HttpServletRequest request) {
        String encryptedCode = request.getParameter(parameterName);
        String key = request.getParameter("symmetric");

        HttpSession session = WebUtils.getSession(request);
        this.identity = session.getId();

        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(encryptedCode)) {
            byte[] byteKey = SymmetricUtils.getDecryptedSymmetricKey(key);
            this.code = SymmetricUtils.decrypt(encryptedCode, byteKey);
        }
    }

    /**
     * 而被关闭
     *
     * @return {@link Boolean }
     * @since 2023-07-04 10:02:55
     */
    public Boolean getClosed() {
        return closed;
    }

    /**
     * 得到参数名称
     *
     * @return {@link String }
     * @since 2023-07-04 10:02:55
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * 得到类别
     *
     * @return {@link String }
     * @since 2023-07-04 10:02:55
     */
    public String getCategory() {
        return category;
    }

    /**
     * 获取代码
     *
     * @return {@link String }
     * @since 2023-07-04 10:02:55
     */
    public String getCode() {
        return code;
    }

    /**
     * 获得身份
     *
     * @return {@link String }
     * @since 2023-07-04 10:02:55
     */
    public String getIdentity() {
        return identity;
    }
}
