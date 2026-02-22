/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.ParameterizedTypeReference
 */
package com.kuma.boot.security.spring.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;

public class WellFormedUtils {
    private static final Logger log = LoggerFactory.getLogger(WellFormedUtils.class);
    public static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new ParameterizedTypeReference<Map<String, Object>>(){};

    public static String url(String url) {
        if (StringUtils.endsWith((CharSequence)url, (CharSequence)"/")) {
            return url;
        }
        return url + "/";
    }

    public static String parentId(String parentId) {
        if (StringUtils.isBlank((CharSequence)parentId)) {
            return "0";
        }
        return parentId;
    }

    public static String getHostAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        }
        catch (UnknownHostException e) {
            log.error("Get host address error: {}", (Object)e.getLocalizedMessage());
            return null;
        }
    }

    public static String serviceUri(String gatewayServiceUri, String serviceUri, String serviceName, String abbreviation) {
        if (StringUtils.isNotBlank((CharSequence)serviceUri)) {
            return serviceUri;
        }
        if (StringUtils.isBlank((CharSequence)serviceName)) {
            log.error("Property [{} Service Name] is not set or property format is incorrect!", (Object)abbreviation);
            return null;
        }
        return WellFormedUtils.url(gatewayServiceUri) + serviceName;
    }
}

