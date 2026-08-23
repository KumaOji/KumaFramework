package com.kuma.cloud.lab.starter.probe.impl;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.core.support.Collector;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeStepVO;
import com.kuma.cloud.lab.starter.probe.AbstractStarterProbe;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoreStarterProbe extends AbstractStarterProbe {

    public CoreStarterProbe() {
        super(
                StarterNameConstants.CORE_STARTER,
                "com.kuma.boot.core.autoconfigure.CoreAutoConfiguration",
                "核心运行时、Collector 与启动增强"
        );
    }

    @Override
    protected StarterProbeResultVO doDiagnose(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        boolean collectorReady = hasBean(applicationContext, Collector.class);
        steps.add(step("bean.collector", collectorReady, collectorReady ? "Collector Bean 已注册" : "Collector Bean 缺失"));
        StarterProbeStatus status = collectorReady ? StarterProbeStatus.READY : StarterProbeStatus.FAILED;
        return result(
                status,
                collectorReady ? "Core Starter 已就绪" : "Core Starter Bean 未就绪",
                steps,
                detailsOf("collectorBeans", applicationContext.getBeanNamesForType(Collector.class).length)
        );
    }

    @Override
    protected StarterProbeResultVO doSmokeTest(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        try {
            Collector collector = requireBean(applicationContext, Collector.class, "Collector");
            collector.value("starter-lab-probe").set("ok");
            Object value = collector.value("starter-lab-probe").get();
            boolean success = "ok".equals(String.valueOf(value));
            steps.add(step("collector.value", success, "Collector 读写=" + value));
            return result(
                    success ? StarterProbeStatus.PASSED : StarterProbeStatus.FAILED,
                    success ? "Core Starter 冒烟测试通过" : "Core Starter 冒烟测试失败",
                    steps,
                    detailsOf("value", value)
            );
        } catch (Exception error) {
            steps.add(step("collector.invoke", false, error.getMessage()));
            return result(StarterProbeStatus.FAILED, "Core Starter 冒烟测试失败", steps, detailsOf());
        }
    }

}
