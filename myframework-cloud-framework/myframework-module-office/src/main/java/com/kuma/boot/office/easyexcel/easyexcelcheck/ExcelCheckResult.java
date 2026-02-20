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

package com.kuma.boot.office.easyexcel.easyexcelcheck;

import java.util.ArrayList;
import java.util.List;

public class ExcelCheckResult<T> {

    private List<T> successDtos;

    private List<com.kuma.boot.office.easyexcel.easyexcelcheck.ExcelCheckErrDto<T>> errDtos;

    public ExcelCheckResult(List<T> successDtos, List<com.kuma.boot.office.easyexcel.easyexcelcheck.ExcelCheckErrDto<T>> errDtos) {
        this.successDtos = successDtos;
        this.errDtos = errDtos;
    }

    public ExcelCheckResult(List<com.kuma.boot.office.easyexcel.easyexcelcheck.ExcelCheckErrDto<T>> errDtos) {
        this.successDtos = new ArrayList<>();
        this.errDtos = errDtos;
    }

    public List<T> getSuccessDtos() {
        return successDtos;
    }

    public void setSuccessDtos(List<T> successDtos) {
        this.successDtos = successDtos;
    }

    public List<com.kuma.boot.office.easyexcel.easyexcelcheck.ExcelCheckErrDto<T>> getErrDtos() {
        return errDtos;
    }

    public void setErrDtos(List<com.kuma.boot.office.easyexcel.easyexcelcheck.ExcelCheckErrDto<T>> errDtos) {
        this.errDtos = errDtos;
    }
}
