/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.dromara.hutool.core.date.DateTime
 *  org.dromara.hutool.core.date.DateUtil
 *  org.dromara.hutool.core.map.MapUtil
 *  org.dromara.hutool.json.JSONUtil
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.dromara.hutool.core.date.DateTime;
import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

@Component
public class UnusualLoginRiskHandle
extends AbstractLoginHandle {
    @Override
    public void filterRisk(List<RiskRule> filter, Map<Integer, RiskRule> ruleMap, UserAccount account) {
        RiskRule loginTimeExe;
        if (MapUtil.isNotEmpty(ruleMap) && (loginTimeExe = ruleMap.get(2)) != null) {
            List unusualLoginTimes = JSONUtil.toList((String)loginTimeExe.getUnusualLoginTime(), UnusualLoginTime.class);
            Date now = new Date();
            int dayOfWeek = DateUtil.dayOfWeek((Date)now);
            for (UnusualLoginTime unusualLoginTime : unusualLoginTimes) {
                DateTime endTime;
                DateTime startTime;
                if (unusualLoginTime.getWeek() != dayOfWeek || !DateUtil.isIn((Date)now, (Date)(startTime = DateUtil.parse((CharSequence)unusualLoginTime.getStartTime())), (Date)(endTime = DateUtil.parse((CharSequence)unusualLoginTime.getEndTime())))) continue;
                filter.add(loginTimeExe);
                break;
            }
        }
        if (this.nextHandle != null) {
            this.nextHandle.filterRisk(filter, ruleMap, account);
        }
    }

    public static class UnusualLoginTime {
        private int week;
        private String startTime;
        private String endTime;

        public int getWeek() {
            return this.week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public String getStartTime() {
            return this.startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return this.endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}

