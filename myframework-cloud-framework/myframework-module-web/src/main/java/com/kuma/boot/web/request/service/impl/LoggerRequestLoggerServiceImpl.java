/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.web.request.service.impl;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.model.RequestLog;
import com.kuma.boot.web.request.service.RequestLoggerService;

public class LoggerRequestLoggerServiceImpl
implements RequestLoggerService {
    @Override
    public void save(RequestLog requestLog) {
        LogUtils.info((String)"\u672c\u5730\u65e5\u5fd7\u8bb0\u5f55\u6210\u529f\uff1a{}", (Object[])new Object[]{JacksonUtils.toJSONString((Object)requestLog)});
    }
}

