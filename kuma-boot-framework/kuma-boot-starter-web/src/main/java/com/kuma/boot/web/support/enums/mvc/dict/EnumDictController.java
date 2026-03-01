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

package com.kuma.boot.web.support.enums.mvc.dict;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.web.support.enums.mvc.CommonEnumRegistry;
import com.kuma.boot.web.support.enums.mvc.CommonEnumVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** 通用字典接口 */
@RestController
@RequestMapping("/enumDict")
public class EnumDictController {

    @Autowired private CommonEnumRegistry commonEnumRegistry;

    @GetMapping("all")
    public Result<Map<String, List<CommonEnumVO>>> allEnums() {
        Map<String, List<CommonEnum>> dict = this.commonEnumRegistry.getNameDict();
        Map<String, List<CommonEnumVO>> dictVo = Maps.newHashMapWithExpectedSize(dict.size());
        for (Map.Entry<String, List<CommonEnum>> entry : dict.entrySet()) {
            dictVo.put(entry.getKey(), CommonEnumVO.from(entry.getValue()));
        }
        return Result.success(dictVo);
    }

    @GetMapping("types")
    public Result<List<String>> enumTypes() {
        Map<String, List<CommonEnum>> dict = this.commonEnumRegistry.getNameDict();
        return Result.success(Lists.newArrayList(dict.keySet()));
    }

    @GetMapping("/{type}")
    public Result<List<CommonEnumVO>> dictByType(@PathVariable("type") String type) {
        Map<String, List<CommonEnum>> dict = this.commonEnumRegistry.getNameDict();
        List<CommonEnum> commonEnums = dict.get(type);

        return Result.success(CommonEnumVO.from(commonEnums));
    }
}
