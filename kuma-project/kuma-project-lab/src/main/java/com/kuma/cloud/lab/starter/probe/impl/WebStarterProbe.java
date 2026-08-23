package com.kuma.cloud.lab.starter.probe.impl;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.web.gracefulresponse.advice.GlobalExceptionAdvice;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeStepVO;
import com.kuma.cloud.lab.starter.probe.AbstractStarterProbe;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.List;

@Component
public class WebStarterProbe extends AbstractStarterProbe {

    public WebStarterProbe() {
        super(
                StarterNameConstants.WEB_STARTER,
                "com.kuma.boot.web.autoconfigure.ServletAutoConfiguration",
                "Servlet Web、统一响应与全局异常处理"
        );
    }

    @Override
    protected StarterProbeResultVO doDiagnose(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        boolean servletReady = hasBean(applicationContext, DispatcherServlet.class);
        boolean adviceReady = hasBean(applicationContext, GlobalExceptionAdvice.class);
        steps.add(step("bean.dispatcherServlet", servletReady,
                servletReady ? "DispatcherServlet 已注册" : "DispatcherServlet 缺失"));
        steps.add(step("bean.globalExceptionAdvice", adviceReady,
                adviceReady ? "GlobalExceptionAdvice 已注册" : "GlobalExceptionAdvice 缺失"));
        boolean ready = servletReady && adviceReady;
        return result(
                ready ? StarterProbeStatus.READY : StarterProbeStatus.FAILED,
                ready ? "Web Starter 已就绪" : "Web Starter Bean 未就绪",
                steps,
                detailsOf(
                        "dispatcherServletBeans", applicationContext.getBeanNamesForType(DispatcherServlet.class).length,
                        "globalExceptionAdviceBeans", applicationContext.getBeanNamesForType(GlobalExceptionAdvice.class).length
                )
        );
    }

    @Override
    protected StarterProbeResultVO doSmokeTest(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        boolean ready = hasBean(applicationContext, DispatcherServlet.class)
                && hasBean(applicationContext, GlobalExceptionAdvice.class);
        steps.add(step("web.stack", ready, ready ? "Web 栈组件齐全" : "Web 栈组件不完整"));
        return result(
                ready ? StarterProbeStatus.PASSED : StarterProbeStatus.FAILED,
                ready ? "Web Starter 冒烟测试通过" : "Web Starter 冒烟测试失败",
                steps,
                detailsOf()
        );
    }

}
