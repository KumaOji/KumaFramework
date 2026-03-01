/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.kuma.boot.common.utils.lang.StringUtils
 */
package com.kuma.boot.idempotent.idempotentenhance.core.registry;

import com.google.common.collect.Maps;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class IdempotentRepositoryRegistry {
    public static final String PRIMARY = "primary";
    private static IdempotentRepository primaryRepository;
    private static final Map<String, IdempotentRepository> IDEMPOTENT_REPOSITORY_CACHE;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void register(String name, IdempotentRepository idempotentRepository) {
        if (StringUtils.isBlank((String)name) || Objects.isNull(idempotentRepository)) {
            throw new IllegalArgumentException("register repository failed, name and idempotentRepository can not be null.");
        }
        Class<IdempotentRepositoryRegistry> clazz = IdempotentRepositoryRegistry.class;
        synchronized (IdempotentRepositoryRegistry.class) {
            if (IDEMPOTENT_REPOSITORY_CACHE.containsKey(name)) {
                throw new IllegalArgumentException(String.format("[%s] already exists in registry.", name));
            }
            IDEMPOTENT_REPOSITORY_CACHE.put(name, idempotentRepository);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public static IdempotentRepository find(String name) {
        return IDEMPOTENT_REPOSITORY_CACHE.get(name);
    }

    public static IdempotentRepository findOrElseException(String name) {
        IdempotentRepository repository = IDEMPOTENT_REPOSITORY_CACHE.get(name);
        if (Objects.isNull(repository)) {
            throw new IllegalArgumentException(String.format("can not found repository by name [%s]", name));
        }
        return repository;
    }

    public static IdempotentRepository findOrElsePrimary(String name) {
        return IDEMPOTENT_REPOSITORY_CACHE.get(name) == null ? primaryRepository : IDEMPOTENT_REPOSITORY_CACHE.get(name);
    }

    public static Collection<IdempotentRepository> getIdempotentRepositories() {
        return IDEMPOTENT_REPOSITORY_CACHE.values();
    }

    public static Collection<String> getIdempotentRepositoryNames() {
        return IDEMPOTENT_REPOSITORY_CACHE.keySet();
    }

    public static IdempotentRepository getPrimaryRepository() {
        return primaryRepository;
    }

    public static void setPrimaryRepository(IdempotentRepository primaryRepository) {
        IdempotentRepositoryRegistry.primaryRepository = primaryRepository;
    }

    static {
        IDEMPOTENT_REPOSITORY_CACHE = Maps.newConcurrentMap();
    }
}

