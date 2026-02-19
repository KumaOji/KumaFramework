/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.javers.comparator;

import com.kuma.boot.data.mybatis.javers.CustomerLabel;
import org.javers.core.diff.custom.CustomValueComparator;

/**
 * CustomerLabelComparator
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class CustomerLabelComparator implements CustomValueComparator<CustomerLabel> {

    @Override
    public boolean equals( CustomerLabel a, CustomerLabel b ) {
        return a.getId().equals(b.getId());
    }

    @Override
    public String toString( CustomerLabel value ) {
        return value.getLabelName();
    }
}
