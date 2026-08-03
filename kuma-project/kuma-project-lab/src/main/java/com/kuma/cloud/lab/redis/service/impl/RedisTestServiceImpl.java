package com.kuma.cloud.lab.redis.service.impl;

import com.kuma.boot.cache.redis.model.CacheKey;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.cloud.lab.redis.config.RedisLabProperties;
import com.kuma.cloud.lab.redis.domain.dto.RedisHashWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisListWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisSetWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisStringWriteDTO;
import com.kuma.cloud.lab.redis.domain.vo.RedisOperationStepVO;
import com.kuma.cloud.lab.redis.domain.vo.RedisScenarioVO;
import com.kuma.cloud.lab.redis.domain.vo.RedisValueVO;
import com.kuma.cloud.lab.redis.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisTestServiceImpl implements RedisTestService {

    private final RedisRepository redisRepository;
    private final RedisLabProperties redisLabProperties;

    @Override
    public RedisScenarioVO runScenario() {
        String prefix = redisLabProperties.getKeyPrefix();
        String stringKey = prefix + "scenario:string";
        String hashKey = prefix + "scenario:hash";
        String listKey = prefix + "scenario:list";
        String setKey = prefix + "scenario:set";

        cleanupScenarioKeys(stringKey, hashKey, listKey, setKey);

        List<RedisOperationStepVO> steps = new ArrayList<>();

        steps.add(step("SETEX string", stringKey,
                readValue(stringKey),
                writeAndRead(stringKey, "kuma-lab", 300L)));

        steps.add(step("HSET hash", hashKey,
                readValue(hashKey),
                writeHashAndRead(hashKey, "name", "KumaFramework")));

        steps.add(step("LPUSH list", listKey,
                readValue(listKey),
                writeListAndRead(listKey, true, List.of("first", "second", "third"))));

        steps.add(step("SADD set", setKey,
                readValue(setKey),
                writeSetAndRead(setKey, List.of("alpha", "beta", "gamma"))));

        steps.add(step("DEL string", stringKey,
                readValue(stringKey),
                deleteAndRead(stringKey)));

        return new RedisScenarioVO(prefix, steps);
    }

    @Override
    public RedisValueVO writeString(RedisStringWriteDTO dto) {
        String key = resolveKey(dto.getKey());
        if (dto.getTtlSeconds() != null && dto.getTtlSeconds() > 0) {
            redisRepository.setEx(key, dto.getValue(), dto.getTtlSeconds());
        } else {
            redisRepository.set(key, dto.getValue());
        }
        return readValue(key);
    }

    @Override
    public RedisValueVO readString(String key) {
        return readValue(resolveKey(key));
    }

    @Override
    public RedisValueVO writeHash(RedisHashWriteDTO dto) {
        String key = resolveKey(dto.getKey());
        redisRepository.hSet(key, dto.getField(), dto.getValue());
        return readValue(key);
    }

    @Override
    public RedisValueVO readHash(String key) {
        return readValue(resolveKey(key));
    }

    @Override
    public RedisValueVO writeList(RedisListWriteDTO dto) {
        String key = resolveKey(dto.getKey());
        Object[] values = dto.getValues().toArray();
        if (dto.isLeftPush()) {
            redisRepository.lPush(key, values);
        } else {
            redisRepository.rPush(key, values);
        }
        return readValue(key);
    }

    @Override
    public RedisValueVO readList(String key) {
        return readValue(resolveKey(key));
    }

    @Override
    public RedisValueVO writeSet(RedisSetWriteDTO dto) {
        String key = resolveKey(dto.getKey());
        redisRepository.sAdd(new CacheKey(key), dto.getMembers().toArray());
        return readValue(key);
    }

    @Override
    public RedisValueVO readSet(String key) {
        return readValue(resolveKey(key));
    }

    @Override
    public Long delete(String key) {
        return redisRepository.del(new CacheKey(resolveKey(key)));
    }

    private void cleanupScenarioKeys(String... keys) {
        for (String key : keys) {
            redisRepository.del(new CacheKey(key));
        }
    }

    private RedisOperationStepVO step(String operation, String key, Object before, RedisValueVO after) {
        return new RedisOperationStepVO(operation, key, before, after.value(), after.ttlSeconds());
    }

    private RedisValueVO writeAndRead(String key, String value, long ttlSeconds) {
        redisRepository.setEx(key, value, ttlSeconds);
        return readValue(key);
    }

    private RedisValueVO writeHashAndRead(String key, String field, String value) {
        redisRepository.hSet(key, field, value);
        return readValue(key);
    }

    private RedisValueVO writeListAndRead(String key, boolean leftPush, List<String> values) {
        Object[] array = values.toArray();
        if (leftPush) {
            redisRepository.lPush(key, array);
        } else {
            redisRepository.rPush(key, array);
        }
        return readValue(key);
    }

    private RedisValueVO writeSetAndRead(String key, List<String> members) {
        redisRepository.sAdd(new CacheKey(key), members.toArray());
        return readValue(key);
    }

    private RedisValueVO deleteAndRead(String key) {
        redisRepository.del(new CacheKey(key));
        return readValue(key);
    }

    private RedisValueVO readValue(String key) {
        if (!Boolean.TRUE.equals(redisRepository.exists(key))) {
            return new RedisValueVO(key, "none", null, null);
        }

        DataType dataType = redisRepository.type(key);
        String type = dataType == null ? "unknown" : dataType.code();
        Object value = switch (type) {
            case "string" -> redisRepository.get(key);
            case "hash" -> new LinkedHashMap<>(redisRepository.<String, String>hGetAll(key));
            case "list" -> redisRepository.lRange(key, 0, -1);
            case "set" -> new LinkedHashSet<>(redisRepository.<String>sMembers(new CacheKey(key)));
            default -> null;
        };
        Long ttl = normalizeTtl(redisRepository.ttl(key));
        return new RedisValueVO(key, type, value, ttl);
    }

    private Long normalizeTtl(Long ttl) {
        if (ttl == null || ttl < 0) {
            return null;
        }
        return ttl;
    }

    private String resolveKey(String key) {
        if (!StringUtils.hasText(key)) {
            return redisLabProperties.getKeyPrefix();
        }
        if (key.startsWith(redisLabProperties.getKeyPrefix())) {
            return key;
        }
        return redisLabProperties.getKeyPrefix() + key;
    }

}
