/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  org.apache.commons.collections4.CollectionUtils
 */
package com.kuma.boot.web.support.enums.mvc;

import com.kuma.boot.common.enums.base.CommonEnum;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;

public class CommonEnumVO {
    private int code;
    private String name;
    private String desc;

    public CommonEnumVO() {
    }

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
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

