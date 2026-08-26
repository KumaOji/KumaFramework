package com.kuma.cloud.lab.bloom.service;

import com.kuma.cloud.lab.bloom.domain.dto.BloomContainsDTO;
import com.kuma.cloud.lab.bloom.domain.vo.BloomContainsVO;
import com.kuma.cloud.lab.bloom.domain.vo.BloomScenarioVO;

public interface BloomTestService {

    BloomScenarioVO runScenario();

    BloomContainsVO add(String value);

    BloomContainsVO contains(BloomContainsDTO dto);

}
