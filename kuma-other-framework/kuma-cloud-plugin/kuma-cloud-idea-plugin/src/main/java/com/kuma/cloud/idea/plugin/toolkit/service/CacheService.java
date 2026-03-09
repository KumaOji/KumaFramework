package com.kuma.cloud.idea.plugin.toolkit.service;


public interface CacheService {
    void put(String key, Object vlaue);

    Object get(String key);
}
