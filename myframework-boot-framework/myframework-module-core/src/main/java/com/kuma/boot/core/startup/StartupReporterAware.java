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

package com.kuma.boot.core.startup;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the {@link com.kuma.boot.core.startup.StartupReporter} that it runs in.
 *
 * @author huzijie
 * @version StartupReporterAware.java, v 0.1 2023年01月12日 6:12 PM huzijie Exp $
 * @since 4.0.0
 */
public interface StartupReporterAware extends Aware {

    /**
     * Set the StartupReporter that this object runs in.
     *
     * @param startupReporter the StartupReporter object to be used by this object
     * @throws BeansException if thrown by startupReporter methods
     */
    void setStartupReporter(com.kuma.boot.core.startup.StartupReporter startupReporter) throws BeansException;
}
