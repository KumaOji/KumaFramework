package com.kuma.cloud.lab.starter.probe;

import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import org.springframework.context.ApplicationContext;

/**
 * Starter 探测器，负责诊断与冒烟测试。
 */
public interface StarterProbe {

    String name();

    String category();

    String description();

    boolean supports();

    StarterProbeResultVO diagnose(ApplicationContext applicationContext);

    StarterProbeResultVO smokeTest(ApplicationContext applicationContext);

}
