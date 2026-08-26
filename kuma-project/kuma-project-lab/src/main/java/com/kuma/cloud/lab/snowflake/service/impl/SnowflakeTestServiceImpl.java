package com.kuma.cloud.lab.snowflake.service.impl;

import com.kuma.boot.common.utils.common.SequenceUtils;
import com.kuma.cloud.lab.snowflake.config.SnowflakeLabProperties;
import com.kuma.cloud.lab.snowflake.domain.dto.SnowflakeGenerateDTO;
import com.kuma.cloud.lab.snowflake.domain.vo.SnowflakeGenerateVO;
import com.kuma.cloud.lab.snowflake.domain.vo.SnowflakeOperationStepVO;
import com.kuma.cloud.lab.snowflake.domain.vo.SnowflakeScenarioVO;
import com.kuma.cloud.lab.snowflake.service.SnowflakeTestService;
import com.kuma.cloud.lab.snowflake.support.SnowflakeIdParser;
import com.kuma.cloud.lab.snowflake.support.SnowflakeIdParser.ParsedSnowflakeId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnowflakeTestServiceImpl implements SnowflakeTestService {

    private final SequenceUtils snowflakeSequenceUtils;
    private final SnowflakeLabProperties snowflakeLabProperties;

    @Override
    public SnowflakeScenarioVO runScenario() {
        int batchSize = snowflakeLabProperties.getBatchSize();
        List<SnowflakeOperationStepVO> steps = new ArrayList<>();
        List<Long> ids = new ArrayList<>(batchSize);
        Set<Long> uniqueIds = new HashSet<>();

        for (int i = 0; i < batchSize; i++) {
            long id = snowflakeSequenceUtils.nextId();
            ids.add(id);
            uniqueIds.add(id);
            ParsedSnowflakeId parsed = SnowflakeIdParser.parse(id);
            steps.add(new SnowflakeOperationStepVO(
                    "NEXT_ID",
                    parsed.id(),
                    parsed.timestampText(),
                    parsed.datacenterId(),
                    parsed.workerId(),
                    parsed.sequence(),
                    "生成全局唯一 ID"));
        }

        boolean strictlyIncreasing = true;
        for (int i = 1; i < ids.size(); i++) {
            if (ids.get(i) <= ids.get(i - 1)) {
                strictlyIncreasing = false;
                break;
            }
        }

        steps.add(new SnowflakeOperationStepVO(
                "UNIQUE_CHECK",
                0,
                null,
                snowflakeLabProperties.getDatacenterId(),
                snowflakeLabProperties.getWorkerId(),
                uniqueIds.size(),
                uniqueIds.size() == batchSize ? "本批次 ID 无重复" : "检测到重复 ID"));

        steps.add(new SnowflakeOperationStepVO(
                "ORDER_CHECK",
                0,
                null,
                snowflakeLabProperties.getDatacenterId(),
                snowflakeLabProperties.getWorkerId(),
                batchSize,
                strictlyIncreasing ? "ID 按时间趋势递增" : "同毫秒内序列号变化，整体不一定严格递增"));

        return new SnowflakeScenarioVO(
                snowflakeLabProperties.getWorkerId(),
                snowflakeLabProperties.getDatacenterId(),
                batchSize,
                strictlyIncreasing,
                steps);
    }

    @Override
    public SnowflakeGenerateVO generate(SnowflakeGenerateDTO dto) {
        List<ParsedSnowflakeId> ids = new ArrayList<>(dto.getCount());
        for (int i = 0; i < dto.getCount(); i++) {
            ids.add(SnowflakeIdParser.parse(snowflakeSequenceUtils.nextId()));
        }
        return new SnowflakeGenerateVO(dto.getCount(), ids);
    }

    @Override
    public ParsedSnowflakeId parse(long id) {
        return SnowflakeIdParser.parse(id);
    }

}
