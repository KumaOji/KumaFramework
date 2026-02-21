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

public enum ElementType {
    /**
     * 类、接口(包括注释类型)或枚举声明
     */
    TYPE,
    /**
     * 字段声明(包括枚举常量)
     */
    FIELD,
    /**
     * 方法声明
     */
    METHOD,
    /**
     * 正式的参数声明
     */
    PARAMETER,
    /**
     * 构造函数声明
     */
    CONSTRUCTOR,
    /**
     * 局部变量声明
     */
    LOCAL_VARIABLE,
    /**
     * 注释类型声明
     */
    ANNOTATION_TYPE,
    /**
     * 程序包声明
     */
    PACKAGE,
    /**
     * 类型参数声明
     */
    TYPE_PARAMETER,
    /**
     * 类型的使用
     */
    TYPE_USE
}
