package com.kuma.cloud.lab.redis.service;

import com.kuma.cloud.lab.redis.domain.dto.RedisHashWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisListWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisSetWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisStringWriteDTO;
import com.kuma.cloud.lab.redis.domain.vo.RedisScenarioVO;
import com.kuma.cloud.lab.redis.domain.vo.RedisValueVO;

public interface RedisTestService {

    RedisScenarioVO runScenario();

    RedisValueVO writeString(RedisStringWriteDTO dto);

    RedisValueVO readString(String key);

    RedisValueVO writeHash(RedisHashWriteDTO dto);

    RedisValueVO readHash(String key);

    RedisValueVO writeList(RedisListWriteDTO dto);

    RedisValueVO readList(String key);

    RedisValueVO writeSet(RedisSetWriteDTO dto);

    RedisValueVO readSet(String key);

    Long delete(String key);

}
