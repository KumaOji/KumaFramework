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

package com.kuma.boot.security.spring.authentication.login.extension.mfa.convert;

import com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.SmartHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.util.StringUtils;

/**
 * @author: ReLive27
 * @since: 2023/1/13 19:39
 */
public class MfaAuthenticationHttpMessageConverter
        extends AbstractHttpMessageConverter<MfaAuthenticationResponse> {
    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP =
            new ParameterizedTypeReference<Map<String, Object>>() {};

    private final SmartHttpMessageConverter<Object> jsonMessageConverter =
            new JacksonJsonHttpMessageConverter();
    private Converter<MfaAuthenticationResponse, Map<String, Object>> converter =
            new MfaAuthenticationResponseMapConverter();

    @Override
    protected boolean supports(Class<?> clazz) {
        return MfaAuthenticationResponse.class.isAssignableFrom(clazz);
    }

    @Override
    protected MfaAuthenticationResponse readInternal(
            Class<? extends MfaAuthenticationResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new HttpMessageNotReadableException(
                "Result is empty when reading MfaAuthenticationResponse", inputMessage);
    }

    @Override
    protected void writeInternal(
            MfaAuthenticationResponse mfaAuthenticationResponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        try {
            Map<String, Object> mfaResponseParameters =
                    this.converter.convert(mfaAuthenticationResponse);
            this.jsonMessageConverter.write(
                    (Object) mfaResponseParameters,
                    (ResolvableType) STRING_OBJECT_MAP.getType(),
                    MediaType.APPLICATION_JSON,
                    outputMessage,
                    null);
        } catch (Exception ex) {
            throw new HttpMessageNotWritableException(
                    "An error occurred writing the MFA login response: " + ex.getMessage(), ex);
        }
    }

    private static final class MfaAuthenticationResponseMapConverter
            implements Converter<MfaAuthenticationResponse, Map<String, Object>> {

        @Override
        public Map<String, Object> convert(MfaAuthenticationResponse source) {
            Map<String, Object> responseClaims = new LinkedHashMap<>();
            responseClaims.put("code", source.getResponseCode());
            responseClaims.put("message", source.getMessage());
            Map<String, Object> data = new HashMap<>();
            data.put("mfa", source.getMfa());
            if (StringUtils.hasText(source.getQrCode())) {
                data.put("qrCode", source.getQrCode());
            }
            if (StringUtils.hasText(source.getToken())) {
                data.put("token", source.getToken());
            }
            responseClaims.put("data", data);
            return responseClaims;
        }
    }
}
