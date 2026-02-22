/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.MediaType
 *  org.springframework.http.RequestEntity
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 *  org.springframework.security.oauth2.core.ClientAuthenticationMethod
 *  org.springframework.security.oauth2.core.OAuth2AuthenticationException
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest$Builder
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public enum SocialClientProviders {
    WECHAT_WEB_LOGIN_CLIENT("wechat-web-login", SocialClientProviders::oAuth2AuthorizationRequestConsumer, authorizationCodeGrantRequest -> {
        ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
        HttpHeaders headers = SocialClientProviders.getTokenRequestHeaders(clientRegistration);
        OAuth2AuthorizationExchange authorizationExchange = authorizationCodeGrantRequest.getAuthorizationExchange();
        LinkedMultiValueMap queryParameters = new LinkedMultiValueMap();
        queryParameters.add((Object)"grant_type", (Object)authorizationCodeGrantRequest.getGrantType().getValue());
        queryParameters.add((Object)"code", (Object)authorizationExchange.getAuthorizationResponse().getCode());
        queryParameters.add((Object)"appid", (Object)clientRegistration.getClientId());
        queryParameters.add((Object)"secret", (Object)clientRegistration.getClientSecret());
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();
        URI uri = UriComponentsBuilder.fromUriString((String)tokenUri).queryParams((MultiValueMap)queryParameters).build().toUri();
        return RequestEntity.get((URI)uri).headers(headers).build();
    }),
    WECHAT_WEB_CLIENT("wechat-web", SocialClientProviders::oAuth2AuthorizationRequestConsumer, authorizationCodeGrantRequest -> {
        ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
        HttpHeaders headers = SocialClientProviders.getTokenRequestHeaders(clientRegistration);
        OAuth2AuthorizationExchange authorizationExchange = authorizationCodeGrantRequest.getAuthorizationExchange();
        LinkedMultiValueMap queryParameters = new LinkedMultiValueMap();
        queryParameters.add((Object)"appid", (Object)clientRegistration.getClientId());
        String redirectUri = authorizationExchange.getAuthorizationRequest().getRedirectUri();
        if (redirectUri != null) {
            queryParameters.add((Object)"redirect_uri", (Object)redirectUri);
        }
        queryParameters.add((Object)"grant_type", (Object)authorizationCodeGrantRequest.getGrantType().getValue());
        queryParameters.add((Object)"code", (Object)authorizationExchange.getAuthorizationResponse().getCode());
        queryParameters.add((Object)"secret", (Object)clientRegistration.getClientSecret());
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();
        URI uri = UriComponentsBuilder.fromUriString((String)tokenUri).queryParams((MultiValueMap)queryParameters).build().toUri();
        return RequestEntity.get((URI)uri).headers(headers).build();
    }),
    WORK_WECHAT_SCAN_CLIENT("work-wechat-scan", builder -> builder.attributes(attributes -> builder.parameters(parameters -> {
        LinkedHashMap linkedParameters = new LinkedHashMap();
        parameters.forEach((k, v) -> {
            if ("client_id".equals(k)) {
                linkedParameters.put("appid", v);
            }
            if ("redirect_uri".equals(k)) {
                linkedParameters.put("redirect_uri", v);
            }
            if ("state".equals(k)) {
                linkedParameters.put("state", v);
            }
            if ("scope".equals(k)) {
                linkedParameters.put("agentid", v);
            }
        });
        parameters.clear();
        parameters.putAll(linkedParameters);
    })), authorizationCodeGrantRequest -> {
        String code = authorizationCodeGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode();
        if (code == null) {
            throw new OAuth2AuthenticationException("\u7528\u6237\u7ec8\u6b62\u6388\u6743");
        }
        ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
        LinkedMultiValueMap queryParameters = new LinkedMultiValueMap();
        queryParameters.add((Object)"corpid", (Object)clientRegistration.getClientId());
        queryParameters.add((Object)"corpsecret", (Object)clientRegistration.getClientSecret());
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();
        URI uri = UriComponentsBuilder.fromUriString((String)tokenUri).queryParams((MultiValueMap)queryParameters).build().toUri();
        return RequestEntity.get((URI)uri).build();
    });

    private final String registrationId;
    private final Consumer<OAuth2AuthorizationRequest.Builder> oAuth2AuthorizationRequestConsumer;
    private final Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> tokenUriConverter;

    private SocialClientProviders(String registrationId, Consumer<OAuth2AuthorizationRequest.Builder> oAuth2AuthorizationRequestConsumer, Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> tokenUriConverter) {
        this.registrationId = registrationId;
        this.oAuth2AuthorizationRequestConsumer = oAuth2AuthorizationRequestConsumer;
        this.tokenUriConverter = tokenUriConverter;
    }

    public String registrationId() {
        return this.registrationId;
    }

    public Consumer<OAuth2AuthorizationRequest.Builder> requestConsumer() {
        return this.oAuth2AuthorizationRequestConsumer;
    }

    public Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> converter() {
        return this.tokenUriConverter;
    }

    static HttpHeaders getTokenRequestHeaders(ClientRegistration clientRegistration) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.valueOf((String)"application/json;charset=UTF-8")));
        MediaType contentType = MediaType.valueOf((String)"application/x-www-form-urlencoded;charset=UTF-8");
        headers.setContentType(contentType);
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals((Object)clientRegistration.getClientAuthenticationMethod())) {
            String clientId = SocialClientProviders.encodeClientCredential(clientRegistration.getClientId());
            String clientSecret = SocialClientProviders.encodeClientCredential(clientRegistration.getClientSecret());
            headers.setBasicAuth(clientId, clientSecret);
        }
        return headers;
    }

    private static String encodeClientCredential(String clientCredential) {
        return URLEncoder.encode(clientCredential, StandardCharsets.UTF_8);
    }

    private static void oAuth2AuthorizationRequestConsumer(OAuth2AuthorizationRequest.Builder builder) {
        builder.attributes(attributes -> builder.parameters(parameters -> {
            LinkedHashMap linkedParameters = new LinkedHashMap();
            parameters.forEach((k, v) -> {
                if ("client_id".equals(k)) {
                    linkedParameters.put("appid", v);
                } else {
                    linkedParameters.put(k, v);
                }
            });
            parameters.clear();
            parameters.putAll(linkedParameters);
            builder.authorizationRequestUri(uriBuilder -> uriBuilder.fragment("wechat_redirect").build(new Object[0]));
        }));
    }
}

