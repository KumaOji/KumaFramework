/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.parser;

import com.kuma.boot.common.support.cron.pojo.TimeOfDay;
import java.util.Date;
import java.util.List;

public interface CronParser {
    public Date next(Date var1);

    public List<TimeOfDay> timesOfDay(Date var1);
}

