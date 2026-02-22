/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.MediaType
 *  org.springframework.http.RequestEntity
 *  org.springframework.http.ResponseEntity
 *  org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
 *  org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException
 *  org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal
 *  org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException
 *  org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.security.spring.oauth2.introspector;

import com.kuma.boot.security.spring.core.authority.TtcGrantedAuthority;
import com.kuma.boot.security.spring.properties.OAuth2EndpointProperties;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class SecurityOpaqueTokenIntrospector
implements OpaqueTokenIntrospector {
    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<Map<String, Object>>(){};
    private final Log logger = LogFactory.getLog(this.getClass());
    private final RestOperations restOperations;
    private Converter<String, RequestEntity<?>> requestEntityConverter;

    public SecurityOpaqueTokenIntrospector(OAuth2EndpointProperties OAuth2EndpointProperties2, OAuth2ResourceServerProperties resourceServerProperties) {
        this(SecurityOpaqueTokenIntrospector.getIntrospectionUri(OAuth2EndpointProperties2, resourceServerProperties), resourceServerProperties.getOpaquetoken().getClientId(), resourceServerProperties.getOpaquetoken().getClientSecret());
    }

    public SecurityOpaqueTokenIntrospector(String introspectionUri, String clientId, String clientSecret) {
        Assert.notNull((Object)introspectionUri, (String)"introspectionUri cannot be null");
        this.requestEntityConverter = this.defaultRequestEntityConverter(URI.create(introspectionUri));
        RestTemplate restTemplate = new RestTemplate();
        this.restOperations = restTemplate;
    }

    public SecurityOpaqueTokenIntrospector(String introspectionUri, RestOperations restOperations) {
        Assert.notNull((Object)introspectionUri, (String)"introspectionUri cannot be null");
        Assert.notNull((Object)restOperations, (String)"restOperations cannot be null");
        this.requestEntityConverter = this.defaultRequestEntityConverter(URI.create(introspectionUri));
        this.restOperations = restOperations;
    }

    private static String getIntrospectionUri(OAuth2EndpointProperties OAuth2EndpointProperties2, OAuth2ResourceServerProperties resourceServerProperties) {
        String introspectionUri = OAuth2EndpointProperties2.getTokenIntrospectionUri();
        String configIntrospectionUri = resourceServerProperties.getOpaquetoken().getIntrospectionUri();
        if (StringUtils.isNotBlank((CharSequence)configIntrospectionUri)) {
            introspectionUri = configIntrospectionUri;
        }
        return introspectionUri;
    }

    private Converter<String, RequestEntity<?>> defaultRequestEntityConverter(URI introspectionUri) {
        return token -> {
            HttpHeaders headers = this.requestHeaders();
            MultiValueMap<String, String> body = this.requestBody((String)token);
            return new RequestEntity(body, (MultiValueMap)headers, HttpMethod.POST, introspectionUri);
        };
    }

    private HttpHeaders requestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private MultiValueMap<String, String> requestBody(String token) {
        LinkedMultiValueMap body = new LinkedMultiValueMap();
        body.add((Object)"token", (Object)token);
        return body;
    }

    public OAuth2AuthenticatedPrincipal introspect(String token) {
        RequestEntity requestEntity = (RequestEntity)this.requestEntityConverter.convert((Object)token);
        if (requestEntity == null) {
            throw new OAuth2IntrospectionException("requestEntityConverter returned a null entity");
        }
        ResponseEntity<Map<String, Object>> responseEntity = this.makeRequest(requestEntity);
        Map<String, Object> claims = this.adaptToNimbusResponse(responseEntity);
        return this.convertClaimsSet(claims);
    }

    public void setRequestEntityConverter(Converter<String, RequestEntity<?>> requestEntityConverter) {
        Assert.notNull(requestEntityConverter, (String)"requestEntityConverter cannot be null");
        this.requestEntityConverter = requestEntityConverter;
    }

    private ResponseEntity<Map<String, Object>> makeRequest(RequestEntity<?> requestEntity) {
        try {
            return this.restOperations.exchange(requestEntity, STRING_OBJECT_MAP);
        }
        catch (Exception ex) {
            throw new OAuth2IntrospectionException(ex.getMessage(), (Throwable)ex);
        }
    }

    private Map<String, Object> adaptToNimbusResponse(ResponseEntity<Map<String, Object>> responseEntity) {
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new OAuth2IntrospectionException("Introspection endpoint responded with " + String.valueOf(responseEntity.getStatusCode()));
        }
        Map claims = (Map)responseEntity.getBody();
        if (claims == null) {
            return Collections.emptyMap();
        }
        boolean active = (Boolean)claims.compute("active", (k, v) -> {
            if (v instanceof String) {
                return Boolean.parseBoolean((String)v);
            }
            if (v instanceof Boolean) {
                return v;
            }
            return false;
        });
        if (!active) {
            this.logger.trace((Object)"Did not validate token since it is inactive");
            throw new BadOpaqueTokenException("Provided token isn't active");
        }
        return claims;
    }

    private OAuth2AuthenticatedPrincipal convertClaimsSet(Map<String, Object> claims) {
        claims.computeIfPresent("aud", (k, v) -> {
            if (v instanceof String) {
                return Collections.singletonList(v);
            }
            return v;
        });
        claims.computeIfPresent("client_id", (k, v) -> v.toString());
        claims.computeIfPresent("exp", (k, v) -> Instant.ofEpochSecond(((Number)v).longValue()));
        claims.computeIfPresent("iat", (k, v) -> Instant.ofEpochSecond(((Number)v).longValue()));
        claims.computeIfPresent("iss", (k, v) -> v.toString());
        claims.computeIfPresent("nbf", (k, v) -> Instant.ofEpochSecond(((Number)v).longValue()));
        ArrayList authorities = new ArrayList();
        claims.computeIfPresent("scope", (k, v) -> v.toString());
        claims.computeIfPresent("authorities", (k, v) -> {
            if (v instanceof ArrayList) {
                List values = (List)v;
                values.forEach(value -> authorities.add(new TtcGrantedAuthority((String)value)));
            }
            return v;
        });
        return new OAuth2IntrospectionAuthenticatedPrincipal(claims, authorities);
    }
}

