package com.kuma.cloud.lab.starter.probe.impl;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeStepVO;
import com.kuma.cloud.lab.starter.probe.AbstractStarterProbe;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringdocStarterProbe extends AbstractStarterProbe {

    public SpringdocStarterProbe() {
        super(
                StarterNameConstants.SPRINGDOC_STARTER,
                "com.kuma.boot.springdoc.autoconfigure.SpringdocAutoConfiguration",
                "OpenAPI 文档与 Knife4j 集成"
        );
    }

    @Override
    protected StarterProbeResultVO doDiagnose(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        boolean openApiReady = hasBean(applicationContext, OpenAPI.class);
        steps.add(step("bean.openAPI", openApiReady,
                openApiReady ? "OpenAPI Bean 已注册" : "OpenAPI Bean 缺失"));
        if (!openApiReady) {
            return result(StarterProbeStatus.FAILED, "Springdoc Starter Bean 未就绪", steps, detailsOf());
        }
        OpenAPI openAPI = requireBean(applicationContext, OpenAPI.class, "OpenAPI");
        String title = openAPI.getInfo() != null ? openAPI.getInfo().getTitle() : null;
        steps.add(step("openapi.info", title != null, title != null ? "title=" + title : "OpenAPI Info 缺失"));
        return result(
                StarterProbeStatus.READY,
                "Springdoc Starter 已就绪",
                steps,
                detailsOf("title", title)
        );
    }

    @Override
    protected StarterProbeResultVO doSmokeTest(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        if (!hasBean(applicationContext, OpenAPI.class)) {
            steps.add(step("openapi.smoke", false, "OpenAPI Bean 不可用"));
            return result(StarterProbeStatus.FAILED, "Springdoc Starter 冒烟测试失败", steps, detailsOf());
        }
        OpenAPI openAPI = requireBean(applicationContext, OpenAPI.class, "OpenAPI");
        boolean ready = openAPI.getInfo() != null && openAPI.getInfo().getTitle() != null;
        steps.add(step("openapi.smoke", ready, ready ? "OpenAPI 元信息完整" : "OpenAPI 元信息不完整"));
        return result(
                ready ? StarterProbeStatus.PASSED : StarterProbeStatus.FAILED,
                ready ? "Springdoc Starter 冒烟测试通过" : "Springdoc Starter 冒烟测试失败",
                steps,
                detailsOf("title", openAPI.getInfo() != null ? openAPI.getInfo().getTitle() : null)
        );
    }

}
