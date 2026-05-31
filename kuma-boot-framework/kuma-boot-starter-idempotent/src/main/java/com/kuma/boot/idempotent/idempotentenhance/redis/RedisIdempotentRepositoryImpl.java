/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSONObject
 *  com.alibaba.fastjson2.JSONWriter$Feature
 *  com.google.common.collect.Lists
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.data.redis.core.StringRedisTemplate
 *  org.springframework.data.redis.core.script.DefaultRedisScript
 *  org.springframework.scripting.ScriptSource
 *  org.springframework.scripting.support.ResourceScriptSource
 */
package com.kuma.boot.idempotent.idempotentenhance.redis;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.em.IdempotentStatusEnum;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import com.kuma.boot.idempotent.idempotentenhance.redis.config.properties.IdempotentRedisAdapterProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

public class RedisIdempotentRepositoryImpl
implements IdempotentRepository {
    private static final String CREATE_LUA = "script/lua/create.lua";
    private static final String STATE_FLOW_LUA = "script/lua/state-flow.lua";
    private static final String DELETE_LUA = "script/lua/delete.lua";
    private static final DefaultRedisScript<Boolean> CREATE_SCRIPT = new DefaultRedisScript<>();
    private static final DefaultRedisScript<Boolean> STATE_FLOW_SCRIPT;
    private static final DefaultRedisScript<Boolean> DELETE_SCRIPT;
    private final StringRedisTemplate redisTemplate;
    private final IdempotentRedisAdapterProperties properties;

    public RedisIdempotentRepositoryImpl(StringRedisTemplate redisTemplate, IdempotentRedisAdapterProperties properties) {
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
        Boolean execute = this.doExecuteScript(idempotentEntity, CREATE_SCRIPT);
        if (!Boolean.TRUE.equals(execute)) {
            LogUtils.error((String)"create idempotent record failed, because return value is false, uniqueKey is : {}", (Object[])new Object[]{idempotentEntity.getUniqueKey()});
        }
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public Boolean changeIdempotentStatusProcessing(IdempotentEntity idempotentEntity) {
        idempotentEntity.setIdempotentStatus(IdempotentStatusEnum.PROCESSING.getStatus());
        idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() + 1L);
        Boolean result = this.doExecuteScript(idempotentEntity, STATE_FLOW_SCRIPT);
        if (!Boolean.TRUE.equals(result)) {
            idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() - 1L);
        }
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean changeIdempotentStatusSuccess(IdempotentEntity idempotentEntity) {
        idempotentEntity.setIdempotentStatus(IdempotentStatusEnum.SUCCESS.getStatus());
        idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() + 1L);
        Boolean result = this.doExecuteScript(idempotentEntity, STATE_FLOW_SCRIPT);
        if (!Boolean.TRUE.equals(result)) {
            idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() - 1L);
        }
        return Boolean.TRUE.equals(result);
    }

    @Override
    public Boolean delete(IdempotentEntity idempotentEntity) {
        String uniqueKey = this.format(this.properties.getKeyPrefix(), idempotentEntity.getUniqueKey());
        String uniqueDataKey = this.format(this.properties.getKeyPrefix(), idempotentEntity.getUniqueKey());
        if (StringUtils.isAllBlank((CharSequence[])new CharSequence[]{uniqueKey, uniqueDataKey})) {
            return true;
        }
        @SuppressWarnings("unchecked")
        Boolean execute = (Boolean)this.redisTemplate.execute(DELETE_SCRIPT, (List)Lists.newArrayList((Object[])new String[]{uniqueKey, uniqueDataKey}), new Object[0]);
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public Optional<IdempotentEntity> query(IdempotentEntity idempotentEntity) {
        String uniqueDataKey = this.format(this.properties.getDataKeyPrefix(), idempotentEntity.getUniqueKey());
        String result = (String)this.redisTemplate.opsForValue().get((Object)uniqueDataKey);
        return Optional.ofNullable((IdempotentEntity)JSONObject.parseObject((String)result, IdempotentEntity.class));
    }

    public String format(String prefix, String key) {
        return prefix + key;
    }

    private Boolean doExecuteScript(IdempotentEntity idempotentEntity, DefaultRedisScript<Boolean> script) {
        String uniqueKey = this.format(this.properties.getKeyPrefix(), idempotentEntity.getUniqueKey());
        String uniqueDataKey = this.format(this.properties.getDataKeyPrefix(), idempotentEntity.getUniqueKey());
        long expectVersion = idempotentEntity.getObjectVersionNumber() == 1L ? idempotentEntity.getObjectVersionNumber() : idempotentEntity.getObjectVersionNumber() - 1L;
        String expireTime = String.valueOf(this.properties.getUnit().toSeconds(this.properties.getExpireTime()));
        String jsonStr = JSONObject.toJSONString((Object)idempotentEntity, (JSONWriter.Feature[])new JSONWriter.Feature[0]);
        ArrayList keys = Lists.newArrayList((Object[])new String[]{uniqueKey, uniqueDataKey});
        ArrayList argv = Lists.newArrayList((Object[])new String[]{String.valueOf(expectVersion), expireTime, jsonStr});
        @SuppressWarnings("unchecked")
        Boolean result = (Boolean)this.redisTemplate.execute(script, (List)keys, argv.toArray());
        return result;
    }

    static {
        CREATE_SCRIPT.setScriptSource((ScriptSource)new ResourceScriptSource((Resource)new ClassPathResource(CREATE_LUA)));
        CREATE_SCRIPT.setResultType(Boolean.class);
        STATE_FLOW_SCRIPT = new DefaultRedisScript<>();
        STATE_FLOW_SCRIPT.setScriptSource((ScriptSource)new ResourceScriptSource((Resource)new ClassPathResource(STATE_FLOW_LUA)));
        STATE_FLOW_SCRIPT.setResultType(Boolean.class);
        DELETE_SCRIPT = new DefaultRedisScript<>();
        DELETE_SCRIPT.setScriptSource((ScriptSource)new ResourceScriptSource((Resource)new ClassPathResource(DELETE_LUA)));
        DELETE_SCRIPT.setResultType(Boolean.class);
    }
}

