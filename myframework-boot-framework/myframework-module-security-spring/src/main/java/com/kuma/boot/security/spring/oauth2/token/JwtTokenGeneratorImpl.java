/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.nimbusds.jose.jwk.source.JWKSource
 *  com.nimbusds.jose.proc.SecurityContext
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.oauth2.core.OAuth2AccessToken$TokenType
 *  org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
 *  org.springframework.security.oauth2.core.user.OAuth2User
 *  org.springframework.security.oauth2.jose.jws.JwsAlgorithm
 *  org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
 *  org.springframework.security.oauth2.jwt.JwsHeader
 *  org.springframework.security.oauth2.jwt.JwsHeader$Builder
 *  org.springframework.security.oauth2.jwt.Jwt
 *  org.springframework.security.oauth2.jwt.JwtClaimsSet
 *  org.springframework.security.oauth2.jwt.JwtEncoderParameters
 *  org.springframework.security.oauth2.jwt.NimbusJwtEncoder
 */
package com.kuma.boot.security.spring.oauth2.token;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

public class JwtTokenGeneratorImpl
implements JwtTokenGenerator {
    private final JWKSource<SecurityContext> jwkSource;

    public JwtTokenGeneratorImpl(JWKSource<SecurityContext> jwkSource) {
        this.jwkSource = jwkSource;
    }

    @Override
    public OAuth2AccessTokenResponse tokenResponse(UserDetails userDetails) {
        JwsHeader jwsHeader = ((JwsHeader.Builder)JwsHeader.with((JwsAlgorithm)SignatureAlgorithm.RS256).type("JWT")).build();
        Instant issuedAt = Clock.system(ZoneId.of("Asia/Shanghai")).instant();
        Set scopes = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        Instant expiresAt = issuedAt.plusSeconds(18000L);
        JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuer("https://blog.kumacloud.top/").subject(userDetails.getUsername()).expiresAt(expiresAt).audience(Arrays.asList("client1", "client2")).issuedAt(issuedAt).claim("scope", scopes).build();
        Jwt jwt = new NimbusJwtEncoder(this.jwkSource).encode(JwtEncoderParameters.from((JwsHeader)jwsHeader, (JwtClaimsSet)claimsSet));
        return OAuth2AccessTokenResponse.withToken((String)jwt.getTokenValue()).tokenType(OAuth2AccessToken.TokenType.BEARER).expiresIn(expiresAt.getEpochSecond()).scopes(scopes).refreshToken(UUID.randomUUID().toString()).build();
    }

    @Override
    public OAuth2AccessTokenResponse socialTokenResponse(OAuth2User oAuth2User) {
        JwsHeader jwsHeader = ((JwsHeader.Builder)JwsHeader.with((JwsAlgorithm)SignatureAlgorithm.RS256).type("JWT")).build();
        Instant issuedAt = Clock.system(ZoneId.of("Asia/Shanghai")).instant();
        Set scopes = oAuth2User.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        Instant expiresAt = issuedAt.plusSeconds(18000L);
        JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuer("https://blog.kumacloud.top/").subject(oAuth2User.getName()).expiresAt(expiresAt).audience(Arrays.asList("client1", "client2")).issuedAt(issuedAt).claim("scope", scopes).build();
        Jwt jwt = new NimbusJwtEncoder(this.jwkSource).encode(JwtEncoderParameters.from((JwsHeader)jwsHeader, (JwtClaimsSet)claimsSet));
        return OAuth2AccessTokenResponse.withToken((String)jwt.getTokenValue()).tokenType(OAuth2AccessToken.TokenType.BEARER).expiresIn(expiresAt.getEpochSecond()).scopes(scopes).refreshToken(UUID.randomUUID().toString()).build();
    }
}

