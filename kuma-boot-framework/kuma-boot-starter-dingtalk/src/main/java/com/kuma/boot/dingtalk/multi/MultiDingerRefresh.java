/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.dingtalk.enums.MultiDingerConfigContainer;
import com.kuma.boot.dingtalk.model.DingerRefresh;

public class MultiDingerRefresh
extends DingerRefresh {
    protected static void multiDingerRefresh() {
        MultiDingerRefresh.dingerFresh();
        MultiDingerProperty.clear();
        MultiDingerAlgorithmInjectRegister.clear();
        MultiDingerConfigContainer.clear();
    }
}

