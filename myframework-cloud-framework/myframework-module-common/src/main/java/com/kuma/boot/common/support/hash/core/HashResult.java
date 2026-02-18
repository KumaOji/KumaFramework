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

package com.kuma.boot.common.support.hash.core;

import java.util.Arrays;

/**
 * HashResult
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class HashResult implements com.kuma.boot.common.support.hash.api.HashResult {

    private byte[] hashed;

    public static HashResult newInstance() {
        return new HashResult();
    }

    public byte[] hashed() {
        return this.hashed;
    }

    public HashResult hashed( byte[] hashed ) {
        this.hashed = hashed;
        return this;
    }

    public String toString() {
        return "HashResult{hashed=" + Arrays.toString(this.hashed) + '}';
    }
}
