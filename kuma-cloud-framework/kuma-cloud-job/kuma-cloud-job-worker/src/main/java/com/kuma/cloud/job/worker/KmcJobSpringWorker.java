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

package com.kuma.cloud.job.worker;

import com.google.common.collect.Lists;
import com.kuma.cloud.job.worker.common.KmcJobWorkerConfig;
import com.kuma.cloud.job.worker.processor.factory.BuildInSpringMethodProcessorFactory;
import com.kuma.cloud.job.worker.processor.factory.BuiltInSpringProcessorFactory;
import com.kuma.cloud.job.worker.processor.factory.ProcessorFactory;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring 项目中的 Worker 启动器
 * 能够获取到由 Spring IOC 容器管理的 processor
 *
 */
public class KmcJobSpringWorker
        implements InitializingBean, DisposableBean, ApplicationContextAware {

    /**
     * 组合优于继承，持有 kJobWorker，设置ProcessFactoryList，这里可以自定义工厂
     */
    private KmcJobWorker kmcJobWorker;

    private final KmcJobWorkerConfig config;

    public KmcJobSpringWorker(KmcJobWorkerConfig config) {
        this.config = config;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        kmcJobWorker = new KmcJobWorker(config);
        kmcJobWorker.init();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BuiltInSpringProcessorFactory springProcessorFactory =
                new BuiltInSpringProcessorFactory(applicationContext);
        BuildInSpringMethodProcessorFactory springMethodProcessorFactory =
                new BuildInSpringMethodProcessorFactory(applicationContext);

        // append BuiltInSpringProcessorFactory
        List<ProcessorFactory> processorFactories =
                Lists.newArrayList(
                        Optional.ofNullable(config.getProcessorFactoryList())
                                .orElse(Collections.emptyList()));
        processorFactories.add(springProcessorFactory);
        processorFactories.add(springMethodProcessorFactory);
        config.setProcessorFactoryList(processorFactories);
    }

    @Override
    public void destroy() throws Exception {
        kmcJobWorker.destroy();
    }
}
