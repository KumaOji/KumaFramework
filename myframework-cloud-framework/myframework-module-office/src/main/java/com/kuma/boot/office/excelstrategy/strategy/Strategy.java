/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.poi.ss.usermodel.Workbook
 */
package com.kuma.boot.office.excelstrategy.strategy;

import java.io.InputStream;
import org.apache.poi.ss.usermodel.Workbook;

public interface Strategy {
    public Workbook version(String var1, InputStream var2, Workbook var3) throws Exception;
}

