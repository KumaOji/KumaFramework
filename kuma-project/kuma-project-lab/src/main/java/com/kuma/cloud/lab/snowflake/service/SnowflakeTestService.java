package com.kuma.cloud.lab.snowflake.service;

import com.kuma.cloud.lab.snowflake.domain.dto.SnowflakeGenerateDTO;
import com.kuma.cloud.lab.snowflake.domain.vo.SnowflakeGenerateVO;
import com.kuma.cloud.lab.snowflake.domain.vo.SnowflakeScenarioVO;
import com.kuma.cloud.lab.snowflake.support.SnowflakeIdParser.ParsedSnowflakeId;

public interface SnowflakeTestService {

    SnowflakeScenarioVO runScenario();

    SnowflakeGenerateVO generate(SnowflakeGenerateDTO dto);

    ParsedSnowflakeId parse(long id);

}
