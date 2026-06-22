package com.kuma.boot.data.vector.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.vector.autoconfigure.properties.VectorStoreProperties;
import com.kuma.boot.data.vector.core.VectorStore;
import com.kuma.boot.data.vector.support.memory.InMemoryVectorStore;
import com.kuma.boot.data.vector.support.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 向量库自动装配。
 *
 * <p>按 {@code kuma.boot.data.vector.type} 选择实现，向容器注入唯一的 {@link VectorStore}：
 * <ul>
 *   <li>{@code memory}（默认）：纯内存实现，零依赖，适合测试 / 开发；</li>
 *   <li>{@code qdrant}：基于 Qdrant REST API。</li>
 * </ul>
 * 用户自定义 {@link VectorStore} Bean 时，本装配自动让位（{@code @ConditionalOnMissingBean}）。
 */
@AutoConfiguration
@EnableConfigurationProperties(VectorStoreProperties.class)
@ConditionalOnProperty(prefix = VectorStoreProperties.PREFIX, name = "enabled",
        havingValue = "true", matchIfMissing = true)
public class VectorStoreAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(VectorStoreAutoConfiguration.class, StarterNameConstants.DATA_VECTOR_STARTER);
    }

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    @ConditionalOnProperty(prefix = VectorStoreProperties.PREFIX, name = "type",
            havingValue = "memory", matchIfMissing = true)
    public VectorStore inMemoryVectorStore(VectorStoreProperties props) {
        InMemoryVectorStore store = new InMemoryVectorStore();
        if (props.isAutoCreateCollection()) {
            store.createCollection(props.getCollection(), props.getDimension(), props.getDistance());
        }
        return store;
    }

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    @ConditionalOnProperty(prefix = VectorStoreProperties.PREFIX, name = "type", havingValue = "qdrant")
    public VectorStore qdrantVectorStore(VectorStoreProperties props) {
        VectorStoreProperties.Qdrant q = props.getQdrant();
        QdrantVectorStore store = new QdrantVectorStore(
                q.getHost(), q.getHttpPort(), q.isUseHttps(), q.getApiKey());
        if (props.isAutoCreateCollection()) {
            store.createCollection(props.getCollection(), props.getDimension(), props.getDistance());
        }
        return store;
    }
}
