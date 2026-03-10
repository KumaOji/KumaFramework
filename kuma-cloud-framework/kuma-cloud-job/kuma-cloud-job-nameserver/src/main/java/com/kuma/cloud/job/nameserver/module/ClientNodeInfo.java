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

package com.kuma.cloud.job.nameserver.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClientNodeInfo
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientNodeInfo {

	private String type;
	private String appName;
	private long lastActiveTime;
	private static final long CLIENT_TIMEOUT_MS = 60000;

	public void refresh( ClientHeartbeat clientHeartbeat ) {
		lastActiveTime = clientHeartbeat.getHeartbeatTime();
		appName = clientHeartbeat.getAppName();
	}

	public boolean timeout() {
		long timeout = System.currentTimeMillis() - lastActiveTime;
		return timeout > CLIENT_TIMEOUT_MS;
	}
}
