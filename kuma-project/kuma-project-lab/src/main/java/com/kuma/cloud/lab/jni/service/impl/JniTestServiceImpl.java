package com.kuma.cloud.lab.jni.service.impl;

import com.kuma.cloud.lab.jni.NativeMath;
import com.kuma.cloud.lab.jni.config.JniLabProperties;
import com.kuma.cloud.lab.jni.domain.dto.JniBinaryOpDTO;
import com.kuma.cloud.lab.jni.domain.dto.JniGreetDTO;
import com.kuma.cloud.lab.jni.domain.dto.JniSumDTO;
import com.kuma.cloud.lab.jni.domain.vo.JniOperationStepVO;
import com.kuma.cloud.lab.jni.domain.vo.JniScenarioVO;
import com.kuma.cloud.lab.jni.domain.vo.JniValueVO;
import com.kuma.cloud.lab.jni.service.JniTestService;
import com.kuma.cloud.lab.jni.support.JniLibraryLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JniTestServiceImpl implements JniTestService {

    private final JniLabProperties jniLabProperties;

    @Override
    public JniScenarioVO runScenario() {
        ensureEnabled();

        List<JniOperationStepVO> steps = new ArrayList<>();
        steps.add(step("add", Map.of("left", 21, "right", 21), NativeMath.add(21, 21)));
        steps.add(step("multiply", Map.of("left", 6, "right", 7), NativeMath.multiply(6, 7)));
        steps.add(step("greet", Map.of("name", "KumaFramework"), NativeMath.greet("KumaFramework")));
        steps.add(step("sumArray", List.of(1, 2, 3, 4, 5), NativeMath.sumArray(new int[]{1, 2, 3, 4, 5})));

        return new JniScenarioVO(
                JniLibraryLoader.isLoaded(),
                JniLibraryLoader.loadedPath(),
                System.getProperty("os.name") + " / " + System.getProperty("os.arch"),
                steps
        );
    }

    @Override
    public JniValueVO add(JniBinaryOpDTO dto) {
        ensureEnabled();
        Map<String, Integer> input = Map.of("left", dto.getLeft(), "right", dto.getRight());
        return new JniValueVO("add", input, NativeMath.add(dto.getLeft(), dto.getRight()));
    }

    @Override
    public JniValueVO multiply(JniBinaryOpDTO dto) {
        ensureEnabled();
        Map<String, Integer> input = Map.of("left", dto.getLeft(), "right", dto.getRight());
        return new JniValueVO("multiply", input, NativeMath.multiply(dto.getLeft(), dto.getRight()));
    }

    @Override
    public JniValueVO greet(JniGreetDTO dto) {
        ensureEnabled();
        return new JniValueVO("greet", Map.of("name", dto.getName()), NativeMath.greet(dto.getName()));
    }

    @Override
    public JniValueVO sum(JniSumDTO dto) {
        ensureEnabled();
        int[] values = dto.getValues().stream().mapToInt(Integer::intValue).toArray();
        return new JniValueVO("sumArray", dto.getValues(), NativeMath.sumArray(values));
    }

    private JniOperationStepVO step(String operation, Object input, Object output) {
        return new JniOperationStepVO(operation, input, output);
    }

    private void ensureEnabled() {
        if (!jniLabProperties.isEnabled()) {
            throw new IllegalStateException("JNI lab test is disabled. Set kuma.lab.jni.enabled=true first.");
        }
    }

}
