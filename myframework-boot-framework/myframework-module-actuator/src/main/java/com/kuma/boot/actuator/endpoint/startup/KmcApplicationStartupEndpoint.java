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

package com.kuma.boot.actuator.endpoint.startup;

import com.kuma.boot.core.startup.StartupReporter;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

/**
 * {@link Endpoint @Endpoint} to expose details startup costs.
 *
 * @author Zhijie
 * @since 2020/7/7
 */
@Endpoint(id = "kmcapplicationstartup")
public class KmcApplicationStartupEndpoint {

    private final StartupReporter startupReporter;

    public KmcApplicationStartupEndpoint(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }

    @ReadOperation
    public StartupReporter.StartupStaticsModel startupSnapshot() {
        return startupReporter.getStartupStaticsModel();
    }

    @WriteOperation
    public StartupReporter.StartupStaticsModel startup() {
        return startupReporter.drainStartupStaticsModel();
    }
}
