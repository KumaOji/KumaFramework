/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.thread.ThreadUtil
 *  cn.hutool.json.JSONUtil
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.collection.CollectionUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.ApplicationRunner
 *  org.springframework.data.redis.core.DefaultTypedTuple
 */
package com.kuma.cloud.stream.consumer.trigger;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.DefaultTypedTuple;

public abstract class AbstractDelayQueueListen
implements ApplicationRunner {
    private final RedisRepository redisRepository;

    protected AbstractDelayQueueListen(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startDelayQueueMachine() {
        LogUtils.info((String)"\u5ef6\u65f6\u961f\u5217\u673a\u5668{}\u5f00\u59cb\u8fd0\u4f5c", (Object[])new Object[]{this.setDelayQueueName()});
        block11: while (true) {
            try {
                long now = System.currentTimeMillis() / 1000L;
                Set tuples = this.redisRepository.zRangeByScore(this.setDelayQueueName(), 0.0, (double)now);
                if (CollectionUtils.isEmpty((Collection)tuples)) continue;
                LogUtils.info((String)"\u6267\u884c\u4efb\u52a1:{}", (Object[])new Object[]{JSONUtil.toJsonStr((Object)tuples)});
                Iterator iterator = tuples.iterator();
                while (true) {
                    if (!iterator.hasNext()) continue block11;
                    Object t = iterator.next();
                    DefaultTypedTuple tuple = (DefaultTypedTuple)t;
                    String jobId = (String)tuple.getValue();
                    Long num = this.redisRepository.zRem(this.setDelayQueueName(), new Object[]{jobId});
                    if (num <= 0L) continue;
                    ThreadUtil.execute(() -> this.invoke(jobId));
                }
            }
            catch (Exception e) {
                LogUtils.error((String)"\u5904\u7406\u5ef6\u65f6\u4efb\u52a1\u53d1\u751f\u5f02\u5e38,\u5f02\u5e38\u539f\u56e0\u4e3a{}", (Object[])new Object[]{e.getMessage(), e});
                continue;
            }
            finally {
                try {
                    TimeUnit.SECONDS.sleep(5L);
                }
                catch (InterruptedException e) {
                    LogUtils.error((Throwable)e);
                }
                continue;
            }
            break;
        }
    }

    public abstract void invoke(String var1);

    public abstract String setDelayQueueName();

    public void init() {
        ThreadUtil.execute(this::startDelayQueueMachine);
    }
}

