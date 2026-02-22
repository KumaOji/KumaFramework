/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.utils;

import com.kuma.boot.security.spring.exception.IllegalParameterExtensionLoginException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class ExtensionLoginUtils {
    private ExtensionLoginUtils() {
    }

    public static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map parameterMap = request.getParameterMap();
        LinkedMultiValueMap parameters = new LinkedMultiValueMap(parameterMap.size());
        parameterMap.forEach((arg_0, arg_1) -> ExtensionLoginUtils.lambda$getParameters$0((MultiValueMap)parameters, arg_0, arg_1));
        return parameters;
    }

    public static Map<String, Object> getParameters(HttpServletRequest request, String ... exclusions) {
        HashMap<String, Object> parameters = new HashMap<String, Object>(ExtensionLoginUtils.getParameters(request).toSingleValueMap());
        for (String exclusion : exclusions) {
            parameters.remove(exclusion);
        }
        return parameters;
    }

    public static void throwError(String errorCode, String parameterName) {
        throw new IllegalParameterExtensionLoginException(parameterName + "\u4e0d\u80fd\u4e3a\u7a7a");
    }

    private static boolean checkRequired(MultiValueMap<String, String> parameters, String parameterName, String parameterValue) {
        return !StringUtils.hasText((String)parameterValue) || ((List)parameters.get((Object)parameterName)).size() != 1;
    }

    private static boolean checkOptional(MultiValueMap<String, String> parameters, String parameterName, String parameterValue) {
        return StringUtils.hasText((String)parameterValue) && ((List)parameters.get((Object)parameterName)).size() != 1;
    }

    public static String checkParameter(MultiValueMap<String, String> parameters, String parameterName, boolean isRequired, String errorCode) {
        String value = (String)parameters.getFirst((Object)parameterName);
        if (isRequired) {
            if (ExtensionLoginUtils.checkRequired(parameters, parameterName, value)) {
                ExtensionLoginUtils.throwError(errorCode, parameterName);
            }
        } else if (ExtensionLoginUtils.checkOptional(parameters, parameterName, value)) {
            ExtensionLoginUtils.throwError(errorCode, parameterName);
        }
        return value;
    }

    public static String checkRequiredParameter(MultiValueMap<String, String> parameters, String parameterName, String errorCode) {
        return ExtensionLoginUtils.checkParameter(parameters, parameterName, true, errorCode);
    }

    public static String checkRequiredParameter(MultiValueMap<String, String> parameters, String parameterName) {
        return ExtensionLoginUtils.checkRequiredParameter(parameters, parameterName, "invalid_request");
    }

    public static String checkOptionalParameter(MultiValueMap<String, String> parameters, String parameterName, String errorCode) {
        return ExtensionLoginUtils.checkParameter(parameters, parameterName, false, errorCode);
    }

    public static String checkOptionalParameter(MultiValueMap<String, String> parameters, String parameterName) {
        return ExtensionLoginUtils.checkOptionalParameter(parameters, parameterName, "invalid_request");
    }

    private static /* synthetic */ void lambda$getParameters$0(MultiValueMap parameters, String key, String[] values) {
        for (String value : values) {
            parameters.add((Object)key, (Object)value);
        }
    }
}

