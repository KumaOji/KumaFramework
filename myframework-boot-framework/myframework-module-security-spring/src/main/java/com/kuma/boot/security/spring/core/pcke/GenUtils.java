/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.security.crypto.keygen.Base64StringKeyGenerator
 */
package com.kuma.boot.security.spring.core.pcke;

import com.kuma.boot.common.utils.log.LogUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;

public class GenUtils {
    public static String genCode() throws NoSuchAlgorithmException {
        Base64StringKeyGenerator authorizationCodeGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);
        String codeVerifier = authorizationCodeGenerator.generateKey();
        LogUtils.info((String)codeVerifier, (Object[])new Object[0]);
        LogUtils.info((String)"length: {}", (Object[])new Object[]{codeVerifier.length()});
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        LogUtils.info((String)codeChallenge, (Object[])new Object[0]);
        return codeChallenge;
    }
}

