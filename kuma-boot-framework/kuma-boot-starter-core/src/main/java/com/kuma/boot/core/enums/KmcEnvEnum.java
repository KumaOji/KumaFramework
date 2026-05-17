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

package com.kuma.boot.core.enums;

import com.kuma.boot.core.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.log.LogUtils;

import static com.kuma.boot.common.constant.CommonConstants.ACTIVE_PROFILES_ACTIVE;
import static com.kuma.boot.common.constant.CommonConstants.ACTIVE_PROFILES_PROPERTY;
import static com.kuma.boot.common.constant.CommonConstants.ENV;
import static com.kuma.boot.common.constant.CommonConstants.KMC_ENV;

/**
 * EnvironmentEnum
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:23:43
 */
public enum KmcEnvEnum {
    DEV("DEV", "开发环境"),
    TEST("TEST", "测试环境"),
    PRE("PRE", "预生产环境"),
    PRO("PRO", "生产环境"),
    KUBERNETES("KUBERNETES", "生产环境-kubernetes"),
    DOCKER("DOCKER", "生产环境-docker"),
    UNKNOWN("UNKNOWN", "未知");

    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    KmcEnvEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    private static KmcEnvEnum currEnv = null;
    private static String currEnvStr = null;

    public static KmcEnvEnum getCurrEnv() {
        if (currEnv == null) {
            getEnv();
        }
        return currEnv;
    }

    public static String getEnv() {
        if (currEnvStr == null) {
            currEnvStr = System.getProperty(ACTIVE_PROFILES_PROPERTY);
            if (currEnvStr == null) {
                currEnvStr = System.getProperty(ENV);
                if (currEnvStr == null) {
                    currEnvStr = System.getProperty(ACTIVE_PROFILES_ACTIVE);
                    if (currEnvStr == null) {
                        currEnvStr = System.getProperty(KMC_ENV);
                    }
                }

            }
            if (currEnvStr != null) {
                try {
                    PropertyUtils.setProperty(ACTIVE_PROFILES_PROPERTY, currEnvStr, "");
                    currEnv = KmcEnvEnum.valueOf(currEnvStr.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    LogUtils.error("环境配置错误");
                    currEnv = UNKNOWN;
                }
            }
            else {
                //throw new BusinessException("需要配置系统环境变量spring.profiles.active");
                LogUtils.error("需要配置系统环境变量spring.profiles.active");
            }

        }
        return currEnvStr;
    }

    public static boolean isDev() {
        return is(KmcEnvEnum.DEV);
    }

    public static boolean isTest() {
        return is(KmcEnvEnum.TEST);
    }

    public static boolean isPre() {
        return is(KmcEnvEnum.PRE);
    }

    public static boolean isPro() {
        return is(KmcEnvEnum.PRO);
    }

    public static boolean is(KmcEnvEnum envEnum) {
        if (envEnum == null) {
            throw new IllegalArgumentException("env不能为null");
        }
        if (currEnv == null) {
            getEnv();
        }
        return envEnum == currEnv;
    }


}
