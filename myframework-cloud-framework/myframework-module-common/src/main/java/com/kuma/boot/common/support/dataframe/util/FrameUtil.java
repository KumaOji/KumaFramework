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

package com.kuma.boot.common.support.dataframe.util;

import static java.util.stream.Collectors.toList;

import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.item.FI3;
import com.kuma.boot.common.support.dataframe.iframe.item.FI4;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FrameUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class FrameUtil {

    private FrameUtil() {
    }

    public static <K, V> List<FI2<K, V>> toListFI2( Map<K, V> resultMap ) {
        return resultMap.entrySet().stream()
                .map(e -> new FI2<>(e.getKey(), e.getValue()))
                .collect(toList());
    }

    public static <K, J, V> List<FI3<K, J, V>> toListFI3( Map<K, Map<J, V>> map ) {
        return map.entrySet().stream()
                .flatMap(
                        et ->
                                et.getValue().entrySet().stream()
                                        .map(
                                                subEt ->
                                                        new FI3<>(
                                                                et.getKey(),
                                                                subEt.getKey(),
                                                                subEt.getValue()))
                                        .collect(toList())
                                        .stream())
                .collect(toList());
    }

    public static <K, J, H, V> List<FI4<K, J, H, V>> toListFI4( Map<K, Map<J, Map<H, V>>> map ) {
        return map.entrySet().stream()
                .flatMap(
                        et ->
                                et.getValue().entrySet().stream()
                                        .flatMap(
                                                subEt ->
                                                        subEt.getValue().entrySet().stream()
                                                                .map(
                                                                        sub2Et ->
                                                                                new FI4<>(
                                                                                        et.getKey(),
                                                                                        subEt
                                                                                                .getKey(),
                                                                                        sub2Et
                                                                                                .getKey(),
                                                                                        sub2Et
                                                                                                .getValue()))
                                                                .collect(toList())
                                                                .stream())
                                        .collect(toList())
                                        .stream())
                .collect(toList());
    }
}
