/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.extension.mfa.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.util.Assert;

/**
 * @author: ReLive27
 * @since: 2023/1/12 19:01
 */
public final class JwtGenerator implements TokenGenerator<Jwt> {
    private final JwtEncoder jwtEncoder;

    public JwtGenerator(JwtEncoder jwtEncoder) {
        Assert.notNull(jwtEncoder, "jwtEncoder can not be null");
        this.jwtEncoder = jwtEncoder;
    }

    public Jwt generate(Authentication authentication) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(30, ChronoUnit.MINUTES);
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        claimsBuilder
                .subject(authentication.getName())
                .issuer("http://127.0.0.1:8080")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .notBefore(issuedAt);

        JwsHeader.Builder headersBuilder = JwsHeader.with(SignatureAlgorithm.RS256);

        JwsHeader headers = headersBuilder.build();
        JwtClaimsSet claims = claimsBuilder.build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(headers, claims));
    }
}
