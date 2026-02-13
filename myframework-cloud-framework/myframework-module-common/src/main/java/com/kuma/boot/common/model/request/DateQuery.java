/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.NotNull
 *  org.springframework.format.annotation.DateTimeFormat
 */
package com.kuma.boot.common.model.request;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class DateQuery
implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}

