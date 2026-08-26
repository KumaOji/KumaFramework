package com.kuma.cloud.lab.spring.service.impl;

import com.kuma.cloud.lab.spring.architecture.OrderApplicationService;
import com.kuma.cloud.lab.spring.config.SpringLabProperties;
import com.kuma.cloud.lab.spring.context.SpringLabContextHolder;
import com.kuma.cloud.lab.spring.domain.dto.PublishEventDTO;
import com.kuma.cloud.lab.spring.domain.event.LabUserRegisteredEvent;
import com.kuma.cloud.lab.spring.domain.vo.ArchitectureDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.ContextInfoVO;
import com.kuma.cloud.lab.spring.domain.vo.EventDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.IocDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.SpringOperationStepVO;
import com.kuma.cloud.lab.spring.domain.vo.SpringScenarioVO;
import com.kuma.cloud.lab.spring.ioc.MessageService;
import com.kuma.cloud.lab.spring.lifecycle.PrototypeCounter;
import com.kuma.cloud.lab.spring.service.SpringLabService;
import com.kuma.cloud.lab.spring.support.SpringLabEventHistory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpringLabServiceImpl implements SpringLabService {

    private final MessageService messageService;
    private final ApplicationContext applicationContext;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderApplicationService orderApplicationService;
    private final SpringLabProperties springLabProperties;
    private final SpringLabEventHistory eventHistory;

    @Override
    public SpringScenarioVO runScenario() {
        List<SpringOperationStepVO> steps = new ArrayList<>();

        IocDemoVO ioc = demonstrateIoc();
        steps.add(new SpringOperationStepVO(
                "IOC",
                "Bean 注入",
                ioc,
                "@Primary 与 @Qualifier 控制多实现注入；prototype 每次 getBean 产生新实例"));

        ContextInfoVO context = demonstrateContext();
        steps.add(new SpringOperationStepVO(
                "Context",
                "容器快照",
                context,
                "ApplicationContext 管理 Bean 定义与 Profile"));

        PublishEventDTO publishDto = new PublishEventDTO();
        publishDto.setUsername(springLabProperties.getDemoUsername());
        publishDto.setSource("scenario");
        EventDemoVO event = publishUserRegisteredEvent(publishDto);
        steps.add(new SpringOperationStepVO(
                "Event",
                "事件发布",
                event,
                "ApplicationEventPublisher 发布，@EventListener 异步/同步消费"));

        ArchitectureDemoVO architecture = demonstrateArchitecture();
        steps.add(new SpringOperationStepVO(
                "Architecture",
                "分层调用",
                architecture,
                "Controller → Service → Repository，Service 层发布领域事件"));

        return new SpringScenarioVO(steps);
    }

    @Override
    public IocDemoVO demonstrateIoc() {
        String message = "Hello Spring IOC";
        PrototypeCounter counterA = applicationContext.getBean(PrototypeCounter.class);
        PrototypeCounter counterB = applicationContext.getBean(PrototypeCounter.class);

        return new IocDemoVO(
                messageService.primaryRendererType(),
                messageService.plainRendererType(),
                messageService.renderWithPrimary(message),
                messageService.renderWithPlain(message),
                counterA.instanceId(),
                counterB.instanceId(),
                counterA.instanceId() != counterB.instanceId());
    }

    @Override
    public ContextInfoVO demonstrateContext() {
        Environment environment = applicationContext.getEnvironment();
        return new ContextInfoVO(
                environment.getProperty("spring.application.name", "unknown"),
                applicationContext.getId(),
                environment.getActiveProfiles(),
                applicationContext.getBeanDefinitionCount(),
                SpringLabContextHolder.containsBean("messageService"),
                SpringLabContextHolder.containsBean("inMemoryOrderRepository"),
                eventHistory.contextLogs(),
                eventHistory.lifecycleLogs());
    }

    @Override
    public EventDemoVO publishUserRegisteredEvent(PublishEventDTO dto) {
        LabUserRegisteredEvent event = new LabUserRegisteredEvent(dto.getUsername(), dto.getSource());
        eventPublisher.publishEvent(event);
        return new EventDemoVO(
                "LabUserRegisteredEvent(username=" + dto.getUsername() + ")",
                eventHistory.handledEvents());
    }

    @Override
    public ArchitectureDemoVO demonstrateArchitecture() {
        String orderId = springLabProperties.getDemoOrderId();
        List<String> callChain = new ArrayList<>();
        callChain.add("Controller 接收创建订单请求");
        callChain.add("OrderApplicationService#createOrder 执行业务逻辑");
        callChain.add("InMemoryOrderRepository#save 持久化订单状态 CREATED");
        callChain.add("ApplicationEventPublisher 发布 LabOrderCreatedEvent");
        callChain.add("SpringLabEventListener#onOrderCreated 监听并记录事件");

        String status = orderApplicationService.createOrder(orderId, "Spring-Lab-Book", 99.0);
        callChain.add("返回订单状态: " + status);

        return new ArchitectureDemoVO(orderId, status, callChain);
    }

}
