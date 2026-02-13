/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.apache.commons.collections4.CollectionUtils
 */
package com.kuma.boot.common.support.objectrelation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.concurrent.NotThreadSafe;
import org.apache.commons.collections4.CollectionUtils;

@NotThreadSafe
public final class ObjectRelationMatcher2<E, I, C extends Collection<I>, D, K> {
    private static final int DEFAULT_CACHE_COLLECTION_COUNT = 16;
    private static final int DEFAULT_CACHE_IDENTIFIER_COUNT_PER_COLLECTION = 128;
    private static final String EXECUTOR_THREAD_NAME_FORMAT = "orm-parallel-query-pool-%d";
    private static final int EXECUTOR_PARALLEL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int EXECUTOR_QUEUE_SIZE = 1024;
    private static Table<String, Object, Object> cache = HashBasedTable.create((int)16, (int)128);
    private static ListeningExecutorService parallelQueryPool;
    private Collection<E> elements;
    private Predicate<E> elementFilter;
    private Function<E, I> elementIdentifierExtractor;
    private Function<E, Collection<I>> elementIdentifiersExtractor;
    private Collector<I, ?, C> identifierCollector;
    private Function<C, ? extends Collection<D>> batchQueryMethod;
    private int maxBatchQuerySize;
    private Function<I, D> singleQueryMethod;
    private boolean parallelExecuteQuery = true;
    private int queryExceptionRetryTimes;
    private long queryExceptionRetryInterval;
    private Predicate<D> dataFilter;
    private Function<D, K> dataKeyGenerator;
    private Function<E, K> elementToKeyMapping;
    private Function<E, Collection<K>> elementToKeysMapping;
    private boolean oneToMany;
    private boolean manyToMany;
    private boolean emptyAsUnmatched;
    private boolean useCache;
    private String cacheKeyName;
    private boolean clearCacheAfterMatch;
    private ObjectRelationMatcherEvent<E, I, C, D, K> eventListener;
    private boolean matched;
    private Map<E, D> oneToOneMap;
    private Map<E, List<D>> oneToManyMap;
    private Map<E, List<D>> manyToManyMap;

    private void validate() {
        Objects.requireNonNull(this.elements, "\u6e90\u96c6\u5408\u4e0d\u80fd\u4e3a\u7a7a");
        if (this.manyToMany) {
            Objects.requireNonNull(this.elementIdentifiersExtractor, "\u6807\u8bc6\u7b26\u63d0\u53d6\u5668\u4e0d\u80fd\u4e3a\u7a7a");
        } else {
            Objects.requireNonNull(this.elementIdentifierExtractor, "\u6807\u8bc6\u7b26\u63d0\u53d6\u5668\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (this.batchQueryMethod != null) {
            Objects.requireNonNull(this.identifierCollector, "\u6279\u91cf\u67e5\u8be2\u6a21\u5f0f\u4e0b\u6807\u8bc6\u805a\u5408\u5668\u4e0d\u80fd\u4e3a\u7a7a");
        } else {
            this.setIdentifierCollectorType(List.class);
            Objects.requireNonNull(this.singleQueryMethod, "\u6279\u91cf\u67e5\u8be2\u65b9\u6cd5\u548c\u5355\u4e2a\u67e5\u8be2\u65b9\u6cd5\u81f3\u5c11\u8981\u6307\u5b9a\u4e00\u4e2a");
        }
        if (this.queryExceptionRetryTimes < 0) {
            this.queryExceptionRetryTimes = 0;
        }
        if (this.queryExceptionRetryInterval < 0L) {
            this.queryExceptionRetryInterval = 0L;
        }
        Objects.requireNonNull(this.dataKeyGenerator, "\u65b0\u96c6\u5408\u552f\u4e00\u952e\u751f\u6210\u5668\u4e0d\u80fd\u4e3a\u7a7a");
        if (this.manyToMany) {
            Objects.requireNonNull(this.elementToKeysMapping, "\u6e90\u96c6\u5408\u5230\u552f\u4e00\u952e\u7684\u6620\u5c04\u5173\u7cfb\u4e0d\u80fd\u4e3a\u7a7a");
        } else {
            Objects.requireNonNull(this.elementToKeyMapping, "\u6e90\u96c6\u5408\u5230\u552f\u4e00\u952e\u7684\u6620\u5c04\u5173\u7cfb\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (this.useCache) {
            if (this.manyToMany) {
                throw new RuntimeException("\u591a\u5bf9\u591a\u7684\u60c5\u51b5\u4e0b\u4e0d\u80fd\u4f7f\u7528\u7f13\u5b58");
            }
            Objects.requireNonNull(this.cacheKeyName, "\u4f7f\u7528\u7f13\u5b58\u7684\u60c5\u51b5\u4e0b\u7f13\u5b58\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
        }
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setIdentifierCollectorType(Class<? extends Collection> type) {
        Objects.requireNonNull(type, "\u6807\u8bc6\u6536\u96c6\u5668\u7c7b\u578b\u4e0d\u80fd\u8bbe\u7f6e\u4e3anull");
        if (List.class.isAssignableFrom(type)) {
            this.setIdentifierCollector(Collectors.toList());
        } else if (Set.class.isAssignableFrom(type)) {
            this.setIdentifierCollector(Collectors.toSet());
        } else {
            throw new RuntimeException("\u65e0\u6cd5\u5904\u7406\u7684\u6807\u8bc6\u6536\u96c6\u5668\u7c7b\u578b: " + String.valueOf(type));
        }
        return this;
    }

    public ObjectRelationMatcher2<E, I, C, D, K> match() {
        Stream<Object> identifierStream;
        HashMap elementDataListMap;
        HashMap elementDataMap;
        this.validate();
        if (this.manyToMany) {
            elementDataMap = null;
            elementDataListMap = new HashMap(this.elements.size(), 1.0f);
        } else if (this.oneToMany) {
            elementDataMap = null;
            elementDataListMap = new HashMap(this.elements.size(), 1.0f);
        } else {
            elementDataMap = new HashMap(this.elements.size(), 1.0f);
            elementDataListMap = null;
        }
        Stream<E> elementStream = this.filteredElementStream();
        if (this.useCache) {
            ArrayList identifierList = new ArrayList(this.elements.size());
            if (this.manyToMany) {
                elementStream.forEach(element -> {
                    Collection<I> identifiers = this.elementIdentifiersExtractor.apply(element);
                    if (CollectionUtils.isNotEmpty(identifiers)) {
                        identifierList.addAll(identifiers);
                    }
                });
            } else if (this.oneToMany) {
                elementStream.forEach(element -> {
                    I identifier = this.elementIdentifierExtractor.apply(element);
                    List<D> list = this.getDataListFormCache(identifier);
                    if (!(list == null || list.isEmpty() && this.emptyAsUnmatched)) {
                        elementDataListMap.put(element, list);
                    } else {
                        identifierList.add(identifier);
                    }
                });
            } else {
                elementStream.forEach(element -> {
                    I identifier = this.elementIdentifierExtractor.apply(element);
                    D data = this.getDataFromCache(identifier);
                    if (data != null) {
                        elementDataMap.put(element, data);
                    } else {
                        identifierList.add(identifier);
                    }
                });
            }
            identifierStream = identifierList.stream();
        } else {
            identifierStream = elementStream.map(this.elementIdentifierExtractor);
        }
        Collection identifierCollection = (Collection)identifierStream.collect(this.identifierCollector);
        if (!identifierCollection.isEmpty()) {
            Collection dataCollection = null;
            int retryCount = 0;
            Exception lastException = null;
            while (true) {
                try {
                    if (identifierCollection.size() == 1 && this.singleQueryMethod != null) {
                        D data = this.singleQueryMethod.apply(identifierCollection.iterator().next());
                        dataCollection = Optional.ofNullable(data).map(Collections::singletonList).orElse(Collections.emptyList());
                    } else if (this.batchQueryMethod != null) {
                        if (this.maxBatchQuerySize <= 0) {
                            dataCollection = this.batchQueryMethod.apply(identifierCollection);
                        } else {
                            int pages = (identifierCollection.size() + this.maxBatchQuerySize - 1) / this.maxBatchQuerySize;
                            List identifierListBatches = Stream.iterate(0, x -> x + 1).limit(pages).map(x -> (Collection)identifierCollection.stream().skip((long)x.intValue() * (long)this.maxBatchQuerySize).limit(this.maxBatchQuerySize).collect(this.identifierCollector)).collect(Collectors.toList());
                            if (this.parallelExecuteQuery) {
                                List futures = identifierListBatches.stream().map(x -> parallelQueryPool.submit(() -> this.batchQueryMethod.apply(x))).collect(Collectors.toList());
                                dataCollection = ((List)Futures.allAsList(futures).get()).stream().flatMap(Collection::stream).collect(Collectors.toList());
                            } else {
                                dataCollection = identifierListBatches.stream().map(this.batchQueryMethod).flatMap(Collection::stream).collect(Collectors.toList());
                            }
                        }
                    } else if (this.singleQueryMethod != null) {
                        if (this.parallelExecuteQuery) {
                            List futures = identifierCollection.stream().map(x -> parallelQueryPool.submit(() -> this.singleQueryMethod.apply(x))).collect(Collectors.toList());
                            dataCollection = (Collection)Futures.allAsList(futures).get();
                        } else {
                            dataCollection = identifierCollection.stream().map(this.singleQueryMethod).collect(Collectors.toList());
                        }
                    }
                    if (this.eventListener == null) break;
                    this.eventListener.onQuerySuccess(identifierCollection, retryCount, dataCollection);
                }
                catch (Exception e2) {
                    lastException = e2;
                    if (this.eventListener != null) {
                        this.eventListener.onQueryException(identifierCollection, retryCount, e2);
                    }
                    try {
                        Thread.sleep(this.queryExceptionRetryInterval);
                        continue;
                    }
                    catch (InterruptedException identifierListBatches) {
                        // empty catch block
                    }
                    if (++retryCount <= this.queryExceptionRetryTimes) continue;
                }
                break;
            }
            if (retryCount > this.queryExceptionRetryTimes) {
                throw new RuntimeException("query for" + String.valueOf(identifierCollection) + "failure", lastException);
            }
            if (CollectionUtils.isNotEmpty(dataCollection)) {
                Stream dataStream = dataCollection.stream();
                if (this.dataFilter != null) {
                    dataStream = dataStream.filter(this.dataFilter);
                }
                elementStream = this.filteredElementStream();
                if (this.manyToMany) {
                    keyDataListMap = dataStream.filter(Objects::nonNull).collect(Collectors.groupingBy(this.dataKeyGenerator));
                    elementStream.filter(element -> !keyDataListMap.containsKey(element)).forEach(element -> {
                        Collection<K> collection = this.elementToKeysMapping.apply(element);
                        for (K key : collection) {
                            List list = (List)keyDataListMap.get(key);
                            elementDataListMap.put(element, list);
                        }
                        K key = this.elementToKeyMapping.apply(element);
                        List list = (List)keyDataListMap.get(key);
                        elementDataListMap.put(element, list);
                        if (!(!this.useCache || list == null || list.isEmpty() && this.emptyAsUnmatched)) {
                            this.putListToCache(this.elementIdentifierExtractor.apply(element), list);
                        }
                    });
                } else if (this.oneToMany) {
                    keyDataListMap = dataStream.filter(Objects::nonNull).collect(Collectors.groupingBy(this.dataKeyGenerator));
                    elementStream.filter(element -> !keyDataListMap.containsKey(element)).forEach(element -> {
                        K key = this.elementToKeyMapping.apply(element);
                        List list = (List)keyDataListMap.get(key);
                        elementDataListMap.put(element, list);
                        if (!(!this.useCache || list == null || list.isEmpty() && this.emptyAsUnmatched)) {
                            this.putListToCache(this.elementIdentifierExtractor.apply(element), list);
                        }
                    });
                } else {
                    Map keyDataMap = dataStream.filter(Objects::nonNull).collect(HashMap::new, (m, e) -> m.put(this.dataKeyGenerator.apply(e), e), Map::putAll);
                    elementStream.filter(element -> !elementDataMap.containsKey(element)).forEach(element -> {
                        K key = this.elementToKeyMapping.apply(element);
                        Object data = keyDataMap.get(key);
                        elementDataMap.put(element, data);
                        if (this.useCache && data != null) {
                            this.putToCache(this.elementIdentifierExtractor.apply(element), data);
                        }
                    });
                }
            }
        }
        if (this.manyToMany) {
            this.elements.stream().filter(element -> !elementDataListMap.containsKey(element)).forEach(element -> elementDataListMap.put(element, null));
        } else if (this.oneToMany) {
            this.elements.stream().filter(element -> !elementDataListMap.containsKey(element)).forEach(element -> elementDataListMap.put(element, null));
        } else {
            this.elements.stream().filter(element -> !elementDataMap.containsKey(element)).forEach(element -> elementDataMap.put(element, null));
        }
        this.oneToOneMap = elementDataMap;
        this.oneToManyMap = elementDataListMap;
        this.manyToManyMap = elementDataListMap;
        if (this.useCache && this.clearCacheAfterMatch) {
            this.clearCache();
        }
        this.matched = true;
        return this;
    }

    private Stream<E> filteredElementStream() {
        Stream<E> stream = this.elements.stream();
        return Optional.ofNullable(this.elementFilter).map(stream::filter).orElse(stream);
    }

    private D getDataFromCache(I identifier) {
        return (D)cache.get((Object)this.cacheKeyName, identifier);
    }

    private List<D> getDataListFormCache(I identifier) {
        return (List)cache.get((Object)this.cacheKeyName, identifier);
    }

    private void putToCache(I identifier, D data) {
        cache.put((Object)this.cacheKeyName, identifier, data);
    }

    private void putListToCache(I identifier, List<D> list) {
        cache.put((Object)this.cacheKeyName, identifier, list);
    }

    private void clearCache() {
        ObjectRelationMatcher2.clearCache(this.cacheKeyName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clearCache(String cacheKeyName) {
        Map map;
        Map map2 = map = (Map)cache.columnMap().get(cacheKeyName);
        synchronized (map2) {
            map.clear();
        }
    }

    public static void clearAllCaches() {
        cache.clear();
    }

    public Map<E, D> getOneToOneRelations(boolean containsUnmatched) {
        if (!this.matched) {
            throw new RuntimeException("\u9700\u8981\u5148\u8c03\u7528match\u65b9\u6cd5");
        }
        if (this.oneToMany) {
            throw new RuntimeException("\u4e00\u5bf9\u591a\u5173\u7cfb\u5e94\u8be5\u8c03\u7528getOneToManyRelations\u65b9\u6cd5");
        }
        HashMap<E, D> map = new HashMap<E, D>(this.oneToOneMap);
        if (!containsUnmatched) {
            map.entrySet().stream().filter(x -> x.getValue() == null).map(Map.Entry::getKey).collect(Collectors.toList()).forEach(map::remove);
        }
        return map;
    }

    public Map<E, D> getOneToOneRelations() {
        return this.getOneToOneRelations(false);
    }

    public Map<E, List<D>> getOneToManyRelations(boolean containsUnmatched) {
        if (!this.matched) {
            throw new RuntimeException("\u9700\u8981\u5148\u8c03\u7528match\u65b9\u6cd5");
        }
        if (!this.oneToMany) {
            throw new RuntimeException("\u4e00\u5bf9\u4e00\u5173\u7cfb\u5e94\u8be5\u8c03\u7528getOneToOneRelations\u65b9\u6cd5");
        }
        HashMap<E, List<D>> map = new HashMap<E, List<D>>(this.oneToManyMap);
        if (!containsUnmatched) {
            map.entrySet().stream().filter(x -> x.getValue() == null || ((List)x.getValue()).isEmpty() && this.emptyAsUnmatched).map(Map.Entry::getKey).collect(Collectors.toList()).forEach(map::remove);
        }
        return map;
    }

    public Map<E, List<D>> getOneToManyRelations() {
        return this.getOneToManyRelations(false);
    }

    public void processOneToOne(BiConsumer<E, D> matchedProcessor, Consumer<E> unmatchedProcessor) {
        if (!this.matched) {
            throw new RuntimeException("\u9700\u8981\u5148\u8c03\u7528match\u65b9\u6cd5");
        }
        if (this.oneToMany) {
            throw new RuntimeException("\u4e00\u5bf9\u591a\u5173\u7cfb\u5e94\u8be5\u8c03\u7528getOneToManyRelations\u65b9\u6cd5");
        }
        this.oneToOneMap.forEach((k, v) -> {
            if (v != null) {
                matchedProcessor.accept(k, v);
            } else if (unmatchedProcessor != null) {
                unmatchedProcessor.accept(k);
            }
        });
    }

    public void processOneToOne(BiConsumer<E, D> matchedProcessor) {
        this.processOneToOne(matchedProcessor, null);
    }

    public void processOneToMany(BiConsumer<E, List<D>> matchedProcessor, Consumer<E> unmatchedProcessor) {
        if (!this.matched) {
            throw new RuntimeException("\u9700\u8981\u5148\u8c03\u7528match\u65b9\u6cd5");
        }
        if (!this.oneToMany) {
            throw new RuntimeException("\u4e00\u5bf9\u4e00\u5173\u7cfb\u5e94\u8be5\u8c03\u7528getOneToOneRelations\u65b9\u6cd5");
        }
        this.oneToManyMap.forEach((k, v) -> {
            if (!(v == null || v.isEmpty() && this.emptyAsUnmatched)) {
                matchedProcessor.accept((E)k, (List<D>)v);
            } else if (unmatchedProcessor != null) {
                unmatchedProcessor.accept(k);
            }
        });
    }

    public void processManyToMany(BiConsumer<E, List<D>> matchedProcessor, Consumer<E> unmatchedProcessor) {
        if (!this.matched) {
            throw new RuntimeException("\u9700\u8981\u5148\u8c03\u7528match\u65b9\u6cd5");
        }
        if (!this.manyToMany) {
            throw new RuntimeException("\u4e00\u5bf9\u4e00\u5173\u7cfb\u5e94\u8be5\u8c03\u7528getOneToOneRelations\u65b9\u6cd5");
        }
        this.manyToManyMap.forEach((k, v) -> {
            if (!(v == null || v.isEmpty() && this.emptyAsUnmatched)) {
                matchedProcessor.accept((E)k, (List<D>)v);
            } else if (unmatchedProcessor != null) {
                unmatchedProcessor.accept(k);
            }
        });
    }

    public void processOneToMany(BiConsumer<E, List<D>> matchedProcessor) {
        this.processOneToMany(matchedProcessor, null);
    }

    private ObjectRelationMatcher2<E, I, C, D, K> markUnmatched() {
        this.matched = false;
        return this;
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setElements(Collection<E> elements) {
        this.elements = elements;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setElementFilter(Predicate<E> elementFilter) {
        this.elementFilter = elementFilter;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setElementIdentifierExtractor(Function<E, I> elementIdentifierExtractor) {
        this.elementIdentifierExtractor = elementIdentifierExtractor;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setIdentifierCollector(Collector<I, ?, C> identifierCollector) {
        this.identifierCollector = identifierCollector;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setBatchQueryMethod(Function<C, ? extends Collection<D>> batchQueryMethod) {
        this.batchQueryMethod = batchQueryMethod;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setMaxBatchQuerySize(int maxBatchQuerySize) {
        this.maxBatchQuerySize = maxBatchQuerySize;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setSingleQueryMethod(Function<I, D> singleQueryMethod) {
        this.singleQueryMethod = singleQueryMethod;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setParallelExecuteQuery(boolean parallelExecuteQuery) {
        this.parallelExecuteQuery = parallelExecuteQuery;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setQueryExceptionRetryTimes(int queryExceptionRetryTimes) {
        this.queryExceptionRetryTimes = queryExceptionRetryTimes;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setQueryExceptionRetryInterval(long queryExceptionRetryInterval) {
        this.queryExceptionRetryInterval = queryExceptionRetryInterval;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setDataFilter(Predicate<D> dataFilter) {
        this.dataFilter = dataFilter;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setDataKeyGenerator(Function<D, K> dataKeyGenerator) {
        this.dataKeyGenerator = dataKeyGenerator;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setElementToKeyMapping(Function<E, K> elementToKeyMapping) {
        this.elementToKeyMapping = elementToKeyMapping;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setOneToMany(boolean oneToMany) {
        this.oneToMany = oneToMany;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setEmptyAsUnmatched(boolean emptyAsUnmatched) {
        this.emptyAsUnmatched = emptyAsUnmatched;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setCacheKeyName(String cacheKeyName) {
        this.cacheKeyName = cacheKeyName;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setClearCacheAfterMatch(boolean clearCacheAfterMatch) {
        this.clearCacheAfterMatch = clearCacheAfterMatch;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setEventListener(ObjectRelationMatcherEvent<E, I, C, D, K> eventListener) {
        this.eventListener = eventListener;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setElementIdentifiersExtractor(Function<E, Collection<I>> elementIdentifiersExtractor) {
        this.elementIdentifiersExtractor = elementIdentifiersExtractor;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setElementToKeysMapping(Function<E, Collection<K>> elementToKeysMapping) {
        this.elementToKeysMapping = elementToKeysMapping;
        return this.markUnmatched();
    }

    public ObjectRelationMatcher2<E, I, C, D, K> setManyToMany(boolean manyToMany) {
        this.manyToMany = manyToMany;
        return this.markUnmatched();
    }

    public static void shutdown() {
        try {
            ObjectRelationMatcher2.clearAllCaches();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            parallelQueryPool.shutdown();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static {
        ThreadPoolExecutor originalPool = new ThreadPoolExecutor(EXECUTOR_PARALLEL_SIZE, EXECUTOR_PARALLEL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder().setNameFormat(EXECUTOR_THREAD_NAME_FORMAT).build(), new ThreadPoolExecutor.CallerRunsPolicy());
        parallelQueryPool = MoreExecutors.listeningDecorator((ExecutorService)originalPool);
    }

    public static class ObjectRelationMatcherEvent<E, I, C extends Collection<I>, D, K> {
        public void onQuerySuccess(C identifierCollection, int count, Collection<D> dataCollection) {
            if (count == 0) {
                LogUtils.debug("orm query for {} results are: {}", identifierCollection, dataCollection);
            } else {
                LogUtils.debug("orm query(retry={}) for {} results are: {}", count, identifierCollection, dataCollection);
            }
        }

        public void onQueryException(C identifierCollection, int count, Exception e) {
            if (count == 0) {
                LogUtils.warn("orm query for {} throws exception: ", identifierCollection, e);
            } else {
                LogUtils.warn("orm query(retry={}) for {} throws exception: ", count, identifierCollection, e);
            }
        }
    }
}

