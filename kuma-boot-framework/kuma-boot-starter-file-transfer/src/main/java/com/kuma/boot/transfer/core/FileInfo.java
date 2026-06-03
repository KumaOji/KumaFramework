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

package com.kuma.boot.transfer.core;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

/**
 * 远程文件/目录元信息.
 *
 * @author kuma
 */
@Value
@Builder
public class FileInfo {

    /** 文件名（不含路径）. */
    String name;

    /** 完整远程路径. */
    String path;

    /** 是否为目录. */
    boolean directory;

    /** 文件大小（字节），目录为 0. */
    long size;

    /** 最后修改时间. */
    Instant lastModified;
}
