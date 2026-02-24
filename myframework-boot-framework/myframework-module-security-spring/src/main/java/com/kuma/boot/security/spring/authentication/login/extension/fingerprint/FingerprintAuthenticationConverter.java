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

package com.kuma.boot.security.spring.authentication.login.extension.fingerprint;

import static com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter.KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_FINGER_PRINT_URL;
import static com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter.KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_NAME_URL;

import com.kuma.boot.security.spring.utils.ExtensionLoginUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.MultiValueMap;

/**
 * 帐户验证转换器
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 13:07:23
 */
public class FingerprintAuthenticationConverter
        implements Converter<HttpServletRequest, FingerprintAuthenticationToken> {

    private String usernameParameter = KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_NAME_URL;
    private String fingerPrintParameter =
            KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_FINGER_PRINT_URL;

    @Override
    public FingerprintAuthenticationToken convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = ExtensionLoginUtils.getParameters(request);

        // username (REQUIRED)
        ExtensionLoginUtils.checkRequiredParameter(parameters, usernameParameter);
        // password (REQUIRED)
        ExtensionLoginUtils.checkRequiredParameter(parameters, fingerPrintParameter);

        String username = request.getParameter(this.usernameParameter);
        String fingerPrint = request.getParameter(this.fingerPrintParameter);

        return FingerprintAuthenticationToken.unauthenticated(username, fingerPrint);
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getFingerPrintParameter() {
        return fingerPrintParameter;
    }

    public void setFingerPrintParameter(String fingerPrintParameter) {
        this.fingerPrintParameter = fingerPrintParameter;
    }
}
