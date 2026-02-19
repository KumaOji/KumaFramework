/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.utils;

import static com.baomidou.dynamic.datasource.enums.DdConstants.MASTER;

import com.baomidou.dynamic.datasource.enums.DdConstants;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import cn.hutool.core.util.ObjUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * MybatisUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class MybatisUtil {

    private final Executor executor;

    private final SqlSessionFactory sqlSessionFactory;

    private static final int DEFAULT_BATCH_NUM = 50000;

    public MybatisUtil(
            @Autowired @Qualifier("asyncThreadPoolTaskExecutor") Executor executor,
            SqlSessionFactory sqlSessionFactory ) {
        this.executor = executor;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public <T, M> void batch( List<T> dataList, Class<M> clazz, BiConsumer<M, T> consumer ) {
        batch(dataList, DEFAULT_BATCH_NUM, clazz, MASTER, consumer);
    }

    public <T, M> void batch(
            List<T> dataList, Class<M> clazz, String ds, BiConsumer<M, T> consumer ) {
        batch(dataList, DEFAULT_BATCH_NUM, clazz, ds, consumer);
    }

    /**
     * 批量新增.
     *
     * @param dataList 集合
     * @param batchNum 每组多少条数据
     * @param clazz 类型
     * @param <T> 泛型
     * @param <M> mapper泛型
     * @param consumer 函数
     * @param ds 数据源名称
     */
    public <T, M> void batch(
            List<T> dataList, int batchNum, Class<M> clazz, String ds, BiConsumer<M, T> consumer ) {
        // 数据分组
        List<List<T>> partition = Lists.partition(dataList, batchNum);
        AtomicBoolean rollback = new AtomicBoolean(false);
        int size = partition.size();
        // 经测试，10个任务可以支持多线程事务回滚，超过则被阻塞
        CyclicBarrier cyclicBarrier;
        if (size > 10) {
            cyclicBarrier = null;
        } else {
            cyclicBarrier = new CyclicBarrier(partition.size());
        }
        List<CompletableFuture<Void>> futures =
                partition.stream()
                        .map(
                                item ->
                                        CompletableFuture.runAsync(
                                                () ->
                                                        handleBatch(
                                                                item,
                                                                clazz,
                                                                consumer,
                                                                rollback,
                                                                ds,
                                                                cyclicBarrier),
                                                executor))
                        .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        if (rollback.get()) {
            throw new DataSourceException("批量插入数据异常，数据已回滚");
        }
    }

    private <T, M> void handleBatch(
            List<T> item,
            Class<M> clazz,
            BiConsumer<M, T> consumer,
            AtomicBoolean rollback,
            String ds,
            CyclicBarrier cyclicBarrier ) {
        try {
            DynamicDataSourceContextHolder.push(ds);
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            M mapper = sqlSession.getMapper(clazz);
            try {
                item.forEach(i -> consumer.accept(mapper, i));
                if (ObjUtil.isNotNull(cyclicBarrier)) {
                    cyclicBarrier.await(180, TimeUnit.SECONDS);
                }
                sqlSession.commit();
            } catch (Exception e) {
                handleException(rollback, e, cyclicBarrier);
            } finally {
                if (rollback.get()) {
                    sqlSession.rollback();
                }
                sqlSession.clearCache();
                sqlSession.close();
            }
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
    }

    private void handleException( AtomicBoolean rollback, Exception e, CyclicBarrier cyclicBarrier ) {
        // 回滚标识
        rollback.compareAndSet(false, true);
        LogUtils.error("批量插入数据异常，已设置回滚标识，错误信息", e);
        if (ObjUtil.isNotNull(cyclicBarrier)) {
            cyclicBarrier.reset();
        }
    }
}
