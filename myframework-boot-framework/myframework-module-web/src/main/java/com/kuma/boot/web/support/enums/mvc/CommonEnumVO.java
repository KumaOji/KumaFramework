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

package com.kuma.boot.web.support.enums.mvc;

import com.kuma.boot.common.enums.base.CommonEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** 通用枚举 */
public class CommonEnumVO {

    private int code;

    private String name;

    private String desc;

    public CommonEnumVO() {}

    public CommonEnumVO(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static CommonEnumVO from(CommonEnum commonEnum) {
        if (commonEnum == null) {
            return null;
        }
        return new CommonEnumVO(commonEnum.getCode(), commonEnum.getName(), commonEnum.getDesc());
    }

    public static List<CommonEnumVO> from(List<CommonEnum> commonEnums) {
        if (CollectionUtils.isEmpty(commonEnums)) {
            return Collections.emptyList();
        }
        return commonEnums.stream().filter(Objects::nonNull).map(CommonEnumVO::from).toList();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
