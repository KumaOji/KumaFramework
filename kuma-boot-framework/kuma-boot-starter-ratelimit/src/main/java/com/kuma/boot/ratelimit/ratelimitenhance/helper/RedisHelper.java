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

package com.kuma.boot.ratelimit.ratelimitenhance.helper;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

/** redis操作助手 */
public class RedisHelper {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 将 Lua 脚本封装到 RedisScript 中执行
     *
     * @param redisScript Lua 脚本
     * @param keys 脚本中对应的key，可以用 KEYS[1]、KEYS[2]... 获取
     * @param args 脚本中用到的参数，可以用 ARGV[1]、ARGV[2]... 获取
     * @param <T> 返回类型
     * @return T
     */
    public <T> T executeScript(RedisScript<T> redisScript, List<String> keys, List<Object> args) {
        return redisTemplate.execute(redisScript, keys, args.toArray());
    }
}
