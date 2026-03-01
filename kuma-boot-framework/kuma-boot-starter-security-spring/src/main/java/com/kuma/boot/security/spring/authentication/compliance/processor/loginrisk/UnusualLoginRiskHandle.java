/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 异常时间登录风险实现
 */
@Component
public class UnusualLoginRiskHandle extends com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk.AbstractLoginHandle {

    @Override
    public void filterRisk(
            List<com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk.RiskRule> filter, Map<Integer, com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk.RiskRule> ruleMap, UserAccount account) {
        if (MapUtil.isNotEmpty(ruleMap)) {
            com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk.RiskRule loginTimeExe = ruleMap.get(2);
            if (loginTimeExe != null) {
                // 将json转为异常时间对象
                List<UnusualLoginTime> unusualLoginTimes =
                        JSONUtil.toList(loginTimeExe.getUnusualLoginTime(), UnusualLoginTime.class);
                Date now = new Date();
                // 判断当前时间是周几
                int dayOfWeek = DateUtil.dayOfWeek(now);
                for (UnusualLoginTime unusualLoginTime : unusualLoginTimes) {
                    // 如果当前的周数与配置的周数相等，那么判断当前的具体时间
                    if (unusualLoginTime.getWeek() == dayOfWeek) {
                        DateTime startTime = DateUtil.parse(unusualLoginTime.getStartTime());
                        DateTime endTime = DateUtil.parse(unusualLoginTime.getEndTime());
                        // 如果当前的时间，在配置的时间范围内，那么将算作异常时间登录
                        if (DateUtil.isIn(now, startTime, endTime)) {
                            filter.add(loginTimeExe);
                            break;
                        }
                    }
                }
            }
        }
        // 是否有下一个节点 ， 如果有，继续向下执行
        if (this.nextHandle != null) {
            this.nextHandle.filterRisk(filter, ruleMap, account);
        }
    }

    public static class UnusualLoginTime {

        private int week;

        private String startTime;

        private String endTime;

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
