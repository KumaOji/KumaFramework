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

package com.kuma.boot.dingtalk.entity;

/**
 * DingerMethod
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:19:28
 */
public class DingerMethod {

    String methodName;
    String[] methodParams;
    int[] paramTypes;

    public DingerMethod(String methodName, String[] methodParams, int[] paramTypes) {
        this.methodName = methodName;
        this.methodParams = methodParams;
        this.paramTypes = paramTypes;
    }

    public boolean check() {
        if (paramTypes == null) {
            return false;
        }

        int length = this.methodParams.length;
        for (int index : paramTypes) {
            if (index >= length) {
                return true;
            }
        }
        return false;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getMethodParams() {
        return methodParams;
    }

    public int[] getParamTypes() {
        return paramTypes;
    }
}
