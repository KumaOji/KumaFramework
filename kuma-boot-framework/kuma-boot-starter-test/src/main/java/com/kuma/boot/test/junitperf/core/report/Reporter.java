package com.kuma.boot.test.junitperf.core.report;

import com.kuma.boot.test.junitperf.model.evaluation.EvaluationContext;
import java.util.Collection;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

@API(
   status = Status.INTERNAL,
   since = "2_0_0"
)
public interface Reporter {
   void report(Class testClass, Collection<EvaluationContext> evaluationContextSet);
}
