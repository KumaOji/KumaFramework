/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  com.kuma.boot.common.model.result.Result
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.kuma.boot.web.support.enums.mvc.dict;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.web.support.enums.mvc.CommonEnumRegistry;
import com.kuma.boot.web.support.enums.mvc.CommonEnumVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/enumDict"})
public class EnumDictController {
    @Autowired
    private CommonEnumRegistry commonEnumRegistry;

    @GetMapping(value={"all"})
    public Result<Map<String, List<CommonEnumVO>>> allEnums() {
        Map<String, List<CommonEnum>> dict = this.commonEnumRegistry.getNameDict();
        HashMap dictVo = Maps.newHashMapWithExpectedSize((int)dict.size());
        for (Map.Entry<String, List<CommonEnum>> entry : dict.entrySet()) {
            dictVo.put(entry.getKey(), CommonEnumVO.from(entry.getValue()));
        }
        return Result.success((Object)dictVo);
    }

    @GetMapping(value={"types"})
    public Result<List<String>> enumTypes() {
        Map<String, List<CommonEnum>> dict = this.commonEnumRegistry.getNameDict();
        return Result.success((Object)Lists.newArrayList(dict.keySet()));
    }

    @GetMapping(value={"/{type}"})
    public Result<List<CommonEnumVO>> dictByType(@PathVariable(value="type") String type) {
        Map<String, List<CommonEnum>> dict = this.commonEnumRegistry.getNameDict();
        List<CommonEnum> commonEnums = dict.get(type);
        return Result.success(CommonEnumVO.from(commonEnums));
    }
}

