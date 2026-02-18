/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.support.dataframe.iframe.support;

/**
 * Custom Connection operation to return a new Element
 *
 * @param <L> left table element
 * @param <R> right table element
 * @param <V> new element
 * @author caizhihao
 */
public interface Join<L, R, V> {

    /**
     * Join Operation
     * @param left left table element, If it is a right connection, it may be null
     * @param right right table element, If it is a left connection, it may be null
     * @return Build a new element with left and right element
     */
    V join(L left, R right);
}
