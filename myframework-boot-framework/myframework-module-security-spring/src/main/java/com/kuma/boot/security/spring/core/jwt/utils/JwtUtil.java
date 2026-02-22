/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.exception.BusinessException
 *  com.kuma.boot.common.utils.date.DateUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  io.jsonwebtoken.Claims
 *  io.jsonwebtoken.ExpiredJwtException
 *  io.jsonwebtoken.JwtBuilder
 *  io.jsonwebtoken.Jwts
 *  io.jsonwebtoken.Jwts$SIG
 *  io.jsonwebtoken.SignatureAlgorithm
 *  io.jsonwebtoken.SignatureException
 *  io.jsonwebtoken.security.SecretKeyBuilder
 *  org.dromara.hutool.core.text.StrUtil
 */
package com.kuma.boot.security.spring.core.jwt.utils;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.core.jwt.model.ExceptionCode;
import com.kuma.boot.security.spring.core.jwt.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.SecretKeyBuilder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.dromara.hutool.core.text.StrUtil;

public final class JwtUtil {
    private static final String BASE64_SECURITY = Base64.getEncoder().encodeToString("lamp-cloud_is_a_fantastic_project".getBytes(StandardCharsets.UTF_8));

    private JwtUtil() {
    }

    public static String[] getClient(String basicHeader) {
        if (StrUtil.isEmpty((CharSequence)basicHeader) || !basicHeader.startsWith("Basic ")) {
            throw new BusinessException(ExceptionCode.JWT_BASIC_INVALID.name());
        }
        String decodeBasic = StrUtil.subAfter((CharSequence)basicHeader, (CharSequence)"Basic ", (boolean)false);
        return JwtUtil.extractClient(decodeBasic);
    }

    public static String[] extractClient(String client) {
        String token = JwtUtil.base64Decoder(client);
        int index = token.indexOf(":");
        if (index == -1) {
            throw new BusinessException(ExceptionCode.JWT_BASIC_INVALID.name());
        }
        return new String[]{token.substring(0, index), token.substring(index + 1)};
    }

    public static String base64Decoder(String val) {
        byte[] decoded = Base64.getDecoder().decode(val.getBytes(StandardCharsets.UTF_8));
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public static Token createJwt(Map<String, String> user, long expire) {
        SecretKey key = (SecretKey)((SecretKeyBuilder)Jwts.SIG.HS256.key()).build();
        String jws = Jwts.builder().subject("Joe").signWith((Key)key).compact();
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(BASE64_SECURITY);
        SecretKeySpec signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", (Object)"JsonWebToken").signWith(signatureAlgorithm, (Key)signingKey);
        user.forEach((arg_0, arg_1) -> ((JwtBuilder)builder).claim(arg_0, arg_1));
        long expMillis = nowMillis + expire * 1000L;
        Date exp = new Date(expMillis);
        ((JwtBuilder)((JwtBuilder)builder.setIssuedAt(now)).setNotBefore(now)).setExpiration(exp);
        Token tokenInfo = new Token();
        tokenInfo.setToken(builder.compact());
        tokenInfo.setExpire(expire);
        tokenInfo.setExpiration(DateUtils.date2LocalDateTime((Date)exp));
        return tokenInfo;
    }

    public static Claims parseJwt(String jsonWebToken, long allowedClockSkewSeconds) {
        try {
            return null;
        }
        catch (ExpiredJwtException ex) {
            LogUtils.error((String)"token=[{}], \u8fc7\u671f", (Object[])new Object[]{jsonWebToken, ex});
            throw new BusinessException(Integer.valueOf(ExceptionCode.JWT_TOKEN_EXPIRED.getCode()), ExceptionCode.JWT_TOKEN_EXPIRED.getMsg());
        }
        catch (SignatureException ex) {
            LogUtils.error((String)"token=[{}] \u7b7e\u540d\u9519\u8bef", (Object[])new Object[]{jsonWebToken, ex});
            throw new BusinessException(Integer.valueOf(ExceptionCode.JWT_SIGNATURE.getCode()), ExceptionCode.JWT_SIGNATURE.getMsg());
        }
        catch (IllegalArgumentException ex) {
            LogUtils.error((String)"token=[{}] \u4e3a\u7a7a", (Object[])new Object[]{jsonWebToken, ex});
            throw new BusinessException(Integer.valueOf(ExceptionCode.JWT_ILLEGAL_ARGUMENT.getCode()), ExceptionCode.JWT_ILLEGAL_ARGUMENT.getMsg());
        }
        catch (Exception e) {
            LogUtils.error((String)"token=[{}] errCode:{}, message:{}", (Object[])new Object[]{jsonWebToken, ExceptionCode.JWT_PARSER_TOKEN_FAIL.getCode(), e.getMessage(), e});
            throw new BusinessException(Integer.valueOf(ExceptionCode.JWT_PARSER_TOKEN_FAIL.getCode()), ExceptionCode.JWT_PARSER_TOKEN_FAIL.getMsg());
        }
    }

    public static String getToken(String token) {
        if (token == null) {
            throw new BusinessException(ExceptionCode.JWT_PARSER_TOKEN_FAIL.name());
        }
        if (token.startsWith("Bearer ")) {
            return StrUtil.subAfter((CharSequence)token, (CharSequence)"Bearer ", (boolean)false);
        }
        LogUtils.info((String)"jsonWebToken={}", (Object[])new Object[]{token});
        throw new BusinessException(ExceptionCode.JWT_PARSER_TOKEN_FAIL.name());
    }

    public static Claims getClaims(String token, long allowedClockSkewSeconds) {
        if (token == null) {
            throw new BusinessException(ExceptionCode.JWT_PARSER_TOKEN_FAIL.name());
        }
        if (token.startsWith("Bearer ")) {
            String headStr = StrUtil.subAfter((CharSequence)token, (CharSequence)"Bearer ", (boolean)false);
            return JwtUtil.parseJwt(headStr, allowedClockSkewSeconds);
        }
        LogUtils.info((String)"jsonWebToken={}", (Object[])new Object[]{token});
        throw new BusinessException(ExceptionCode.JWT_PARSER_TOKEN_FAIL.name());
    }
}

