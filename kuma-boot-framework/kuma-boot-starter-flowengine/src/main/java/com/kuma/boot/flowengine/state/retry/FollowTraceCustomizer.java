package com.kuma.boot.flowengine.state.retry;

import com.kuma.boot.flowengine.state.FlowTrace;
import java.util.List;

public interface FollowTraceCustomizer {
   List<FlowTrace> list(RetryFailTypeEnum failType, String retryNodes, String orderBy, String sort, int batch);
}
