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

package com.kuma.boot.common.support.compress;

import com.kuma.boot.common.extension.ExtensionLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * The Data Compress Factory.
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:14:25
 */
public enum CompressFactory {

    /**
     * 压缩
     */
    CF;

    /**
     * 压缩map
     */
    Map<String, Compress> compressMap = new HashMap<>();

    CompressFactory() {
        ExtensionLoader<Compress> extensionLoader =
                ExtensionLoader.getExtensionLoader(Compress.class);
        compressMap = extensionLoader.getExtensionMap();
    }

    /**
     * The get compress @SPI value is {#name} extension.
     * @param name
     * @return {@link Compress }
     * @since 2022-04-27 17:14:25
     */
    public Compress getExtension(String name) {
        return compressMap.get(name);
    }

    public Map<String, Compress> getCompressMap() {
        return compressMap;
    }
}
