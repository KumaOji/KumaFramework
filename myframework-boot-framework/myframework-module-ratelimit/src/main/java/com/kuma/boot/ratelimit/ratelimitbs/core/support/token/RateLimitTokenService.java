/*
 *  cn.hutool.core.util.ArrayUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.token;

import cn.hutool.core.util.ArrayUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import jakarta.servlet.http.HttpServletRequest;

public class RateLimitTokenService
implements IRateLimitTokenService {
    @Override
    public String getTokenId(Object[] params) {
        if (ArrayUtil.isEmpty((Object[])params)) {
            LogUtils.warn((String)"Param is empty, return empty", (Object[])new Object[0]);
            return "";
        }
        for (Object param : params) {
            if (!(param instanceof HttpServletRequest)) continue;
            HttpServletRequest request = (HttpServletRequest)param;
            return RequestUtils.getRemoteAddr((HttpServletRequest)request);
        }
        LogUtils.warn((String)"Param is not found in request, return empty", (Object[])new Object[0]);
        return "";
    }
}

