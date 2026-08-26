package com.kuma.cloud.lab.bloom.service.impl;

import com.kuma.cloud.lab.bloom.config.BloomLabProperties;
import com.kuma.cloud.lab.bloom.domain.dto.BloomContainsDTO;
import com.kuma.cloud.lab.bloom.domain.vo.BloomContainsVO;
import com.kuma.cloud.lab.bloom.domain.vo.BloomOperationStepVO;
import com.kuma.cloud.lab.bloom.domain.vo.BloomScenarioVO;
import com.kuma.cloud.lab.bloom.service.BloomTestService;
import com.kuma.cloud.lab.bloom.support.SimpleBloomFilter;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BloomTestServiceImpl implements BloomTestService {

    private final BloomLabProperties bloomLabProperties;

    private SimpleBloomFilter bloomFilter;

    @PostConstruct
    void init() {
        bloomFilter = new SimpleBloomFilter(
                bloomLabProperties.getExpectedInsertions(),
                bloomLabProperties.getFalsePositiveProbability());
    }

    @Override
    public BloomScenarioVO runScenario() {
        SimpleBloomFilter filter = new SimpleBloomFilter(
                bloomLabProperties.getExpectedInsertions(),
                bloomLabProperties.getFalsePositiveProbability());

        List<BloomOperationStepVO> steps = new ArrayList<>();
        List<String> inserted = List.of("user:1001", "user:1002", "user:1003", "order:90001");
        for (String value : inserted) {
            filter.add(value);
            steps.add(new BloomOperationStepVO("ADD", value, true, "元素已写入布隆过滤器"));
        }

        for (String value : inserted) {
            boolean exists = filter.mightContain(value);
            steps.add(new BloomOperationStepVO(
                    "CONTAINS",
                    value,
                    exists,
                    exists ? "已插入元素，判定为可能存在（无假阴性）" : "不应出现：布隆过滤器不存在假阴性"));
        }

        String absent = "user:9999";
        boolean absentResult = filter.mightContain(absent);
        steps.add(new BloomOperationStepVO(
                "CONTAINS",
                absent,
                absentResult,
                absentResult
                        ? "未插入元素但判定为可能存在，属于允许的假阳性"
                        : "未插入元素，判定为一定不存在"));

        return new BloomScenarioVO(
                filter.getBitSize(),
                filter.getHashFunctions(),
                filter.getInsertedCount(),
                filter.getSetBitCount(),
                steps);
    }

    @Override
    public BloomContainsVO add(String value) {
        bloomFilter.add(value);
        return new BloomContainsVO(value, true, "元素已写入，后续查询可能返回 true");
    }

    @Override
    public BloomContainsVO contains(BloomContainsDTO dto) {
        boolean mightContain = bloomFilter.mightContain(dto.getValue());
        String interpretation = mightContain
                ? "可能存在（若为未插入元素，则为假阳性）"
                : "一定不存在（布隆过滤器无假阴性）";
        return new BloomContainsVO(dto.getValue(), mightContain, interpretation);
    }

}
