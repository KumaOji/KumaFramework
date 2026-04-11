package com.kuma.boot.flowengine.delegate;

import com.kuma.boot.flowengine.module.Flow;

public interface InvokeSupport {
   void proceed(Flow.Key flowKey, String nodeName, String target);
}
