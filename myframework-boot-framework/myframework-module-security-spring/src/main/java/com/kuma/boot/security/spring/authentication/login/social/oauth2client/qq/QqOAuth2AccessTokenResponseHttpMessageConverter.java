/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 *  org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
 *  org.springframework.util.StreamUtils
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.qq;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.util.StreamUtils;

public class QqOAuth2AccessTokenResponseHttpMessageConverter
extends OAuth2AccessTokenResponseHttpMessageConverter {
    public QqOAuth2AccessTokenResponseHttpMessageConverter(MediaType ... mediaType) {
        this.setSupportedMediaTypes(Arrays.asList(mediaType));
    }

    protected OAuth2AccessTokenResponse readInternal(Class<? extends OAuth2AccessTokenResponse> clazz, HttpInputMessage inputMessage) {
        String response = null;
        try {
            response = StreamUtils.copyToString((InputStream)inputMessage.getBody(), (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            LogUtils.error((Throwable)e);
        }
        LogUtils.info((String)"qq\u7684AccessToken\u54cd\u5e94\u4fe1\u606f\uff1a{}", (Object[])new Object[]{response});
        Map<String, Object> tokenResponseParameters = Arrays.stream(response.split("&")).collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        tokenResponseParameters.put("token_type", "bearer");
        return new DefaultMapOAuth2AccessTokenResponseConverter().convert(tokenResponseParameters);
    }

    protected void writeInternal(OAuth2AccessTokenResponse tokenResponse, HttpOutputMessage outputMessage) {
        throw new UnsupportedOperationException();
    }
}

