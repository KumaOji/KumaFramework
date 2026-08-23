package com.kuma.cloud.lab.starter.service.impl;

import com.kuma.cloud.lab.starter.config.StarterLabProperties;
import com.kuma.cloud.lab.starter.domain.vo.StarterCatalogItemVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterScenarioVO;
import com.kuma.cloud.lab.starter.probe.StarterProbe;
import com.kuma.cloud.lab.starter.service.StarterTestService;
import com.kuma.cloud.lab.starter.support.StarterCatalogLoader;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StarterTestServiceImpl implements StarterTestService {

    private final ApplicationContext applicationContext;
    private final StarterLabProperties starterLabProperties;
    private final List<StarterProbe> probes;

    @Override
    public List<StarterCatalogItemVO> catalog() {
        return StarterCatalogLoader.load();
    }

    @Override
    public List<StarterCatalogItemVO> catalogOnClasspath() {
        return catalog().stream()
                .filter(item -> Boolean.TRUE.equals(item.onClasspath()))
                .toList();
    }

    @Override
    public List<StarterProbeResultVO> probes() {
        return probes.stream()
                .sorted(Comparator.comparing(StarterProbe::category).thenComparing(StarterProbe::name))
                .map(probe -> probe.diagnose(applicationContext))
                .toList();
    }

    @Override
    public StarterProbeResultVO diagnose(String starterName) {
        return findProbe(starterName).diagnose(applicationContext);
    }

    @Override
    public StarterProbeResultVO smokeTest(String starterName) {
        if (!starterLabProperties.isSmokeTestEnabled()) {
            StarterProbe probe = findProbe(starterName);
            StarterProbeResultVO diagnose = probe.diagnose(applicationContext);
            return new StarterProbeResultVO(
                    diagnose.name(),
                    diagnose.category(),
                    diagnose.description(),
                    StarterProbeStatus.SKIPPED,
                    "冒烟测试已关闭，请设置 kuma.lab.starter.smoke-test-enabled=true",
                    diagnose.steps(),
                    diagnose.details()
            );
        }
        return findProbe(starterName).smokeTest(applicationContext);
    }

    @Override
    public StarterScenarioVO runScenario() {
        List<StarterProbeResultVO> results = probes.stream()
                .filter(StarterProbe::supports)
                .sorted(Comparator.comparing(StarterProbe::category).thenComparing(StarterProbe::name))
                .map(probe -> starterLabProperties.isSmokeTestEnabled()
                        ? probe.smokeTest(applicationContext)
                        : probe.diagnose(applicationContext))
                .toList();

        int passed = (int) results.stream().filter(item -> item.status() == StarterProbeStatus.PASSED).count();
        int failed = (int) results.stream().filter(item -> item.status() == StarterProbeStatus.FAILED).count();
        int skipped = (int) results.stream().filter(item -> item.status() == StarterProbeStatus.SKIPPED).count();

        return new StarterScenarioVO(results.size(), passed, failed, skipped, results);
    }

    private StarterProbe findProbe(String starterName) {
        return probes.stream()
                .filter(probe -> probe.name().equals(starterName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到 Starter 探测器: " + starterName));
    }

}
