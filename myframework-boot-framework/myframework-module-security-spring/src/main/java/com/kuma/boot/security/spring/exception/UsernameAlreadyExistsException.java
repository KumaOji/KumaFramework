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

package com.kuma.boot.security.spring.exception;

/**
 * <p>用户名已经存在
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:07:49
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    /**
     * 用户名已经存在异常
     *
     * @param msg   味精
     * @param cause 导致
     * @since 2023-07-04 10:07:49
     */
    public UsernameAlreadyExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * 用户名已经存在异常
     *
     * @param msg 味精
     * @since 2023-07-04 10:07:50
     */
    public UsernameAlreadyExistsException(String msg) {
        super(msg);
    }
}
