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

package com.kuma.boot.core.startup;

import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.bootstrap.ConfigurableBootstrapContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;

/**
 * Core constants for startup stage statics.
 *
 * @author huzijie
 * @version BootStageConstants.java, v 0.1 2020年12月31日 11:40 上午 huzijie Exp $
 */
public class BootStageConstants {

    /**
     * The running stage since JVM started to {@link SpringApplicationRunListener#started(ConfigurableApplicationContext, Duration)} ()}
     */
    public static final String JVM_STARTING_STAGE = "JvmStartingStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#started(ConfigurableApplicationContext, Duration)} ()} to
     * {@link SpringApplicationRunListener#environmentPrepared(ConfigurableBootstrapContext, ConfigurableEnvironment)} (ConfigurableEnvironment)}}
     */
    public static final String ENVIRONMENT_PREPARE_STAGE = "EnvironmentPrepareStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#environmentPrepared(ConfigurableBootstrapContext, ConfigurableEnvironment)} (ConfigurableEnvironment)} to
     * {@link SpringApplicationRunListener#contextPrepared(ConfigurableApplicationContext)}}
     */
    public static final String APPLICATION_CONTEXT_PREPARE_STAGE = "ApplicationContextPrepareStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#contextPrepared(ConfigurableApplicationContext)} to
     * {@link SpringApplicationRunListener#contextLoaded(ConfigurableApplicationContext)}}
     */
    public static final String APPLICATION_CONTEXT_LOAD_STAGE = "ApplicationContextLoadStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#contextLoaded(ConfigurableApplicationContext)} to
     * StartupContextRefreshedListener.onApplicationEvent(ContextRefreshedEvent)
     */
    public static final String APPLICATION_CONTEXT_REFRESH_STAGE = "ApplicationContextRefreshStage";
}
