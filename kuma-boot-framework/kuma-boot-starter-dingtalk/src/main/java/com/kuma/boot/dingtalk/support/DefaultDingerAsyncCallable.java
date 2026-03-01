/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.dingtalk.support;

import com.kuma.boot.common.utils.log.LogUtils;

public class DefaultDingerAsyncCallable
implements DingerAsyncCallback {
    @Override
    public void execute(String dingerId, String result) {
        LogUtils.info((String)"dingerId=[{}], result=[{}].", (Object[])new Object[]{dingerId, result});
    }
}

