/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.poi.ss.usermodel.Workbook
 */
package com.kuma.boot.office.excelstrategy.strategy;

import java.io.InputStream;
import org.apache.poi.ss.usermodel.Workbook;

public class Context {
    Strategy strategy;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public Workbook executeStrategy(String fileType, InputStream is, Workbook wb) throws Exception {
        return this.strategy.version(fileType, is, wb);
    }
}

