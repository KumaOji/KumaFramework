package com.kuma.boot.idempotent.idempotentenhance.core.registry;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.helper.IdempotentTemplate;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 幂等repository注册中心
 * <p>
 * {@link IdempotentRepositoryRegistrySupport} {@link IdempotentTemplate}
 *
 * @author wenpan 2023/01/08 22:00
 */
public final class IdempotentRepositoryRegistry {

    public static final String PRIMARY = "primary";

    private static final Map<String, IdempotentRepository> IDEMPOTENT_REPOSITORY_CACHE = Maps.newConcurrentMap();

    public static void registerPrimary(IdempotentRepository idempotentRepository) {
        register(PRIMARY, idempotentRepository);
    }

    public static IdempotentRepository getPrimaryRepository() {
        return IDEMPOTENT_REPOSITORY_CACHE.get(PRIMARY);
    }

    /**
     * 注册幂等repository到注册中
     *
     * @param name                 名称
     * @param idempotentRepository idempotentRepository
     * @author wenpan 2023/1/8 10:12 下午
     */
    public static void register(String name, IdempotentRepository idempotentRepository) {
        if (StringUtils.isBlank(name) || Objects.isNull(idempotentRepository)) {
            throw new IllegalArgumentException("register repository failed, name and idempotentRepository can not be null.");
        }
        synchronized (IdempotentRepositoryRegistry.class) {
            // 强制不允许覆盖
            if (IDEMPOTENT_REPOSITORY_CACHE.containsKey(name)) {
                throw new IllegalArgumentException(String.format("[%s] already exists in registry.", name));
            }
            IDEMPOTENT_REPOSITORY_CACHE.put(name, idempotentRepository);
        }
    }

    /**
     * 从注册中心按名称查询repository
     *
     * @param name name
     * @return org.enhance.idempotent.core.repository.IdempotentRepository
     */
    public static IdempotentRepository find(String name) {

        return IDEMPOTENT_REPOSITORY_CACHE.get(name);
    }

    /**
     * 从注册中心按名称查询repository, 找不到则抛出异常
     *
     * @param name name
     * @return org.enhance.idempotent.core.repository.IdempotentRepository
     */
    public static IdempotentRepository findOrElseException(String name) {
        IdempotentRepository repository = IDEMPOTENT_REPOSITORY_CACHE.get(name);
        if (Objects.isNull(repository)) {
            throw new IllegalArgumentException(String.format("can not found repository by name [%s]", name));
        }
        return repository;
    }

    /**
     * 通过名称在注册中心中查找IdempotentRepository，如果找不到，则返回primary
     *
     * @param name name
     * @return org.enhance.idempotent.core.repository.IdempotentRepository
     * @author wenpan 2023/1/8 10:47 下午
     */
    public static IdempotentRepository findOrElsePrimary(String name) {
        return IDEMPOTENT_REPOSITORY_CACHE.get(name) == null ?
                IDEMPOTENT_REPOSITORY_CACHE.get(PRIMARY) : IDEMPOTENT_REPOSITORY_CACHE.get(name);
    }

    public static Collection<IdempotentRepository> listIdempotentRepositories() {
        return IDEMPOTENT_REPOSITORY_CACHE.values();
    }

    public static Collection<String> listIdempotentRepositoryNames() {
        return IDEMPOTENT_REPOSITORY_CACHE.keySet();
    }

}
