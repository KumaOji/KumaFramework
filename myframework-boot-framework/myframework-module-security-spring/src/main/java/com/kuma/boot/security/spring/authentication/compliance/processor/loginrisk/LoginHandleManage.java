/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.dromara.hutool.core.collection.CollUtil
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Component;

@Component
public class LoginHandleManage {
    @Resource
    private IPRiskHandle ipRiskHandle;
    @Resource
    private LoginAreaRiskHandle loginAreaRiskHandle;
    @Resource
    private PasswordErrorRiskHandle passwordErrorRiskHandle;
    @Resource
    private UnusualLoginRiskHandle unusualLoginRiskHandle;

    @PostConstruct
    public void init() {
        this.passwordErrorRiskHandle.setNextHandle(this.unusualLoginRiskHandle);
        this.unusualLoginRiskHandle.setNextHandle(this.ipRiskHandle);
        this.ipRiskHandle.setNextHandle(this.loginAreaRiskHandle);
    }

    public void execute(UserAccount account) throws Exception {
        Optional<RiskRule> optional;
        ArrayList riskRules = new ArrayList();
        Map<Integer, RiskRule> riskRuleMap = riskRules.stream().collect(Collectors.toMap(RiskRule::getId, r -> r));
        ArrayList<RiskRule> filterRisk = new ArrayList<RiskRule>();
        this.passwordErrorRiskHandle.filterRisk(filterRisk, riskRuleMap, account);
        if (CollUtil.isNotEmpty(filterRisk) && (optional = filterRisk.stream().max(Comparator.comparing(RiskRule::getOperate))).isPresent()) {
            RiskRule riskRule = optional.get();
            this.handleOperate(riskRule);
        }
    }

    public void handleOperate(RiskRule riskRule) throws Exception {
        int operate = riskRule.getOperate();
    }
}

