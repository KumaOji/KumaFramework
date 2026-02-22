/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.oauth2.jose.jws.JwsAlgorithm
 *  org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
 *  org.springframework.security.oauth2.jwt.JwsHeader
 *  org.springframework.security.oauth2.jwt.JwsHeader$Builder
 *  org.springframework.security.oauth2.jwt.Jwt
 *  org.springframework.security.oauth2.jwt.JwtClaimsSet
 *  org.springframework.security.oauth2.jwt.JwtClaimsSet$Builder
 *  org.springframework.security.oauth2.jwt.JwtEncoder
 *  org.springframework.security.oauth2.jwt.JwtEncoderParameters
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.util.Assert;

public final class JwtGenerator
implements TokenGenerator<Jwt> {
    private final JwtEncoder jwtEncoder;

    public JwtGenerator(JwtEncoder jwtEncoder) {
        Assert.notNull((Object)jwtEncoder, (String)"jwtEncoder can not be null");
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public Jwt generate(Authentication authentication) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(30L, ChronoUnit.MINUTES);
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        claimsBuilder.subject(authentication.getName()).issuer("http://127.0.0.1:8080").issuedAt(issuedAt).expiresAt(expiresAt).notBefore(issuedAt);
        JwsHeader.Builder headersBuilder = JwsHeader.with((JwsAlgorithm)SignatureAlgorithm.RS256);
        JwsHeader headers = headersBuilder.build();
        JwtClaimsSet claims = claimsBuilder.build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from((JwsHeader)headers, (JwtClaimsSet)claims));
    }
}

