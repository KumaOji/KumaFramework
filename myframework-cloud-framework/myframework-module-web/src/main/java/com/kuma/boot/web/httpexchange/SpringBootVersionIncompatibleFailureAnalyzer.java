/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.boot.diagnostics.AbstractFailureAnalyzer
 *  org.springframework.boot.diagnostics.FailureAnalysis
 */
package com.kuma.boot.web.httpexchange;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

class SpringBootVersionIncompatibleFailureAnalyzer
extends AbstractFailureAnalyzer<SpringBootVersionIncompatibleException> {
    SpringBootVersionIncompatibleFailureAnalyzer() {
    }

    @SuppressFBWarnings(value={"VA_FORMAT_STRING_USES_NEWLINE"})
    protected FailureAnalysis analyze(Throwable rootFailure, SpringBootVersionIncompatibleException cause) {
        return new FailureAnalysis("The current version of httpexchange-spring-boot-starter requires Spring Boot %s or higher, but found %s.".formatted(cause.getRequiredVersion(), cause.getCurrentVersion()), "Please upgrade your Spring Boot version to at least %s.\n".formatted(cause.getRequiredVersion()), (Throwable)cause);
    }
}

