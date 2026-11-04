package com.kuma.cloud.lab.p0;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;

@Tag(name = "Redis 缓存与分布式锁进阶实验")
@RestController
@RequestMapping("/lab/redis/advanced")
public class RedisAdvancedLabController {
    private final DataSource source;
    private final StringRedisTemplate redis;
    public RedisAdvancedLabController(DataSource source, RedisRepository repository) {
        this.source = source; this.redis = new StringRedisTemplate(repository.getConnectionFactory());
    }
    @Operation(summary = "Redis 完整进阶实验：穿透击穿雪崩、一致性、租约锁与Lua")
    @PostMapping("/scenario")
    public Result<LabReport> scenario() throws Exception { return run("all"); }
    @Operation(summary = "真实MySQL回源：空值缓存防穿透与互斥重建防击穿")
    @PostMapping("/cache")
    public Result<LabReport> cache() throws Exception { return run("cache"); }
    @Operation(summary = "集中到期与错峰TTL：观察首波缓存雪崩")
    @PostMapping("/avalanche")
    public Result<LabReport> avalanche() throws Exception { return run("avalanche"); }
    @Operation(summary = "真实MySQL/Redis复现旧值回填与共用锁协调")
    @PostMapping("/consistency")
    public Result<LabReport> consistency() throws Exception { return run("consistency"); }
    @Operation(summary = "锁租约到期、错误DEL、token校验Lua解锁与手动续期")
    @PostMapping("/locks")
    public Result<LabReport> locks() throws Exception { return run("locks"); }
    @Operation(summary = "并发Lua原子库存扣减，验证无超卖")
    @PostMapping("/lua")
    public Result<LabReport> lua() throws Exception { return run("lua"); }
    @Operation(summary = "锁过期后陈旧写入、单调fencing token与MySQL下游写入屏障")
    @PostMapping("/fencing")
    public Result<LabReport> fencing() throws Exception { return run("fencing"); }
    private Result<LabReport> run(String lesson) throws Exception { return Result.success(new RedisAdvancedLearningDemo(redis).run(source, lesson)); }
}
