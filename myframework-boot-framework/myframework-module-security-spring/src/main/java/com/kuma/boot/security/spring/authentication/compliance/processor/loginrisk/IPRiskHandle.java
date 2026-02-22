/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.dromara.hutool.core.map.MapUtil
 *  org.dromara.hutool.core.text.StrUtil
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.stereotype.Component;

@Component
public class IPRiskHandle
extends AbstractLoginHandle {
    @Override
    public void filterRisk(List<RiskRule> filter, Map<Integer, RiskRule> ruleMap, UserAccount account) {
        List<String> acceptIpList;
        RiskRule ipRisk;
        if (MapUtil.isNotEmpty(ruleMap) && null != (ipRisk = ruleMap.get(3)) && StrUtil.isNotEmpty((CharSequence)ipRisk.getAcceptIp()) && !(acceptIpList = Arrays.asList(ipRisk.getAcceptIp().split(","))).contains(account.getIp())) {
            filter.add(ipRisk);
        }
        if (this.nextHandle != null) {
            this.nextHandle.filterRisk(filter, ruleMap, account);
        }
    }
}

