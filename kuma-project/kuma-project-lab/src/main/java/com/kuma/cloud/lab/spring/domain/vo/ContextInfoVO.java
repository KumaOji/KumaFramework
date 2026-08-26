package com.kuma.cloud.lab.spring.domain.vo;

import java.util.List;

/**
 * ApplicationContext 信息快照。
 */
public record ContextInfoVO(
        String applicationName,
        String contextId,
        String[] activeProfiles,
        int beanDefinitionCount,
        boolean containsMessageService,
        boolean containsOrderRepository,
        List<String> contextLogs,
        List<String> lifecycleLogs
) {
}
