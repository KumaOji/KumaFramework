package com.kuma.cloud.lab.starter.probe.impl;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeStepVO;
import com.kuma.cloud.lab.starter.probe.AbstractStarterProbe;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommonStarterProbe extends AbstractStarterProbe {

    public CommonStarterProbe() {
        super(
                StarterNameConstants.COMMON_STARTER,
                "com.kuma.boot.common.constant.StarterNameConstants",
                "通用模型、工具类与基础常量"
        );
    }

    @Override
    protected StarterProbeResultVO doDiagnose(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        boolean resultPresent = supports();
        steps.add(step("model", resultPresent, "Result 模型可用"));
        return result(
                StarterProbeStatus.READY,
                "通用 Starter 已就绪",
                steps,
                detailsOf("resultClass", Result.class.getName())
        );
    }

    @Override
    protected StarterProbeResultVO doSmokeTest(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        Result<String> response = Result.success("starter-lab");
        boolean success = StatusEnum.SUCCESS.name().equals(response.getStatus())
                && "starter-lab".equals(response.getData());
        steps.add(step("result.success", success, success ? "Result.success 正常" : "Result.success 异常"));
        return result(
                success ? StarterProbeStatus.PASSED : StarterProbeStatus.FAILED,
                success ? "通用 Starter 冒烟测试通过" : "通用 Starter 冒烟测试失败",
                steps,
                detailsOf("code", response.getCode())
        );
    }

}
