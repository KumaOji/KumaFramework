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

package com.kuma.boot.core.runtime.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportMessage;
import org.springframework.boot.logging.LogLevel;

import java.util.function.Supplier;

/**
 * ConditionEvaluationReportLogger
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class ConditionEvaluationReportLogger {

    private static final Logger logger = LoggerFactory.getLogger(ConditionEvaluationReportLogger.class);

    private final Supplier<ConditionEvaluationReport> reportSupplier;

    private final LogLevel logLevel;

    ConditionEvaluationReportLogger( LogLevel logLevel, Supplier<ConditionEvaluationReport> reportSupplier ) {
        //        Assert.isTrue(isInfoOrDebug(logLevel), "LogLevel must be INFO or DEBUG");
        this.logLevel = logLevel;
        this.reportSupplier = reportSupplier;
    }

    private boolean isInfoOrDebug( LogLevel logLevelForReport ) {
        return LogLevel.INFO.equals(logLevelForReport) || LogLevel.DEBUG.equals(logLevelForReport);
    }

    void logReport( boolean isCrashReport ) {
        ConditionEvaluationReport report = this.reportSupplier.get();
        if (report == null) {
            logger.info("Unable to provide the condition evaluation report");
            return;
        }
        if (!report.getConditionAndOutcomesBySource().isEmpty()) {
            if (this.logLevel.equals(LogLevel.INFO)) {
                if (logger.isInfoEnabled()) {
                    logger.info(String.valueOf(new ConditionEvaluationReportMessage(report)));
                } else if (isCrashReport) {
                    logMessage("info");
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.valueOf(new ConditionEvaluationReportMessage(report)));
                } else if (isCrashReport) {
                    logMessage("debug");
                }
            }
        }
    }

    private void logMessage( String logLevel ) {
        logger.info(String.format("%n%nError starting ApplicationContext. To display the "
                + "condition evaluation report re-run your application with '" + logLevel + "' enabled."));
    }
}
