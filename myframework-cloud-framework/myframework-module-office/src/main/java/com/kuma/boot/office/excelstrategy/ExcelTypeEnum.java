/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.excelstrategy;

public enum ExcelTypeEnum {
    EXCEL_THREE(1, "xls"),
    EXCEL_SEVEN(2, "xlsx");

    private Integer key;
    private String text;

    private ExcelTypeEnum(Integer key, String text) {
        this.key = key;
        this.text = text;
    }

    public Integer getKey() {
        return this.key;
    }

    public String getText() {
        return this.text;
    }

    public static ExcelTypeEnum getText(Integer key) {
        for (ExcelTypeEnum typeEnum : ExcelTypeEnum.values()) {
            if (!typeEnum.key.equals(key)) continue;
            return typeEnum;
        }
        throw new IllegalArgumentException("No element matches " + key);
    }
}

