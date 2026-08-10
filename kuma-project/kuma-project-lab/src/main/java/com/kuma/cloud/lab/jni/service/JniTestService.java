package com.kuma.cloud.lab.jni.service;

import com.kuma.cloud.lab.jni.domain.dto.JniBinaryOpDTO;
import com.kuma.cloud.lab.jni.domain.dto.JniGreetDTO;
import com.kuma.cloud.lab.jni.domain.dto.JniSumDTO;
import com.kuma.cloud.lab.jni.domain.vo.JniScenarioVO;
import com.kuma.cloud.lab.jni.domain.vo.JniValueVO;

public interface JniTestService {

    JniScenarioVO runScenario();

    JniValueVO add(JniBinaryOpDTO dto);

    JniValueVO multiply(JniBinaryOpDTO dto);

    JniValueVO greet(JniGreetDTO dto);

    JniValueVO sum(JniSumDTO dto);

}
