package com.kuma.boot.idempotent.idempotentenhance.adapter.redis;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.adapter.redis.config.properties.IdempotentRedisAdapterProperties;
import org.apache.commons.lang3.StringUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.em.IdempotentStatusEnum;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;
import java.util.Optional;

/**
 * @author wenpan 2023/01/02 22:40
 */
public class RedisIdempotentRepositoryImpl implements IdempotentRepository {

    /**
     * 创建幂等记录lua脚本
     */
    private static final String CREATE_LUA = "script/lua/create.lua";

    /**
     * 修改幂等状态为成功lua脚本
     */
    private static final String STATE_FLOW_LUA = "script/lua/state-flow.lua";

    /**
     * 删除幂等记录lua脚本
     */
    private static final String DELETE_LUA = "script/lua/delete.lua";

    private static final DefaultRedisScript<Boolean> CREATE_SCRIPT;

    private static final DefaultRedisScript<Boolean> STATE_FLOW_SCRIPT;

    private static final DefaultRedisScript<Boolean> DELETE_SCRIPT;

    static {
        CREATE_SCRIPT = new DefaultRedisScript<>();
        CREATE_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(CREATE_LUA)));
        CREATE_SCRIPT.setResultType(Boolean.class);

        STATE_FLOW_SCRIPT = new DefaultRedisScript<>();
        STATE_FLOW_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(STATE_FLOW_LUA)));
        STATE_FLOW_SCRIPT.setResultType(Boolean.class);

        DELETE_SCRIPT = new DefaultRedisScript<>();
        DELETE_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(DELETE_LUA)));
        DELETE_SCRIPT.setResultType(Boolean.class);
    }

    private final StringRedisTemplate redisTemplate;

    private final IdempotentRedisAdapterProperties properties;

    public RedisIdempotentRepositoryImpl(StringRedisTemplate redisTemplate,
                                         IdempotentRedisAdapterProperties properties) {
        if (redisTemplate == null) {
            throw new IdempotentException("redisTemplate can not be null.");
        }
        if (properties == null) {
            throw new IdempotentException("properties can not be null.");
        }
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean create(IdempotentEntity idempotentEntity) {
        Boolean execute = doExecuteScript(idempotentEntity, CREATE_SCRIPT);
        if (!Boolean.TRUE.equals(execute)) {
            LogUtils.error("create idempotent record failed, because return value is false," +
                    " uniqueKey is : {}", idempotentEntity.getUniqueKey());
        }
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public Boolean changeIdempotentStatusProcessing(IdempotentEntity idempotentEntity) {
        // 再次显示设置状态为处理中
        idempotentEntity.setIdempotentStatus(IdempotentStatusEnum.PROCESSING.getStatus());
        // 版本号 +1
        idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() + 1);
        Boolean result = doExecuteScript(idempotentEntity, STATE_FLOW_SCRIPT);
        if (!Boolean.TRUE.equals(result)) {
            // 更新不成功，则版本号-1
            idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() - 1);
        }
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean changeIdempotentStatusSuccess(IdempotentEntity idempotentEntity) {
        // 再次显示设置状态为处理成功
        idempotentEntity.setIdempotentStatus(IdempotentStatusEnum.SUCCESS.getStatus());
        // 版本号 +1
        idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() + 1);
        Boolean result = doExecuteScript(idempotentEntity, STATE_FLOW_SCRIPT);
        if (!Boolean.TRUE.equals(result)) {
            // 更新不成功，则版本号-1
            idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() - 1);
        }
        return Boolean.TRUE.equals(result);
    }

    @Override
    public Boolean delete(IdempotentEntity idempotentEntity) {
        String uniqueKey = format(properties.getKeyPrefix(), idempotentEntity.getUniqueKey());
        String uniqueDataKey = format(properties.getDataKeyPrefix(), idempotentEntity.getUniqueKey());
        // 严格检查要删除的key不能都为空
        if (StringUtils.isAllBlank(uniqueKey, uniqueDataKey)) {
            return true;
        }
        Boolean execute = redisTemplate.execute(DELETE_SCRIPT, Lists.newArrayList(uniqueKey, uniqueDataKey));
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public Optional<IdempotentEntity> query(IdempotentEntity idempotentEntity) {
        String uniqueDataKey = format(properties.getDataKeyPrefix(), idempotentEntity.getUniqueKey());
        String result = redisTemplate.opsForValue().get(uniqueDataKey);
        // 从Redis里查询
        return Optional.ofNullable(JSON.parseObject(result, IdempotentEntity.class));
    }

    public String format(String prefix, String key) {
        return prefix + key;
    }

    /**
     * <p>
     * 执行lua脚本, 主要用于执行新增幂等记录和修改幂等记录lua脚本
     * </p>
     *
     * @param idempotentEntity 幂等实体
     * @param script           脚本
     * @return java.lang.Boolean
     * @author wenpan 2023/1/5 10:11 下午
     */
    private Boolean doExecuteScript(IdempotentEntity idempotentEntity, DefaultRedisScript<Boolean> script) {
        // 存放 幂等key : 版本号
        String uniqueKey = format(properties.getKeyPrefix(), idempotentEntity.getUniqueKey());
        // 存放 幂等key : 幂等记录
        String uniqueDataKey = format(properties.getDataKeyPrefix(), idempotentEntity.getUniqueKey());
        // 期望的版本号(新增幂等记录idempotentEntity时版本号为1，不用增加)
        long expectVersion = idempotentEntity.getObjectVersionNumber() == 1L ?
                idempotentEntity.getObjectVersionNumber() : idempotentEntity.getObjectVersionNumber() - 1;
        // 过期时间
        String expireTime = String.valueOf(properties.getUnit().toSeconds(properties.getExpireTime()));
        // 幂等记录转为string存储
        String jsonStr = JSON.toJSONString(idempotentEntity);
        List<String> keys = Lists.newArrayList(uniqueKey, uniqueDataKey);
        List<String> argv = Lists.newArrayList(String.valueOf(expectVersion), expireTime, jsonStr);
        // 执行脚本
        return redisTemplate.execute(script, keys, argv.toArray());
    }

}
