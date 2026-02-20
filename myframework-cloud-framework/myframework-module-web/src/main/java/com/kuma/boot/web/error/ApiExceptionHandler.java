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

package com.kuma.boot.web.error;

/**
 * ApiExceptionHandler
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-01-12 08:50:47
 */
public interface ApiExceptionHandler {

    /**
     * Determine if this {@link ApiExceptionHandler} can handle the given {@link Throwable}. It is
     * guaranteed that this method is called first, and the {@link #handle(Throwable)} method will
     * only be called if this method returns <code>true</code>.
     *
     * @param exception the Throwable that needs to be handled
     * @return true if this handler can handle the Throwable, false otherwise.
     */
    boolean canHandle(Throwable exception);

    /**
     * Handle the given {@link Throwable} and return an {@link com.kuma.boot.web.error.ApiErrorResponse} instance that will
     * be serialized to JSON and returned from the controller method that has thrown the Throwable.
     *
     * @param exception the Throwable that needs to be handled
     * @return the non-null ApiErrorResponse
     */
    com.kuma.boot.web.error.ApiErrorResponse handle(Throwable exception);
}
