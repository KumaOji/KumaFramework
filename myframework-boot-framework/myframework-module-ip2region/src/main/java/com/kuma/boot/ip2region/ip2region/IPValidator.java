/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.net.InetAddresses
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.ip2region.ip2region;

import com.google.common.net.InetAddresses;
import com.kuma.boot.ip2region.ip2region.annotation.IP;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class IPValidator
implements ConstraintValidator<IP, String> {
    public boolean isValid(String ipStr, ConstraintValidatorContext context) {
        return StringUtils.hasText((String)ipStr) && InetAddresses.isInetAddress((String)ipStr);
    }
}

