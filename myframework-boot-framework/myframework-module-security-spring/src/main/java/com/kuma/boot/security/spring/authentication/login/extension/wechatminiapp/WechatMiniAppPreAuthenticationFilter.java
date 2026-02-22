/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.kuma.boot.common.model.Result
 *  com.kuma.boot.common.utils.servlet.ResponseUtils
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.authentication.BadCredentialsException
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 *  org.springframework.web.filter.OncePerRequestFilter
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuma.boot.common.model.Result;
import com.kuma.boot.common.utils.servlet.ResponseUtils;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatLoginResponse;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client.WechatMiniAppClient;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.WechatMiniAppClientService;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.service.WechatMiniAppSessionKeyCacheService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

public class WechatMiniAppPreAuthenticationFilter
extends OncePerRequestFilter {
    private static final String ENDPOINT = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String MINI_CLIENT_KEY = "clientId";
    private static final String JS_CODE_KEY = "jsCode";
    private final RequestMatcher requiresAuthenticationRequestMatcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/wechat/miniapp/preauth");
    private final ObjectMapper om = new ObjectMapper();
    private final WechatMiniAppClientService wechatMiniAppClientService;
    private final WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService;
    private final RestOperations restOperations;

    public WechatMiniAppPreAuthenticationFilter(WechatMiniAppClientService wechatMiniAppClientService, WechatMiniAppSessionKeyCacheService wechatMiniAppSessionKeyCacheService) {
        this.wechatMiniAppClientService = wechatMiniAppClientService;
        this.wechatMiniAppSessionKeyCacheService = wechatMiniAppSessionKeyCacheService;
        this.restOperations = new RestTemplate();
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (response.isCommitted()) {
            return;
        }
        if (this.requiresAuthenticationRequestMatcher.matches(request)) {
            String clientId = request.getParameter(MINI_CLIENT_KEY);
            String jsCode = request.getParameter(JS_CODE_KEY);
            WechatMiniAppClient wechatMiniAppClient = this.wechatMiniAppClientService.get(clientId);
            WechatLoginResponse responseEntity = this.getResponse(wechatMiniAppClient, jsCode);
            String openId = responseEntity.getOpenid();
            String sessionKey = responseEntity.getSessionKey();
            this.wechatMiniAppSessionKeyCacheService.put(clientId + "::" + openId, sessionKey);
            responseEntity.setSessionKey(null);
            ResponseUtils.success((HttpServletResponse)response, (Object)Result.success((Object)responseEntity));
            return;
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private WechatLoginResponse getResponse(WechatMiniAppClient wechatMiniAppClient, String jsCode) throws JsonProcessingException {
        LinkedMultiValueMap queryParams = new LinkedMultiValueMap();
        queryParams.add((Object)"appid", (Object)wechatMiniAppClient.getAppId());
        queryParams.add((Object)"secret", (Object)wechatMiniAppClient.getSecret());
        queryParams.add((Object)"js_code", (Object)jsCode);
        queryParams.add((Object)"grant_type", (Object)"authorization_code");
        URI uri = UriComponentsBuilder.fromUriString((String)ENDPOINT).queryParams((MultiValueMap)queryParams).build().toUri();
        String response = (String)this.restOperations.getForObject(uri, String.class);
        if (Objects.isNull(response)) {
            throw new BadCredentialsException("miniapp response is null");
        }
        return (WechatLoginResponse)this.om.readValue(response, WechatLoginResponse.class);
    }
}

