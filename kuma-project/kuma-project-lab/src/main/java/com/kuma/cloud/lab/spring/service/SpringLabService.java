package com.kuma.cloud.lab.spring.service;

import com.kuma.cloud.lab.spring.domain.dto.PublishEventDTO;
import com.kuma.cloud.lab.spring.domain.vo.ArchitectureDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.ContextInfoVO;
import com.kuma.cloud.lab.spring.domain.vo.EventDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.IocDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.SpringScenarioVO;

public interface SpringLabService {

    SpringScenarioVO runScenario();

    IocDemoVO demonstrateIoc();

    ContextInfoVO demonstrateContext();

    EventDemoVO publishUserRegisteredEvent(PublishEventDTO dto);

    ArchitectureDemoVO demonstrateArchitecture();

}
