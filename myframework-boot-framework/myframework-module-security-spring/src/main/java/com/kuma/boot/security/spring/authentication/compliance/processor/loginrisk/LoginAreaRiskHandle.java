/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.dromara.hutool.core.date.DateTime
 *  org.dromara.hutool.core.date.DateUtil
 *  org.dromara.hutool.core.map.MapUtil
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.dromara.hutool.core.date.DateTime;
import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.map.MapUtil;
import org.springframework.stereotype.Component;

@Component
public class LoginAreaRiskHandle
extends AbstractLoginHandle {
    private static final Integer SEC = 1;
    private static final Integer MIN = 2;
    private static final Integer HOU = 3;

    @Override
    public void filterRisk(List<RiskRule> filter, Map<Integer, RiskRule> ruleMap, UserAccount account) {
        RiskRule areaRisk;
        if (MapUtil.isNotEmpty(ruleMap) && null != (areaRisk = ruleMap.get(4))) {
            Integer triggerTime = areaRisk.getTriggerTime();
            Integer triggerTimeType = areaRisk.getTriggerTimeType();
            Integer triggerNumber = areaRisk.getTriggerNumber();
            Date endTime = new Date();
            if (triggerTimeType == SEC) {
                DateTime startTime = DateUtil.offsetSecond((Date)endTime, (int)(-triggerTime.intValue()));
            } else if (triggerTimeType == MIN) {
                DateTime startTime = DateUtil.offsetMinute((Date)endTime, (int)(-triggerTime.intValue()));
            } else {
                DateTime startTime = DateUtil.offsetHour((Date)endTime, (int)(-triggerTime.intValue()));
            }
            long areaCount = 0L;
            if (areaCount >= triggerNumber.longValue()) {
                filter.add(areaRisk);
            }
        }
        if (this.nextHandle != null) {
            this.nextHandle.filterRisk(filter, ruleMap, account);
        }
    }
}

