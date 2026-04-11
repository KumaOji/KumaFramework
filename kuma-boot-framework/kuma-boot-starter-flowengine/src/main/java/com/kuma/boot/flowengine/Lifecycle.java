package com.kuma.boot.flowengine;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface Lifecycle extends InitializingBean, DisposableBean {
}
