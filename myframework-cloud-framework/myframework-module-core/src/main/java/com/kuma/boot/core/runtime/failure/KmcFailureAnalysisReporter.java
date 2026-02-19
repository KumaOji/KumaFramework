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

package com.kuma.boot.core.runtime.failure;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalysisReporter;

/**
 * KmcFailureAnalysisReporter
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public final class KmcFailureAnalysisReporter implements FailureAnalysisReporter {

    @Override
    public void report( FailureAnalysis failureAnalysis ) {
        LogUtils.error(buildMessage(failureAnalysis));
    }

    private String buildMessage( FailureAnalysis failureAnalysis ) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%n%n"));
        builder.append(String.format("***************************%n"));
        builder.append(String.format("KUMA BOOT APPLICATION FAILED TO START%n"));
        builder.append(String.format("***************************%n%n"));
        builder.append(String.format("Description:%n%n"));
        builder.append(String.format("%s%n", failureAnalysis.getDescription()));
        if (StringUtils.hasText(failureAnalysis.getAction())) {
            builder.append(String.format("%nAction:%n%n"));
            builder.append(String.format("%s%n", failureAnalysis.getAction()));
        }
        return builder.toString();
    }
}
