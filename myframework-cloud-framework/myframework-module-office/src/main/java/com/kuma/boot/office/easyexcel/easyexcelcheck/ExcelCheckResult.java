/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.easyexcel.easyexcelcheck;

import java.util.ArrayList;
import java.util.List;

public class ExcelCheckResult<T> {
    private List<T> successDtos;
    private List<ExcelCheckErrDto<T>> errDtos;

    public ExcelCheckResult(List<T> successDtos, List<ExcelCheckErrDto<T>> errDtos) {
        this.successDtos = successDtos;
        this.errDtos = errDtos;
    }

    public ExcelCheckResult(List<ExcelCheckErrDto<T>> errDtos) {
        this.successDtos = new ArrayList<T>();
        this.errDtos = errDtos;
    }

    public List<T> getSuccessDtos() {
        return this.successDtos;
    }

    public void setSuccessDtos(List<T> successDtos) {
        this.successDtos = successDtos;
    }

    public List<ExcelCheckErrDto<T>> getErrDtos() {
        return this.errDtos;
    }

    public void setErrDtos(List<ExcelCheckErrDto<T>> errDtos) {
        this.errDtos = errDtos;
    }
}

