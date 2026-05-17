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

package com.kuma.boot.common.constant;

/**
 * maven 项目常量
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-22 10:45:11
 */
public final class MavenConstants {

    private MavenConstants() {}

    /** 目标class文件路径后缀 */
    public static final String TARGET_CLASSES_PATH_SUFFIX = "target/classes/";

    /** main-java文件默认存放路径 */
    public static final String SRC_MAIN_JAVA_PATH = "src/main/java/";

    /** main-资源文件存放路径 */
    public static final String SRC_MAIN_RESOURCES_PATH = "src/main/resources/";

    /** java测试文件默认存放路径 */
    public static final String SRC_TEST_JAVA_PATH = "src/test/java/";

    /** 测试资源文件存放路径 */
    public static final String SRC_TEST_RESOURCES_PATH = "src/test/resources/";
}
