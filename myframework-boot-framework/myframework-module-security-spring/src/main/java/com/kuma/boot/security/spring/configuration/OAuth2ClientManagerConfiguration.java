/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
 *  org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.FormHttpMessageConverter
 *  org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
 *  org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
 *  org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient
 *  org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
 *  org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
 *  org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
 *  org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
 *  org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
 *  org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
 *  org.springframework.web.client.ResponseErrorHandler
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.security.spring.authentication.login.social.oauth2client.SocialDelegateClientRegistrationRepository;
import com.kuma.boot.security.spring.authentication.login.social.oauth2client.SocialDelegateMapOAuth2AccessTokenResponseConverter;
import com.kuma.boot.security.spring.authentication.login.social.oauth2client.SocialDelegateOAuth2RefreshTokenRequestEntityConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods=false)
public class OAuth2ClientManagerConfiguration {
    @Bean
    public SocialDelegateClientRegistrationRepository delegateClientRegistrationRepository(OAuth2ClientProperties properties) {
        SocialDelegateClientRegistrationRepository clientRegistrationRepository = new SocialDelegateClientRegistrationRepository();
        if (properties != null) {
            Map clientRegistrations = new OAuth2ClientPropertiesMapper(properties).asClientRegistrations();
            ArrayList registrations = new ArrayList(clientRegistrations.values());
            registrations.forEach(clientRegistrationRepository::addClientRegistration);
        }
        return clientRegistrationRepository;
    }

    @Bean
    OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {
        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        DefaultRefreshTokenTokenResponseClient defaultRefreshTokenTokenResponseClient = new DefaultRefreshTokenTokenResponseClient();
        defaultRefreshTokenTokenResponseClient.setRequestEntityConverter((Converter)new SocialDelegateOAuth2RefreshTokenRequestEntityConverter());
        OAuth2AccessTokenResponseHttpMessageConverter messageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        messageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, new MediaType("application", "*+json")));
        messageConverter.setAccessTokenResponseConverter((Converter)new SocialDelegateMapOAuth2AccessTokenResponseConverter());
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), messageConverter));
        restTemplate.setErrorHandler((ResponseErrorHandler)new OAuth2ErrorResponseErrorHandler());
        defaultRefreshTokenTokenResponseClient.setRestOperations((RestOperations)restTemplate);
        authorizedClientManager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder().authorizationCode().refreshToken(refreshTokenGrantBuilder -> refreshTokenGrantBuilder.accessTokenResponseClient((OAuth2AccessTokenResponseClient)defaultRefreshTokenTokenResponseClient)).clientCredentials().build());
        return authorizedClientManager;
    }
}

