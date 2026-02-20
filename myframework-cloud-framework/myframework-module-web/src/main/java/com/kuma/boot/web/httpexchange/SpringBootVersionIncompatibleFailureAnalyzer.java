package com.kuma.boot.web.httpexchange;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * A {@link org.springframework.boot.diagnostics.FailureAnalyzer} that analyzes
 * {@link com.kuma.boot.web.httpexchange.SpringBootVersionIncompatibleException}.
 *
 * @author Freeman
 */
class SpringBootVersionIncompatibleFailureAnalyzer
        extends AbstractFailureAnalyzer<com.kuma.boot.web.httpexchange.SpringBootVersionIncompatibleException> {

    @Override
    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE")
    protected FailureAnalysis analyze(Throwable rootFailure, com.kuma.boot.web.httpexchange.SpringBootVersionIncompatibleException cause) {
        return new FailureAnalysis(
                "The current version of httpexchange-spring-boot-starter requires Spring Boot %s or higher, but found %s."
                        .formatted(cause.getRequiredVersion(), cause.getCurrentVersion()),
                """
                                Please upgrade your Spring Boot version to at least %s.
                                """.formatted(cause.getRequiredVersion()),
                cause);
    }
}
