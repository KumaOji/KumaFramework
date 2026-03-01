/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通用缓存接口
 */
public interface ICommonCacheService {

    /**
     * 设置
     *
     * @param key   key
     * @param value 值
     */
    void set(String key, String value);

    /**
     * 设置对应的值
     *
     * @param key         健
     * @param value       值
     * @param expireMills 过期的耗秒数  过期时间小于等于0，认为不过期
     */
    void set(String key, String value, long expireMills);

    /**
     * 设置对应的值
     *
     * @param key   键
     * @param value 值
     * @param nxxx  NX
     * @param expx  PX
     * @param time  过期时间，单位毫秒
     * @return 结果
     */
    String set(String key, String value, String nxxx, String expx, int time);

    /**
     * 获取对应的值
     *
     * @param key key
     * @return 结果
     */
    String get(String key);

    /**
     * 是否包含指定的 key
     * 同 exists
     *
     * @param key 键
     * @return 结果
     */
    boolean contains(String key);

    /**
     * 删除
     * 同 del
     *
     * @param key 键
     */
    void remove(String key);

    /**
     * key 的存活时间
     * <p>
     * In Redis 2.8 or newer, if the Key does not have an associated expire, -1 is returned or if the Key does not exists, -2 is returned.
     *
     * @param key 获取 key
     * @return 结果
     */
    long ttl(String key);

    /**
     * 过期
     *
     * @param key        key
     * @param expireTime 过期时间
     * @param timeUnit   时间单位
     */
    void expire(String key, long expireTime, TimeUnit timeUnit);

    /**
     * 指定过期时间
     *
     * @param key      键
     * @param unixTime 时间
     */
    void expireAt(String key, long unixTime);

    /**
     * 获取过期的 UNIX 时间，不存在时返回 null
     *
     * @param key 键
     * @return 时间
     */
    long expireAt(String key);

    /**
     * EVAL 对应的值
     *
     * @param script   脚本
     * @param keyCount key 总数
     * @param params   值
     * @return 结果
     */
    Object eval(String script, int keyCount, String... params);

    /**
     * 执行脚本
     *
     * @param script 脚本
     * @param keys   键
     * @param params 值
     * @return 结果
     */
    Object eval(String script, List<String> keys, List<String> params);

    /**
     * EVAL 脚本
     *
     * @param script 脚本
     * @return 结果
     */
    Object eval(String script);
}
