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

package com.kuma.boot.web.support.methodPartAndRetryer;

public enum RetentionPolicy {
    /**
     * 编译器处理完后不存储在class中
     */
    SOURCE,
    /**
     * 注释将被编译器记录在类文件中，但不需要在运行时被VM保留。 这是默认值
     */
    CLASS,
    /**
     * 编译器存储在class中，可以由虚拟机读取
     */
    RUNTIME
}
