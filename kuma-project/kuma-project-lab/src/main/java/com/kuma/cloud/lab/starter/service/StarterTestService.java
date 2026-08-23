package com.kuma.cloud.lab.starter.service;

import com.kuma.cloud.lab.starter.domain.vo.StarterCatalogItemVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterScenarioVO;

import java.util.List;

public interface StarterTestService {

    List<StarterCatalogItemVO> catalog();

    List<StarterCatalogItemVO> catalogOnClasspath();

    List<StarterProbeResultVO> probes();

    StarterProbeResultVO diagnose(String starterName);

    StarterProbeResultVO smokeTest(String starterName);

    StarterScenarioVO runScenario();

}
