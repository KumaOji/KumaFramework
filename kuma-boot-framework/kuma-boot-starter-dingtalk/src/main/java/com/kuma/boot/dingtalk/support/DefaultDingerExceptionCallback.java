/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.dingtalk.support;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.entity.DingerCallback;
import com.kuma.boot.dingtalk.exception.DingerException;

public class DefaultDingerExceptionCallback
implements DingerExceptionCallback {
    @Override
    public void execute(DingerCallback dkExCallable) {
        DingerException ex = dkExCallable.getEx();
        LogUtils.error((String)"\u5f02\u5e38\u9759\u9ed8\u5904\u7406:{}-{}->{}.", (Object[])new Object[]{ex.getPairs().code(), ex.getPairs().desc(), ex.getMessage()});
    }
}

