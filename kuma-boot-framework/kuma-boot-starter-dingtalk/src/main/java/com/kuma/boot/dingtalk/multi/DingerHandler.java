/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.model.DingerConfig;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DingerHandler
implements AlgorithmHandler {
    public static final String DINGTALK_MULTI_DINGER_COUNT = "multi.dinger.minute.limit.count";
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyMMddHHmm");
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final int COUNT_THRESHOLD = System.getProperty("multi.dinger.minute.limit.count") == null ? 20 : Integer.parseInt(System.getProperty("multi.dinger.minute.limit.count"));
    private volatile int index = 0;
    private String currentMinite = null;
    private AtomicInteger counter = new AtomicInteger(0);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DingerConfig handler(List<DingerConfig> dingerConfigs, DingerConfig defaultDingerConfig) {
        int size = dingerConfigs.size();
        DingerHandler dingerHandler = this;
        synchronized (dingerHandler) {
            int count;
            if (this.currentMinite == null) {
                this.currentMinite = LocalDateTime.now(ZONE_ID).format(DATETIME_FMT);
            }
            boolean countBool = (count = this.counter.getAndIncrement()) >= COUNT_THRESHOLD;
            String now = LocalDateTime.now(ZONE_ID).format(DATETIME_FMT);
            boolean inMinute = now.equals(this.currentMinite);
            if (countBool) {
                if (inMinute) {
                    ++this.index;
                    this.index = this.index >= size ? 0 : this.index;
                }
                this.currentMinite = now;
                this.counter.set(1);
                LogUtils.debug((String)"#{}-{}# \u5728{}\u5206\u5185\u53d1\u9001\u4e86{}\u6b21, \u5f53\u524d\u5206\u949f={}, \u4e0b\u4e00\u4e2a\u673a\u5668\u4eba={}.", (Object[])new Object[]{this.algorithmId(), COUNT_THRESHOLD, this.currentMinite, count, now, this.index});
            } else if (!countBool && !inMinute) {
                this.currentMinite = now;
                this.counter.set(1);
                LogUtils.debug((String)"#{}-{}# \u5728{}\u5206\u5185\u53d1\u9001\u4e86{}\u6b21, \u5f53\u524d\u5206\u949f={}, \u5f53\u524d\u673a\u5668\u4eba={}.", (Object[])new Object[]{this.algorithmId(), COUNT_THRESHOLD, this.currentMinite, count, now, this.index});
            }
        }
        return dingerConfigs.get(this.index);
    }
}

