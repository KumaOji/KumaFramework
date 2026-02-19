/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.convert.Convert
 *  cn.hutool.core.util.ObjUtil
 *  com.alibaba.excel.converters.Converter
 *  com.alibaba.excel.enums.CellDataTypeEnum
 *  com.alibaba.excel.metadata.GlobalConfiguration
 *  com.alibaba.excel.metadata.data.ReadCellData
 *  com.alibaba.excel.metadata.data.WriteCellData
 *  com.alibaba.excel.metadata.property.ExcelContentProperty
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.convert;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import java.math.BigDecimal;

public class ExcelBigNumberConvert
implements Converter<Long> {
    public Class<Long> supportJavaTypeKey() {
        return Long.class;
    }

    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    public Long convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return Convert.toLong((Object)cellData.getData());
    }

    public WriteCellData<Object> convertToExcelData(Long object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String str;
        if (ObjUtil.isNotNull((Object)object) && (str = Convert.toStr((Object)object)).length() > 15) {
            return new WriteCellData(str);
        }
        WriteCellData cellData = new WriteCellData(new BigDecimal(object));
        cellData.setType(CellDataTypeEnum.NUMBER);
        return cellData;
    }
}

