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

package com.kuma.boot.common.support.compress.impl;

import com.kuma.boot.common.support.compress.Compress;
import java.io.IOException;
import org.xerial.snappy.Snappy;

/** The Data Compression Based on snappy. */
public class SnappyCompress implements Compress {

    @Override
    public byte[] compress(byte[] data) throws IOException {
        return Snappy.compress(data);
    }

    @Override
    public byte[] uncompress(byte[] data) throws IOException {
        return Snappy.uncompress(data);
    }
}
