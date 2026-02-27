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

package com.kuma.boot.actuator.endpoint.kmc;

import com.kuma.boot.actuator.health.KmcHealthIndicator;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.boot.health.contributor.Health;

/**
 * KmcHealthEndPoint
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:13:40
 */
@WebEndpoint(id = "kmchealth")
public class KmcHealthEndPoint {

    private final KmcHealthIndicator kmcHealthIndicator;

    public KmcHealthEndPoint(KmcHealthIndicator kmcHealthIndicator) {
        this.kmcHealthIndicator = kmcHealthIndicator;
    }

    @ReadOperation
    public Health health() {
        return kmcHealthIndicator.health(true);
    }
}
