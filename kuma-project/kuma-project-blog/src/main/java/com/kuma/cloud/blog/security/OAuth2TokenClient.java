package com.kuma.cloud.blog.security;

import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Component
public class OAuth2TokenClient {

    private static final ParameterizedTypeReference<Map<String, Object>> TOKEN_RESPONSE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient = RestClient.create();
    private final ClientRegistrationRepository registrations;
    private final OAuth2EndpointProperties endpointProperties;
    private final String registrationId;

    public OAuth2TokenClient(
            ClientRegistrationRepository registrations,
            OAuth2EndpointProperties endpointProperties,
            @Value("${blog.oauth2.registration-id:blog}") String registrationId) {
        this.registrations = registrations;
        this.endpointProperties = endpointProperties;
        this.registrationId = registrationId;
    }

    public TokenResponse refresh(String refreshToken) {
        ClientRegistration registration = registration();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);
        addPostCredentialsIfNeeded(registration, form);

        Map<String, Object> body = request(registration, registration.getProviderDetails().getTokenUri(), form);
        String accessToken = requiredString(body, "access_token");
        String rotatedRefreshToken = string(body, "refresh_token");
        Number expiresIn = (Number) body.getOrDefault("expires_in", 300);
        return new TokenResponse(
                accessToken,
                rotatedRefreshToken == null ? refreshToken : rotatedRefreshToken,
                Instant.now().plusSeconds(expiresIn.longValue()));
    }

    public void revoke(String token, String tokenTypeHint) {
        ClientRegistration registration = registration();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("token", token);
        form.add("token_type_hint", tokenTypeHint);
        addPostCredentialsIfNeeded(registration, form);
        request(registration, endpointProperties.getTokenRevocationUri(), form);
    }

    private Map<String, Object> request(
            ClientRegistration registration, String uri, MultiValueMap<String, String> form) {
        RestClient.RequestBodySpec request = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (!ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(
                registration.getClientAuthenticationMethod())) {
            request.headers(headers -> headers.setBasicAuth(
                    registration.getClientId(), registration.getClientSecret()));
        }
        Map<String, Object> body = request.body(form).retrieve().body(TOKEN_RESPONSE);
        return body == null ? Map.of() : body;
    }

    private void addPostCredentialsIfNeeded(
            ClientRegistration registration, MultiValueMap<String, String> form) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(
                registration.getClientAuthenticationMethod())) {
            form.add("client_id", registration.getClientId());
            form.add("client_secret", registration.getClientSecret());
        }
    }

    private ClientRegistration registration() {
        ClientRegistration registration = registrations.findByRegistrationId(registrationId);
        if (registration == null) {
            throw new IllegalStateException("找不到 OAuth2 ClientRegistration: " + registrationId);
        }
        return registration;
    }

    private String requiredString(Map<String, Object> body, String name) {
        String value = string(body, name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("UAA Token 响应缺少 " + name);
        }
        return value;
    }

    private String string(Map<String, Object> body, String name) {
        Object value = body.get(name);
        return value == null ? null : value.toString();
    }

    public record TokenResponse(String accessToken, String refreshToken, Instant expiresAt) {}
}
