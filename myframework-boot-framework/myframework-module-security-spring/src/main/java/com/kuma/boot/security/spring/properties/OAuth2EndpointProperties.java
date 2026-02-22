/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.security.spring.properties;

import com.google.common.base.MoreObjects;
import com.kuma.boot.security.spring.utils.WellFormedUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="kuma.boot.security.oauth2.endpoint")
public class OAuth2EndpointProperties {
    public static final String PREFIX = "kuma.boot.security.oauth2.endpoint";
    private static final Logger log = LoggerFactory.getLogger(OAuth2EndpointProperties.class);
    private String uaaServiceName;
    private String upmsServiceName;
    private String ossServiceName;
    private String gatewayServiceUri;
    private String uaaServiceUri;
    private String upmsServiceUri;
    private String ossServiceUri;
    private String authorizationUri;
    private String authorizationEndpoint = "/oauth2/authorize";
    private String accessTokenUri;
    private String accessTokenEndpoint = "/oauth2/token";
    private String jwkSetUri;
    private String jwkSetEndpoint = "/oauth2/jwks";
    private String tokenRevocationUri;
    private String tokenRevocationEndpoint = "/oauth2/revoke";
    private String tokenIntrospectionUri;
    private String tokenIntrospectionEndpoint = "/oauth2/introspect";
    private String deviceAuthorizationUri;
    private String deviceAuthorizationEndpoint = "/oauth2/device_authorization";
    private String deviceVerificationUri;
    private String deviceVerificationEndpoint = "/oauth2/device_verification";
    private String oidcClientRegistrationUri;
    private String oidcClientRegistrationEndpoint = "/connect/register";
    private String oidcLogoutUri;
    private String oidcLogoutEndpoint = "/connect/logout";
    private String oidcUserInfoUri;
    private String oidcUserInfoEndpoint = "/userinfo";
    private String issuerUri;

    private String getDefaultEndpoint(String endpoint, String pathAuthorizationEndpoint) {
        if (StringUtils.isNotBlank((CharSequence)endpoint)) {
            return endpoint;
        }
        if (StringUtils.isNotBlank((CharSequence)pathAuthorizationEndpoint)) {
            return this.getUaaServiceUri() + pathAuthorizationEndpoint;
        }
        return this.getUaaServiceUri();
    }

    public String getUaaServiceName() {
        return this.uaaServiceName;
    }

    public void setUaaServiceName(String uaaServiceName) {
        this.uaaServiceName = uaaServiceName;
    }

    public String getUpmsServiceName() {
        return this.upmsServiceName;
    }

    public String getOssServiceName() {
        return this.ossServiceName;
    }

    public void setOssServiceName(String ossServiceName) {
        this.ossServiceName = ossServiceName;
    }

    public void setUpmsServiceName(String upmsServiceName) {
        this.upmsServiceName = upmsServiceName;
    }

    public String getGatewayServiceUri() {
        return this.gatewayServiceUri;
    }

    public void setGatewayServiceUri(String gatewayServiceUri) {
        this.gatewayServiceUri = gatewayServiceUri;
    }

    public String getUaaServiceUri() {
        return WellFormedUtils.serviceUri(this.getGatewayServiceUri(), this.uaaServiceUri, this.uaaServiceName, "UAA");
    }

    public void setUaaServiceUri(String uaaServiceUri) {
        this.uaaServiceUri = uaaServiceUri;
    }

    public String getOssServiceUri() {
        return WellFormedUtils.serviceUri(this.getGatewayServiceUri(), this.ossServiceUri, this.ossServiceName, "OSS");
    }

    public void setOssServiceUri(String ossServiceUri) {
        this.ossServiceUri = ossServiceUri;
    }

    public String getUpmsServiceUri() {
        return WellFormedUtils.serviceUri(this.getGatewayServiceUri(), this.upmsServiceUri, this.upmsServiceName, "UPMS");
    }

    public void setUpmsServiceUri(String upmsServiceUri) {
        this.upmsServiceUri = upmsServiceUri;
    }

    public String getAuthorizationUri() {
        return this.getDefaultEndpoint(this.authorizationUri, this.getAuthorizationEndpoint());
    }

    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    public String getAccessTokenUri() {
        return this.getDefaultEndpoint(this.accessTokenUri, this.getAccessTokenEndpoint());
    }

    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }

    public String getJwkSetUri() {
        return this.getDefaultEndpoint(this.jwkSetUri, this.getJwkSetEndpoint());
    }

    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    public String getTokenRevocationUri() {
        return this.getDefaultEndpoint(this.tokenRevocationUri, this.getTokenRevocationEndpoint());
    }

    public void setTokenRevocationUri(String tokenRevocationUri) {
        this.tokenRevocationUri = tokenRevocationUri;
    }

    public String getTokenIntrospectionUri() {
        return this.getDefaultEndpoint(this.tokenIntrospectionUri, this.getTokenIntrospectionEndpoint());
    }

    public void setTokenIntrospectionUri(String tokenIntrospectionUri) {
        this.tokenIntrospectionUri = tokenIntrospectionUri;
    }

    public String getDeviceAuthorizationUri() {
        return this.getDefaultEndpoint(this.deviceAuthorizationUri, this.getDeviceAuthorizationEndpoint());
    }

    public void setDeviceAuthorizationUri(String deviceAuthorizationUri) {
        this.deviceAuthorizationUri = deviceAuthorizationUri;
    }

    public String getDeviceVerificationUri() {
        return this.getDefaultEndpoint(this.deviceVerificationUri, this.getDeviceVerificationEndpoint());
    }

    public void setDeviceVerificationUri(String deviceVerificationUri) {
        this.deviceVerificationUri = deviceVerificationUri;
    }

    public String getOidcClientRegistrationUri() {
        return this.getDefaultEndpoint(this.oidcClientRegistrationUri, this.getOidcClientRegistrationEndpoint());
    }

    public void setOidcClientRegistrationUri(String oidcClientRegistrationUri) {
        this.oidcClientRegistrationUri = oidcClientRegistrationUri;
    }

    public String getOidcLogoutUri() {
        return this.getDefaultEndpoint(this.oidcLogoutUri, this.getOidcLogoutEndpoint());
    }

    public void setOidcLogoutUri(String oidcLogoutUri) {
        this.oidcLogoutUri = oidcLogoutUri;
    }

    public String getOidcUserInfoUri() {
        return this.getDefaultEndpoint(this.oidcUserInfoUri, this.getOidcUserInfoEndpoint());
    }

    public void setOidcUserInfoUri(String oidcUserInfoUri) {
        this.oidcUserInfoUri = oidcUserInfoUri;
    }

    public String getIssuerUri() {
        return this.issuerUri;
    }

    public void setIssuerUri(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getAccessTokenEndpoint() {
        return this.accessTokenEndpoint;
    }

    public void setAccessTokenEndpoint(String accessTokenEndpoint) {
        this.accessTokenEndpoint = accessTokenEndpoint;
    }

    public String getJwkSetEndpoint() {
        return this.jwkSetEndpoint;
    }

    public void setJwkSetEndpoint(String jwkSetEndpoint) {
        this.jwkSetEndpoint = jwkSetEndpoint;
    }

    public String getTokenRevocationEndpoint() {
        return this.tokenRevocationEndpoint;
    }

    public void setTokenRevocationEndpoint(String tokenRevocationEndpoint) {
        this.tokenRevocationEndpoint = tokenRevocationEndpoint;
    }

    public String getTokenIntrospectionEndpoint() {
        return this.tokenIntrospectionEndpoint;
    }

    public void setTokenIntrospectionEndpoint(String tokenIntrospectionEndpoint) {
        this.tokenIntrospectionEndpoint = tokenIntrospectionEndpoint;
    }

    public String getDeviceAuthorizationEndpoint() {
        return this.deviceAuthorizationEndpoint;
    }

    public void setDeviceAuthorizationEndpoint(String deviceAuthorizationEndpoint) {
        this.deviceAuthorizationEndpoint = deviceAuthorizationEndpoint;
    }

    public String getDeviceVerificationEndpoint() {
        return this.deviceVerificationEndpoint;
    }

    public void setDeviceVerificationEndpoint(String deviceVerificationEndpoint) {
        this.deviceVerificationEndpoint = deviceVerificationEndpoint;
    }

    public String getOidcClientRegistrationEndpoint() {
        return this.oidcClientRegistrationEndpoint;
    }

    public void setOidcClientRegistrationEndpoint(String oidcClientRegistrationEndpoint) {
        this.oidcClientRegistrationEndpoint = oidcClientRegistrationEndpoint;
    }

    public String getOidcUserInfoEndpoint() {
        return this.oidcUserInfoEndpoint;
    }

    public void setOidcUserInfoEndpoint(String oidcUserInfoEndpoint) {
        this.oidcUserInfoEndpoint = oidcUserInfoEndpoint;
    }

    public String getOidcLogoutEndpoint() {
        return this.oidcLogoutEndpoint;
    }

    public void setOidcLogoutEndpoint(String oidcLogoutEndpoint) {
        this.oidcLogoutEndpoint = oidcLogoutEndpoint;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("uaaServiceName", (Object)this.uaaServiceName).add("upmsServiceName", (Object)this.upmsServiceName).add("gatewayServiceUri", (Object)this.gatewayServiceUri).add("uaaServiceUri", (Object)this.uaaServiceUri).add("upmsServiceUri", (Object)this.upmsServiceUri).add("authorizationUri", (Object)this.authorizationUri).add("authorizationEndpoint", (Object)this.authorizationEndpoint).add("accessTokenUri", (Object)this.accessTokenUri).add("accessTokenEndpoint", (Object)this.accessTokenEndpoint).add("jwkSetUri", (Object)this.jwkSetUri).add("jwkSetEndpoint", (Object)this.jwkSetEndpoint).add("tokenRevocationUri", (Object)this.tokenRevocationUri).add("tokenRevocationEndpoint", (Object)this.tokenRevocationEndpoint).add("tokenIntrospectionUri", (Object)this.tokenIntrospectionUri).add("tokenIntrospectionEndpoint", (Object)this.tokenIntrospectionEndpoint).add("deviceAuthorizationUri", (Object)this.deviceAuthorizationUri).add("deviceAuthorizationEndpoint", (Object)this.deviceAuthorizationEndpoint).add("deviceVerificationUri", (Object)this.deviceVerificationUri).add("deviceVerificationEndpoint", (Object)this.deviceVerificationEndpoint).add("oidcClientRegistrationUri", (Object)this.oidcClientRegistrationUri).add("oidcClientRegistrationEndpoint", (Object)this.oidcClientRegistrationEndpoint).add("oidcLogoutUri", (Object)this.oidcLogoutUri).add("oidcLogoutEndpoint", (Object)this.oidcLogoutEndpoint).add("oidcUserInfoUri", (Object)this.oidcUserInfoUri).add("oidcUserInfoEndpoint", (Object)this.oidcUserInfoEndpoint).add("issuerUri", (Object)this.issuerUri).toString();
    }
}

