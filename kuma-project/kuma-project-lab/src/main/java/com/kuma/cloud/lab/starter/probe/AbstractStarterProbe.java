package com.kuma.cloud.lab.starter.probe;

import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeStepVO;
import com.kuma.cloud.lab.starter.support.StarterAnchorRegistry;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Starter 探测器基类。
 */
public abstract class AbstractStarterProbe implements StarterProbe {

    private final String starterName;
    private final String anchorClass;
    private final String description;

    protected AbstractStarterProbe(String starterName, String anchorClass, String description) {
        this.starterName = starterName;
        this.anchorClass = anchorClass;
        this.description = description;
    }

    @Override
    public String name() {
        return starterName;
    }

    @Override
    public String category() {
        return StarterAnchorRegistry.categoryOf(starterName);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean supports() {
        return StarterAnchorRegistry.isClassPresent(anchorClass);
    }

    @Override
    public StarterProbeResultVO diagnose(ApplicationContext applicationContext) {
        List<StarterProbeStepVO> steps = new ArrayList<>();
        if (!supports()) {
            steps.add(step("classpath", false, "锚点类不存在: " + anchorClass));
            return result(StarterProbeStatus.NOT_ON_CLASSPATH, "Starter 未引入", steps, Map.of());
        }
        steps.add(step("classpath", true, "锚点类已加载: " + anchorClass));
        return doDiagnose(applicationContext, steps);
    }

    @Override
    public StarterProbeResultVO smokeTest(ApplicationContext applicationContext) {
        StarterProbeResultVO diagnose = diagnose(applicationContext);
        if (diagnose.status() == StarterProbeStatus.NOT_ON_CLASSPATH) {
            return diagnose;
        }
        if (diagnose.status() == StarterProbeStatus.FAILED) {
            return diagnose;
        }
        List<StarterProbeStepVO> steps = new ArrayList<>(diagnose.steps());
        return doSmokeTest(applicationContext, steps);
    }

    protected abstract StarterProbeResultVO doDiagnose(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    );

    protected abstract StarterProbeResultVO doSmokeTest(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    );

    protected StarterProbeStepVO step(String action, boolean success, String detail) {
        return new StarterProbeStepVO(action, success, detail);
    }

    protected StarterProbeResultVO result(
            StarterProbeStatus status,
            String message,
            List<StarterProbeStepVO> steps,
            Map<String, Object> details
    ) {
        return new StarterProbeResultVO(
                starterName,
                category(),
                description,
                status,
                message,
                List.copyOf(steps),
                Map.copyOf(details)
        );
    }

    protected Map<String, Object> detailsOf(Object... keyValues) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (int index = 0; index + 1 < keyValues.length; index += 2) {
            details.put(String.valueOf(keyValues[index]), keyValues[index + 1]);
        }
        return details;
    }

    protected boolean hasBean(ApplicationContext applicationContext, Class<?> type) {
        return applicationContext.getBeanNamesForType(type).length > 0;
    }

    protected <T> T requireBean(ApplicationContext applicationContext, Class<T> type, String beanLabel) {
        Map<String, T> beans = applicationContext.getBeansOfType(type);
        if (beans.isEmpty()) {
            throw new IllegalStateException(beanLabel + " 未注册");
        }
        return beans.values().iterator().next();
    }

}
