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

package com.kuma.cloud.mq.consistency.raft1.common.exception;

/**
 * raft 运行时异常
 * @since 1.0.0
 */
public class RaftRuntimeException extends RuntimeException {

    public RaftRuntimeException() {
        super();
    }

    public RaftRuntimeException(String message) {
        super(message);
    }

    public RaftRuntimeException(Throwable cause) {
        super(cause);
    }

    public RaftRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
