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

package com.kuma.boot.data.mybatis.interceptor.log.properties;

import org.slf4j.Logger;

/**
 * 日志级别
 */
public enum LogLevel {

    /**
     * TRACE级别
     */
    TRACE {
        @Override
        public void log(Logger logger, String format, Object... arguments) {
            logger.trace(format, arguments);
        }
    },

    /**
     * DEBUG级别
     */
    DEBUG {
        @Override
        public void log(Logger logger, String format, Object... arguments) {
            logger.debug(format, arguments);
        }
    },

    /**
     * INFO级别
     */
    INFO {
        @Override
        public void log(Logger logger, String format, Object... arguments) {
            logger.info(format, arguments);
        }
    };

    /**
     * 记录日志
     * @param logger 日志记录器
     * @param format 格式
     * @param arguments 参数
     */
    public abstract void log(Logger logger, String format, Object... arguments);
}
