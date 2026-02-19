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

package com.kuma.boot.data.mybatis.mybatisplus.injector.methods;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import java.util.function.Predicate;

/** 修改所有的字段 */
public class UpdateAllById extends AlwaysUpdateSomeColumnById {

    public UpdateAllById(Predicate<TableFieldInfo> predicate) {
        super(predicate);
    }

    // @Override
    // public String getMethod(SqlMethod sqlMethod) {
    //	// 自定义 mapper 方法名
    //	return "updateAllById";
    // }

}
