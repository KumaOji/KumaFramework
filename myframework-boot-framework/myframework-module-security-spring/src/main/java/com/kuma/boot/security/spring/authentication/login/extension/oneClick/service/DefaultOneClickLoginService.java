/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.dromara.hutool.core.map.MapUtil
 *  org.springframework.security.core.userdetails.UserDetails
 */
package com.kuma.boot.security.spring.authentication.login.extension.oneClick.service;

import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;
import org.dromara.hutool.core.map.MapUtil;
import org.springframework.security.core.userdetails.UserDetails;

public class DefaultOneClickLoginService
implements OneClickLoginService {
    @Override
    public String callback(String accessToken, Map<String, String> otherParamMap) {
        String opToken = otherParamMap.get("opToken");
        String operator = otherParamMap.get("operator");
        String phoneNumber = null;
        return phoneNumber;
    }

    @Override
    public void otherParamsHandler(UserDetails userDetails, Map<String, String> otherParamMap) {
        if (MapUtil.isNotEmpty(otherParamMap) && !otherParamMap.isEmpty()) {
            LogUtils.info((String)"\u767b\u5f55\u7528\u6237: {}", (Object[])new Object[]{userDetails.getUsername()});
            LogUtils.info((String)"\u767b\u5f55\u65f6\u7684\u5176\u4ed6\u8bf7\u6c42\u53c2\u6570: {}", (Object[])new Object[]{otherParamMap.toString()});
        }
    }
}

