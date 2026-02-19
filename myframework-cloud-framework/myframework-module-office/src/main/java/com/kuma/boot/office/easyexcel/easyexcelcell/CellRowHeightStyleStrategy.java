/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy
 *  org.apache.poi.ss.usermodel.Row
 */
package com.kuma.boot.office.easyexcel.easyexcelcell;

import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Row;

public class CellRowHeightStyleStrategy
extends AbstractRowHeightStyleStrategy {
    protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
        if (relativeRowIndex == 0) {
            row.setHeight((short)3240);
        }
    }

    protected void setContentColumnHeight(Row row, int relativeRowIndex) {
    }
}

