/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.AbstractHttpMessageConverter
 *  org.springframework.http.converter.GenericHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageNotReadableException
 *  org.springframework.http.converter.HttpMessageNotWritableException
 *  org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.convert;

import com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;

public class MfaAuthenticationHttpMessageConverter
extends AbstractHttpMessageConverter<MfaAuthenticationResponse> {
    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<Map<String, Object>>(){};
    private final GenericHttpMessageConverter<Object> jsonMessageConverter = new MappingJackson2HttpMessageConverter();
    private Converter<MfaAuthenticationResponse, Map<String, Object>> converter = new MfaAuthenticationResponseMapConverter();

    protected boolean supports(Class<?> clazz) {
        return MfaAuthenticationResponse.class.isAssignableFrom(clazz);
    }

    protected MfaAuthenticationResponse readInternal(Class<? extends MfaAuthenticationResponse> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new HttpMessageNotReadableException("Result is empty when reading MfaAuthenticationResponse", inputMessage);
    }

    protected void writeInternal(MfaAuthenticationResponse mfaAuthenticationResponse, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            Map mfaResponseParameters = (Map)this.converter.convert((Object)mfaAuthenticationResponse);
            this.jsonMessageConverter.write((Object)mfaResponseParameters, STRING_OBJECT_MAP.getType(), MediaType.APPLICATION_JSON, outputMessage);
        }
        catch (Exception ex) {
            throw new HttpMessageNotWritableException("An error occurred writing the MFA login response: " + ex.getMessage(), (Throwable)ex);
        }
    }

    private static final class MfaAuthenticationResponseMapConverter
    implements Converter<MfaAuthenticationResponse, Map<String, Object>> {
        private MfaAuthenticationResponseMapConverter() {
        }

        public Map<String, Object> convert(MfaAuthenticationResponse source) {
            LinkedHashMap<String, Object> responseClaims = new LinkedHashMap<String, Object>();
            responseClaims.put("code", source.getResponseCode());
            responseClaims.put("message", source.getMessage());
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("mfa", source.getMfa());
            if (StringUtils.hasText((String)source.getQrCode())) {
                data.put("qrCode", source.getQrCode());
            }
            if (StringUtils.hasText((String)source.getToken())) {
                data.put("token", source.getToken());
            }
            responseClaims.put("data", data);
            return responseClaims;
        }
    }
}

