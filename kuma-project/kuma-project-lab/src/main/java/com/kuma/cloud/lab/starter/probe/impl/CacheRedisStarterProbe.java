package com.kuma.cloud.lab.starter.probe.impl;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.cloud.lab.starter.config.StarterLabProperties;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeStepVO;
import com.kuma.cloud.lab.starter.probe.AbstractStarterProbe;
import com.kuma.cloud.lab.starter.support.StarterProbeStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheRedisStarterProbe extends AbstractStarterProbe {

    private final StarterLabProperties starterLabProperties;

    public CacheRedisStarterProbe(StarterLabProperties starterLabProperties) {
        super(
                StarterNameConstants.CACHE_REDIS_STARTER,
                "com.kuma.boot.cache.redis.autoconfigure.RedisAutoConfiguration",
                "Redis 缓存仓库与延迟队列"
        );
        this.starterLabProperties = starterLabProperties;
    }

    @Override
    protected StarterProbeResultVO doDiagnose(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        boolean repositoryReady = hasBean(applicationContext, RedisRepository.class);
        steps.add(step("bean.redisRepository", repositoryReady,
                repositoryReady ? "RedisRepository Bean 已注册" : "RedisRepository Bean 缺失"));
        return result(
                repositoryReady ? StarterProbeStatus.READY : StarterProbeStatus.FAILED,
                repositoryReady ? "Redis Starter 已就绪" : "Redis Starter Bean 未就绪",
                steps,
                detailsOf("redisRepositoryBeans", applicationContext.getBeanNamesForType(RedisRepository.class).length)
        );
    }

    @Override
    protected StarterProbeResultVO doSmokeTest(
            ApplicationContext applicationContext,
            List<StarterProbeStepVO> steps
    ) {
        if (!hasBean(applicationContext, RedisRepository.class)) {
            steps.add(step("redis.ping", false, "RedisRepository 不可用"));
            return result(StarterProbeStatus.SKIPPED, "Redis 未配置，跳过冒烟测试", steps, detailsOf());
        }
        String key = starterLabProperties.getKeyPrefix() + "probe";
        try {
            RedisRepository redisRepository = requireBean(applicationContext, RedisRepository.class, "RedisRepository");
            redisRepository.setEx(key, "ok", 30);
            Object value = redisRepository.get(key);
            boolean success = "ok".equals(String.valueOf(value));
            steps.add(step("redis.set-get", success, success ? "写入并读取成功" : "读取值不匹配"));
            redisRepository.del(key);
            return result(
                    success ? StarterProbeStatus.PASSED : StarterProbeStatus.FAILED,
                    success ? "Redis Starter 冒烟测试通过" : "Redis Starter 冒烟测试失败",
                    steps,
                    detailsOf("key", key, "value", value)
            );
        } catch (Exception error) {
            steps.add(step("redis.ping", false, error.getMessage()));
            return result(
                    StarterProbeStatus.SKIPPED,
                    "Redis 未连通，跳过冒烟测试: " + error.getMessage(),
                    steps,
                    detailsOf("key", key)
            );
        }
    }

}
