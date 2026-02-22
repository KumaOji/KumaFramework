/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.FormHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.config.annotation.web.builders.HttpSecurity
 *  org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
 *  org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
 *  org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
 *  org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 *  org.springframework.security.oauth2.client.registration.ClientRegistration$Builder
 *  org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
 *  org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
 *  org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
 *  org.springframework.security.oauth2.core.AuthorizationGrantType
 *  org.springframework.security.oauth2.core.ClientAuthenticationMethod
 *  org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
 *  org.springframework.security.web.authentication.AuthenticationFailureHandler
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 *  org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter
 *  org.springframework.util.Assert
 *  org.springframework.web.client.ResponseErrorHandler
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import com.kuma.boot.security.spring.authentication.login.social.oauth2client.gitee.GiteeOAuth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.oauth2client.github.GithubOAuth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.oauth2client.qq.QQOauth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.oauth2client.qq.QqOAuth2AccessTokenResponseHttpMessageConverter;
import com.kuma.boot.security.spring.authentication.login.social.oauth2client.wechat.WechatOAuth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.oauth2client.wechatwork.WorkWechatOAuth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.oauth2client.weibo.WeiboOAuth2UserService;
import com.kuma.boot.security.spring.authentication.response.failure.SocialAuthenticationFailureHandler;
import com.kuma.boot.security.spring.authentication.response.success.SocialAuthenticationSuccessHandler;
import com.kuma.boot.security.spring.oauth2.token.JwtTokenGenerator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class SocialLoginFilterSecurityConfigurer<H extends HttpSecurityBuilder<H>>
extends AbstractHttpConfigurer<SocialLoginFilterSecurityConfigurer<H>, H> {
    private SocialDelegateClientRegistrationRepository socialDelegateClientRegistrationRepository = new SocialDelegateClientRegistrationRepository();
    private Consumer<OAuth2LoginConfigurer<HttpSecurity>> oAuth2LoginConfigurerConsumer = oAuth2ProviderConfigurer -> {};

    public SocialLoginFilterSecurityConfigurer<H> socialDelegateClientRegistrationRepository(SocialDelegateClientRegistrationRepository socialDelegateClientRegistrationRepository) {
        this.socialDelegateClientRegistrationRepository = socialDelegateClientRegistrationRepository;
        return this;
    }

    public SocialLoginFilterSecurityConfigurer<H> wechatWebClient(String appId, String secret) {
        ClientRegistration clientRegistration = this.getBuilder(SocialClientProviders.WECHAT_WEB_CLIENT.registrationId(), ClientAuthenticationMethod.NONE).clientId(appId).clientSecret(secret).scope(new String[]{"snsapi_userinfo"}).authorizationUri("https://open.weixin.qq.com/connect/oauth2/authorize").tokenUri("https://api.weixin.qq.com/sns/oauth2/access_token").userInfoUri("https://api.weixin.qq.com/sns/userinfo").clientName("\u5fae\u4fe1\u7f51\u9875\u6388\u6743").build();
        this.socialDelegateClientRegistrationRepository.addClientRegistration(clientRegistration);
        return this;
    }

    public SocialLoginFilterSecurityConfigurer<H> wechatWebLoginClient(String appId, String secret) {
        ClientRegistration clientRegistration = this.getBuilder(SocialClientProviders.WECHAT_WEB_LOGIN_CLIENT.registrationId(), ClientAuthenticationMethod.NONE).clientId(appId).clientSecret(secret).scope(new String[]{"snsapi_login"}).authorizationUri("https://open.weixin.qq.com/connect/qrconnect").tokenUri("https://api.weixin.qq.com/sns/oauth2/access_token").userInfoUri("https://api.weixin.qq.com/sns/userinfo").clientName("\u5fae\u4fe1\u626b\u7801").build();
        this.socialDelegateClientRegistrationRepository.addClientRegistration(clientRegistration);
        return this;
    }

    public SocialLoginFilterSecurityConfigurer<H> workWechatWebLoginClient(String corpId, String secret, String agentId) {
        ClientRegistration clientRegistration = this.getBuilder(SocialClientProviders.WORK_WECHAT_SCAN_CLIENT.registrationId(), ClientAuthenticationMethod.NONE).clientId(corpId).clientSecret(secret).scope(new String[]{agentId}).authorizationUri("https://open.work.weixin.qq.com/wwopen/sso/qrConnect").tokenUri("https://qyapi.weixin.qq.com/cgi-bin/gettoken").userInfoUri("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo").clientName("\u4f01\u4e1a\u5fae\u4fe1").build();
        this.socialDelegateClientRegistrationRepository.addClientRegistration(clientRegistration);
        return this;
    }

    public SocialLoginFilterSecurityConfigurer<H> oAuth2LoginConfigurerConsumer(Consumer<OAuth2LoginConfigurer<HttpSecurity>> oAuth2LoginConfigurerConsumer) {
        this.oAuth2LoginConfigurerConsumer = oAuth2LoginConfigurerConsumer;
        return this;
    }

    protected final ClientRegistration.Builder getBuilder(String registrationId, ClientAuthenticationMethod method) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId((String)registrationId);
        builder.clientAuthenticationMethod(method);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}");
        return builder;
    }

    public void init(H builder) throws Exception {
        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, new MediaType("application", "*+json")));
        tokenResponseHttpMessageConverter.setAccessTokenResponseConverter((Converter)new SocialDelegateMapOAuth2AccessTokenResponseConverter());
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(new HttpMessageConverter[]{new FormHttpMessageConverter(), tokenResponseHttpMessageConverter, new QqOAuth2AccessTokenResponseHttpMessageConverter(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, new MediaType("application", "*+json"))}));
        restTemplate.setErrorHandler((ResponseErrorHandler)new OAuth2ErrorResponseErrorHandler());
        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        tokenResponseClient.setRequestEntityConverter((Converter)new SocialOAuth2ProviderAuthorizationCodeGrantRequestEntityConverter());
        tokenResponseClient.setRestOperations((RestOperations)restTemplate);
        WechatOAuth2UserService wechatOAuth2UserService = new WechatOAuth2UserService();
        HashMap<String, Object> oAuth2UserServiceMap = new HashMap<String, Object>();
        oAuth2UserServiceMap.put(SocialClientProviders.WECHAT_WEB_CLIENT.registrationId(), wechatOAuth2UserService);
        oAuth2UserServiceMap.put(SocialClientProviders.WECHAT_WEB_LOGIN_CLIENT.registrationId(), wechatOAuth2UserService);
        oAuth2UserServiceMap.put(SocialClientProviders.WORK_WECHAT_SCAN_CLIENT.registrationId(), new WorkWechatOAuth2UserService());
        oAuth2UserServiceMap.put("web", new WeiboOAuth2UserService());
        oAuth2UserServiceMap.put("qq", new QQOauth2UserService());
        oAuth2UserServiceMap.put("gitee", new GiteeOAuth2UserService());
        oAuth2UserServiceMap.put("github", (Object)new GithubOAuth2UserService());
        builder.setSharedObject(ClientRegistrationRepository.class, (Object)this.socialDelegateClientRegistrationRepository);
        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver((ClientRegistrationRepository)this.socialDelegateClientRegistrationRepository, "/oauth2/authorization");
        resolver.setAuthorizationRequestCustomizer(SocialOAuth2AuthorizationRequestCustomizer::customize);
        HttpSecurity httpSecurity = (HttpSecurity)builder;
        httpSecurity.oauth2Login(oauth2LoginCustomizer -> {
            ((OAuth2LoginConfigurer)((OAuth2LoginConfigurer)oauth2LoginCustomizer.successHandler(this.authenticationSuccessHandler(httpSecurity))).failureHandler(this.authenticationFailureHandler(httpSecurity))).authorizationEndpoint(authorizationEndpointCustomizer -> authorizationEndpointCustomizer.authorizationRequestResolver((OAuth2AuthorizationRequestResolver)resolver)).tokenEndpoint(tokenEndpointCustomizer -> tokenEndpointCustomizer.accessTokenResponseClient((OAuth2AccessTokenResponseClient)new SocialDelegateOAuth2AccessTokenResponseClient((OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>)tokenResponseClient, (RestOperations)restTemplate))).userInfoEndpoint(userInfoEndpointCustomizer -> userInfoEndpointCustomizer.userService(new SocialDelegatingOAuth2UserService(oAuth2UserServiceMap)));
            this.oAuth2LoginConfigurerConsumer.accept((OAuth2LoginConfigurer<HttpSecurity>)oauth2LoginCustomizer);
        });
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler(HttpSecurity httpSecurity) {
        ApplicationContext applicationContext = (ApplicationContext)httpSecurity.getSharedObject(ApplicationContext.class);
        JwtTokenGenerator jwtTokenGenerator = (JwtTokenGenerator)applicationContext.getBean(JwtTokenGenerator.class);
        Assert.notNull((Object)jwtTokenGenerator, (String)"jwtTokenGenerator is required");
        return new SocialAuthenticationSuccessHandler(jwtTokenGenerator);
    }

    private AuthenticationFailureHandler authenticationFailureHandler(HttpSecurity httpSecurity) {
        return new SocialAuthenticationFailureHandler();
    }

    public void configure(H builder) throws Exception {
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = (DefaultLoginPageGeneratingFilter)builder.getSharedObject(DefaultLoginPageGeneratingFilter.class);
        if (loginPageGeneratingFilter != null) {
            HashMap loginUrlToClientName = new HashMap();
            this.socialDelegateClientRegistrationRepository.getClientRegistrationMap().forEach((s, v) -> {
                String authorizationRequestUri = "/oauth2/authorization/" + v.getRegistrationId();
                loginUrlToClientName.put(authorizationRequestUri, v.getClientName());
            });
            loginPageGeneratingFilter.setOauth2AuthenticationUrlToClientName(loginUrlToClientName);
        }
    }
}

