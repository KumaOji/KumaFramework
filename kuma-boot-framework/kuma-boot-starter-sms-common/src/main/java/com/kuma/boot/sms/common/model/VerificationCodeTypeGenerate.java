package com.kuma.boot.sms.common.model;

import java.util.Map;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface VerificationCodeTypeGenerate {
   @Nullable String getType(String phone, Map params);
}
