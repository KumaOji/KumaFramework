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

package com.kuma.boot.common.extension.wrapper;

import com.kuma.boot.common.extension.Wrapper;
import com.kuma.boot.common.extension.active.ActivateComparator;
import java.util.Comparator;

/**
 * 针对 @Wrapper 排序
 *
 * @see Wrapper
 */
public class WrapperComparator extends ActivateComparator {

    public static final Comparator<Object> COMPARATOR = new WrapperComparator();
}
